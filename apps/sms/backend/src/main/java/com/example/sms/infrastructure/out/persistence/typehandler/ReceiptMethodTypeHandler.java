package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.receipt.ReceiptMethod;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 入金方法 TypeHandler.
 */
@MappedTypes(ReceiptMethod.class)
public class ReceiptMethodTypeHandler extends BaseTypeHandler<ReceiptMethod> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ReceiptMethod parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public ReceiptMethod getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ReceiptMethod.fromDisplayName(value);
    }

    @Override
    public ReceiptMethod getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ReceiptMethod.fromDisplayName(value);
    }

    @Override
    public ReceiptMethod getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ReceiptMethod.fromDisplayName(value);
    }
}
