package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.partner.BillingType;
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
@MappedTypes(BillingType.class)
public class BillingTypeTypeHandler extends BaseTypeHandler<BillingType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BillingType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public BillingType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : BillingType.fromDisplayName(value);
    }

    @Override
    public BillingType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : BillingType.fromDisplayName(value);
    }

    @Override
    public BillingType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : BillingType.fromDisplayName(value);
    }
}
