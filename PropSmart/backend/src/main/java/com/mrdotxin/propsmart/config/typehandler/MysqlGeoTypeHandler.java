package com.mrdotxin.propsmart.config.typehandler;

import com.mrdotxin.propsmart.model.geo.GeoPoint;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * Type handler for MySQL GEOMETRY type
 * Converts between GeoPoint and MySQL GEOMETRY
 */
@MappedTypes(GeoPoint.class)
public class MysqlGeoTypeHandler extends BaseTypeHandler<GeoPoint> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GeoPoint parameter, JdbcType jdbcType) throws SQLException {
        String wkt = parameter.toWkt();
        ps.setString(i, String.format("ST_GeomFromText('%s')", wkt));
    }

    @Override
    public GeoPoint getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String wktValue = getWktFromResultSet(rs, columnName);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }

    @Override
    public GeoPoint getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String wktValue = getWktFromResultSet(rs, columnIndex);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }

    @Override
    public GeoPoint getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String wktValue = cs.getString(columnIndex);
        return wktValue != null ? GeoPoint.fromWkt(wktValue) : null;
    }
    
    private String getWktFromResultSet(ResultSet rs, String columnName) throws SQLException {
        try {
            // For MySQL 5.7+, use ST_AsText to convert GEOMETRY to WKT
            return rs.getString("ST_AsText(" + columnName + ")");
        } catch (SQLException e) {
            // Fallback to regular getString
            return rs.getString(columnName);
        }
    }
    
    private String getWktFromResultSet(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }
} 