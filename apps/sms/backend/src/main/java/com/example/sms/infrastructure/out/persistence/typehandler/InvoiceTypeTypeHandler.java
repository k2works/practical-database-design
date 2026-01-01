package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.invoice.InvoiceType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 請求区分 TypeHandler.
 */
@MappedTypes(InvoiceType.class)
public class InvoiceTypeTypeHandler extends BaseTypeHandler<InvoiceType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, InvoiceType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public InvoiceType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : InvoiceType.fromDisplayName(value);
    }

    @Override
    public InvoiceType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : InvoiceType.fromDisplayName(value);
    }

    @Override
    public InvoiceType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : InvoiceType.fromDisplayName(value);
    }
}
