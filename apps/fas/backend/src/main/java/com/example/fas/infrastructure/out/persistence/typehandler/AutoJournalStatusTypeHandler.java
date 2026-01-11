package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.autojournal.AutoJournalStatus;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * 自動仕訳ステータスの TypeHandler.
 */
public class AutoJournalStatusTypeHandler extends BaseTypeHandler<AutoJournalStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AutoJournalStatus parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public AutoJournalStatus getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : AutoJournalStatus.fromDisplayName(value);
    }

    @Override
    public AutoJournalStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : AutoJournalStatus.fromDisplayName(value);
    }

    @Override
    public AutoJournalStatus getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : AutoJournalStatus.fromDisplayName(value);
    }
}
