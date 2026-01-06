package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.journal.TaxType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 消費税区分のTypeHandler.
 */
public class TaxTypeHandler extends BaseTypeHandler<TaxType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TaxType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public TaxType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : TaxType.fromDisplayName(value);
    }

    @Override
    public TaxType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : TaxType.fromDisplayName(value);
    }

    @Override
    public TaxType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : TaxType.fromDisplayName(value);
    }
}
