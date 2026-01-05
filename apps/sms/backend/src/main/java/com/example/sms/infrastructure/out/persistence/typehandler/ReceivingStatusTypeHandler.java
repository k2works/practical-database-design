package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.purchase.ReceivingStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 入荷ステータス TypeHandler.
 */
@MappedTypes(ReceivingStatus.class)
public class ReceivingStatusTypeHandler extends BaseTypeHandler<ReceivingStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReceivingStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public ReceivingStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ReceivingStatus.fromDisplayName(value);
    }

    @Override
    public ReceivingStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ReceivingStatus.fromDisplayName(value);
    }

    @Override
    public ReceivingStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ReceivingStatus.fromDisplayName(value);
    }
}
