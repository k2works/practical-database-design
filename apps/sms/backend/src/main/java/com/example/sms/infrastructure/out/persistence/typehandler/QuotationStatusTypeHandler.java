package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.sales.QuotationStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 見積ステータス TypeHandler.
 */
@MappedTypes(QuotationStatus.class)
public class QuotationStatusTypeHandler extends BaseTypeHandler<QuotationStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, QuotationStatus parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public QuotationStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : QuotationStatus.fromDisplayName(value);
    }

    @Override
    public QuotationStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : QuotationStatus.fromDisplayName(value);
    }

    @Override
    public QuotationStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : QuotationStatus.fromDisplayName(value);
    }
}
