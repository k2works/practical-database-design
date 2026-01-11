package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.account.AggregationType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 集計区分のTypeHandler.
 */
public class AggregationTypeHandler extends BaseTypeHandler<AggregationType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AggregationType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public AggregationType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : AggregationType.fromDisplayName(value);
    }

    @Override
    public AggregationType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : AggregationType.fromDisplayName(value);
    }

    @Override
    public AggregationType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : AggregationType.fromDisplayName(value);
    }
}
