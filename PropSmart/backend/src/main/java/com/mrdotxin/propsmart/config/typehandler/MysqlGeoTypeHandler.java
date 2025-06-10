package com.mrdotxin.propsmart.config.typehandler;

import com.mrdotxin.propsmart.model.geo.GeoPoint;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * MySQL GEOMETRY类型的TypeHandler
 * 用于在GeoPoint对象和MySQL GEOMETRY类型之间进行转换
 */
@MappedTypes(GeoPoint.class)
public class MysqlGeoTypeHandler extends BaseTypeHandler<GeoPoint> {

    /**
     * 设置非空参数到PreparedStatement
     * @param ps PreparedStatement对象
     * @param i 参数索引
     * @param parameter GeoPoint参数
     * @param jdbcType JDBC类型
     * @throws SQLException SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GeoPoint parameter, JdbcType jdbcType) throws SQLException {
            String wkt = parameter.toWkt();
            try (Statement stmt = ps.getConnection().createStatement()) {
                // 创建一个临时变量存储几何对象
                stmt.execute("SET @geom = ST_GeomFromText('" + wkt + "')");

                // 使用临时变量设置参数
                try (PreparedStatement pstmt = ps.getConnection().prepareStatement(
                        "SELECT @geom")) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            // 从结果集中获取几何对象并设置到主PreparedStatement
                            ps.setObject(i, rs.getObject(1), Types.OTHER);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new SQLException("Error setting geometry parameter", e);
            }
    }

    /**
     * 通过列名从ResultSet获取可空结果
     * @param rs ResultSet对象
     * @param columnName 列名
     * @return GeoPoint对象
     * @throws SQLException SQL异常
     */
    @Override
    public GeoPoint getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String wktValue = getWktFromResultSet(rs, columnName);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }

    /**
     * 通过列索引从ResultSet获取可空结果
     * @param rs ResultSet对象
     * @param columnIndex 列索引
     * @return GeoPoint对象
     * @throws SQLException SQL异常
     */
    @Override
    public GeoPoint getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String wktValue = getWktFromResultSet(rs, columnIndex);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }

    /**
     * 从CallableStatement获取可空结果
     * @param cs CallableStatement对象
     * @param columnIndex 列索引
     * @return GeoPoint对象
     * @throws SQLException SQL异常
     */
    @Override
    public GeoPoint getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String wktValue = cs.getString(columnIndex);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }

    /**
     * 从ResultSet获取WKT格式字符串(通过列名)
     * @param rs ResultSet对象
     * @param columnName 列名
     * @return WKT格式字符串
     * @throws SQLException SQL异常
     */
    private String getWktFromResultSet(ResultSet rs, String columnName) throws SQLException {
        try {
            // 对于MySQL 5.7+，使用ST_AsText将GEOMETRY转换为WKT
            return rs.getString("ST_AsText(" + columnName + ")");
        } catch (SQLException e) {
            // 回退到常规getString方法
            return rs.getString(columnName);
        }
    }

    /**
     * 从ResultSet获取WKT格式字符串(通过列索引)
     * @param rs ResultSet对象
     * @param columnIndex 列索引
     * @return WKT格式字符串
     * @throws SQLException SQL异常
     */
    private String getWktFromResultSet(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }
}