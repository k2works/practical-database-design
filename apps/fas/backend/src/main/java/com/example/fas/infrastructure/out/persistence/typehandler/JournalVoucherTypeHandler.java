package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.journal.JournalVoucherType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 仕訳伝票区分のTypeHandler.
 */
public class JournalVoucherTypeHandler extends BaseTypeHandler<JournalVoucherType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JournalVoucherType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public JournalVoucherType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : JournalVoucherType.fromDisplayName(value);
    }

    @Override
    public JournalVoucherType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : JournalVoucherType.fromDisplayName(value);
    }

    @Override
    public JournalVoucherType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : JournalVoucherType.fromDisplayName(value);
    }
}
