package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.account.DebitCreditType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 貸借区分のTypeHandler.
 */
public class DebitCreditTypeHandler extends BaseTypeHandler<DebitCreditType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DebitCreditType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public DebitCreditType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : DebitCreditType.fromDisplayName(value);
    }

    @Override
    public DebitCreditType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : DebitCreditType.fromDisplayName(value);
    }

    @Override
    public DebitCreditType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : DebitCreditType.fromDisplayName(value);
    }
}
