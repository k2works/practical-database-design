package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.partner.PaymentMethod;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 支払方法 TypeHandler.
 */
@MappedTypes(PaymentMethod.class)
public class PaymentMethodTypeHandler extends BaseTypeHandler<PaymentMethod> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PaymentMethod parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public PaymentMethod getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : PaymentMethod.fromDisplayName(value);
    }

    @Override
    public PaymentMethod getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : PaymentMethod.fromDisplayName(value);
    }

    @Override
    public PaymentMethod getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : PaymentMethod.fromDisplayName(value);
    }
}
