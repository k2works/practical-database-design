package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.inventory.WarehouseType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 倉庫区分 TypeHandler.
 */
@MappedTypes(WarehouseType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class WarehouseTypeTypeHandler extends BaseTypeHandler<WarehouseType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, WarehouseType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public WarehouseType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : WarehouseType.fromDisplayName(value);
    }

    @Override
    public WarehouseType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : WarehouseType.fromDisplayName(value);
    }

    @Override
    public WarehouseType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : WarehouseType.fromDisplayName(value);
    }
}
