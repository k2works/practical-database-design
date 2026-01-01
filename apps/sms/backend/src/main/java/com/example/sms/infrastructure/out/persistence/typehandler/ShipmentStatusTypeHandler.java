package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.shipping.ShipmentStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 出荷ステータス TypeHandler.
 */
@MappedTypes(ShipmentStatus.class)
public class ShipmentStatusTypeHandler extends BaseTypeHandler<ShipmentStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ShipmentStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public ShipmentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ShipmentStatus.fromDisplayName(value);
    }

    @Override
    public ShipmentStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ShipmentStatus.fromDisplayName(value);
    }

    @Override
    public ShipmentStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ShipmentStatus.fromDisplayName(value);
    }
}
