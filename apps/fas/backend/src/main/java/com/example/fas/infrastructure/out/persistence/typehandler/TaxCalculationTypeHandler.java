package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.journal.TaxCalculationType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 消費税計算区分のTypeHandler.
 */
public class TaxCalculationTypeHandler extends BaseTypeHandler<TaxCalculationType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TaxCalculationType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public TaxCalculationType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : TaxCalculationType.fromDisplayName(value);
    }

    @Override
    public TaxCalculationType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : TaxCalculationType.fromDisplayName(value);
    }

    @Override
    public TaxCalculationType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : TaxCalculationType.fromDisplayName(value);
    }
}
