package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.sales.SalesStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 売上ステータス TypeHandler.
 */
@MappedTypes(SalesStatus.class)
public class SalesStatusTypeHandler extends BaseTypeHandler<SalesStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SalesStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : SalesStatus.fromDisplayName(value);
    }

    @Override
    public SalesStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : SalesStatus.fromDisplayName(value);
    }

    @Override
    public SalesStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : SalesStatus.fromDisplayName(value);
    }
}
