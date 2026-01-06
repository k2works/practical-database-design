package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.account.TransactionElementType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 取引要素区分のTypeHandler.
 */
public class TransactionElementTypeHandler extends BaseTypeHandler<TransactionElementType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TransactionElementType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public TransactionElementType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : TransactionElementType.fromDisplayName(value);
    }

    @Override
    public TransactionElementType getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : TransactionElementType.fromDisplayName(value);
    }

    @Override
    public TransactionElementType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : TransactionElementType.fromDisplayName(value);
    }
}
