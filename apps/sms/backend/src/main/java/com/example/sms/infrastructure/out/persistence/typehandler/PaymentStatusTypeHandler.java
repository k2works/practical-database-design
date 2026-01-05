package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.payment.PaymentStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 支払ステータス TypeHandler.
 */
@MappedTypes(PaymentStatus.class)
public class PaymentStatusTypeHandler extends BaseTypeHandler<PaymentStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                     PaymentStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public PaymentStatus getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : PaymentStatus.fromDisplayName(value);
    }

    @Override
    public PaymentStatus getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : PaymentStatus.fromDisplayName(value);
    }

    @Override
    public PaymentStatus getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : PaymentStatus.fromDisplayName(value);
    }
}
