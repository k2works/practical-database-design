package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.invoice.InvoiceStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 請求ステータス TypeHandler.
 */
@MappedTypes(InvoiceStatus.class)
public class InvoiceStatusTypeHandler extends BaseTypeHandler<InvoiceStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, InvoiceStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public InvoiceStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : InvoiceStatus.fromDisplayName(value);
    }

    @Override
    public InvoiceStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : InvoiceStatus.fromDisplayName(value);
    }

    @Override
    public InvoiceStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : InvoiceStatus.fromDisplayName(value);
    }
}
