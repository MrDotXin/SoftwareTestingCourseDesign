package com.mrdotxin.propsmart.config.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final WKBReader wkbReader = new WKBReader(geometryFactory);
    private final WKBWriter wkbWriter = new WKBWriter();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            byte[] wkb = wkbWriter.write(parameter);
            ps.setBytes(i, wkb);
        } catch (Exception e) {
            throw new SQLException("Failed to convert Geometry to WKB", e);
        }
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] wkb = rs.getBytes(columnName);
        return parseWkbGeometry(wkb);
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] wkb = rs.getBytes(columnIndex);
        return parseWkbGeometry(wkb);
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] wkb = cs.getBytes(columnIndex);
        return parseWkbGeometry(wkb);
    }

    private Geometry parseWkbGeometry(byte[] wkb) throws SQLException {
        if (wkb == null || wkb.length == 0) {
            return null;
        }

        try {
            // 使用JTS的WKBReader解析
            return wkbReader.read(wkb);
        } catch (Exception e) {
            // 如果标准解析失败，尝试自定义解析
            return parseWkbManually(wkb);
        }
    }

    private Geometry parseWkbManually(byte[] wkb) throws SQLException {
        // 转换为十六进制字符串
        String hexStr = asciiBytesToHexString(wkb);
         byte[] wkbBytes = hexStringToByteArray(hexStr);

        ByteBuffer buffer = ByteBuffer.wrap(wkbBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // 读取字节顺序标记
        byte byteOrder = buffer.get();
        System.out.println("字节顺序: " + (byteOrder == 0 ? "Big Endian" : "Little Endian"));

        // 读取几何类型
        int geometryType = buffer.getInt();
        System.out.println("几何类型: " + getGeometryTypeName(geometryType));

        // 检查是否有SRID
        boolean hasSrid = (geometryType & 0x20000000) != 0;
        int srid = 0;
        if (hasSrid) {
            srid = buffer.getInt();
            geometryType = geometryType & 0x1FFFFFFF; // 移除SRID标志位
        }

        Geometry geometry;
        switch (geometryType) {
            case 1: // Point
                geometry = parsePoint(buffer);
                break;
            case 2: // LineString
                geometry = parseLineString(buffer);
                break;
            case 3: // Polygon
                geometry = parsePolygon(buffer);
                break;
            // 其他几何类型...
            default:
                throw new SQLException("Unsupported geometry type: " + geometryType);
        }

        if (hasSrid) {
            geometry.setSRID(srid);
        }

        return geometry;
    }

    private static String getGeometryTypeName(int type) {
        switch (type & 0x1FFFFFFF) { // 去掉SRID标志位
            case 1: return "Point";
            case 2: return "LineString";
            case 3: return "Polygon";
            case 4: return "MultiPoint";
            case 5: return "MultiLineString";
            case 6: return "MultiPolygon";
            case 7: return "GeometryCollection";
            default: return "Unknown";
        }
    }



    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    private static String asciiBytesToHexString(byte[] asciiBytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : asciiBytes) {
                sb.append((char) b);
            }
            return sb.toString();
        }

    private Point parsePoint(ByteBuffer buffer) {
        double x = buffer.getDouble();
        double y = buffer.getDouble();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    private LineString parseLineString(ByteBuffer buffer) {
        int numPoints = buffer.getInt();
        Coordinate[] coords = new Coordinate[numPoints];
        for (int i = 0; i < numPoints; i++) {
            coords[i] = new Coordinate(buffer.getDouble(), buffer.getDouble());
        }
        return geometryFactory.createLineString(coords);
    }

    private Polygon parsePolygon(ByteBuffer buffer) {
        int numRings = buffer.getInt();
        LinearRing shell = null;
        LinearRing[] holes = new LinearRing[numRings > 0 ? numRings - 1 : 0];

        for (int i = 0; i < numRings; i++) {
            int numPoints = buffer.getInt();
            Coordinate[] coords = new Coordinate[numPoints];
            for (int j = 0; j < numPoints; j++) {
                coords[j] = new Coordinate(buffer.getDouble(), buffer.getDouble());
            }

            if (i == 0) {
                shell = geometryFactory.createLinearRing(coords);
            } else {
                holes[i - 1] = geometryFactory.createLinearRing(coords);
            }
        }

        return geometryFactory.createPolygon(shell, holes);
    }

    private MultiPoint parseMultiPoint(ByteBuffer buffer) {
        int numPoints = buffer.getInt();
        Point[] points = new Point[numPoints];
        for (int i = 0; i < numPoints; i++) {
            points[i] = parsePoint(buffer);
        }
        return geometryFactory.createMultiPoint(points);
    }

    private MultiLineString parseMultiLineString(ByteBuffer buffer) {
        int numLineStrings = buffer.getInt();
        LineString[] lineStrings = new LineString[numLineStrings];
        for (int i = 0; i < numLineStrings; i++) {
            lineStrings[i] = parseLineString(buffer);
        }
        return geometryFactory.createMultiLineString(lineStrings);
    }

    private MultiPolygon parseMultiPolygon(ByteBuffer buffer) {
        int numPolygons = buffer.getInt();
        Polygon[] polygons = new Polygon[numPolygons];
        for (int i = 0; i < numPolygons; i++) {
            polygons[i] = parsePolygon(buffer);
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    private GeometryCollection parseGeometryCollection(ByteBuffer buffer) throws SQLException {
        int numGeometries = buffer.getInt();
        Geometry[] geometries = new Geometry[numGeometries];
        for (int i = 0; i < numGeometries; i++) {
            // 对于集合中的每个几何体，需要重新解析其WKB格式
            byte[] geomWkb = new byte[buffer.remaining()];
            buffer.get(geomWkb);
            geometries[i] = parseWkbManually(geomWkb);
        }
        return geometryFactory.createGeometryCollection(geometries);
    }
}