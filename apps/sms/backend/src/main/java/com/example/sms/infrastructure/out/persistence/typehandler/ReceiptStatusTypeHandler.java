package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.receipt.ReceiptStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 入金ステータス TypeHandler.
 */
@MappedTypes(ReceiptStatus.class)
public class ReceiptStatusTypeHandler extends BaseTypeHandler<ReceiptStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReceiptStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public ReceiptStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ReceiptStatus.fromDisplayName(value);
    }

    @Override
    public ReceiptStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ReceiptStatus.fromDisplayName(value);
    }

    @Override
    public ReceiptStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ReceiptStatus.fromDisplayName(value);
    }
}
