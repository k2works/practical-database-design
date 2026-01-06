package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.account.BSPLType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * BSPL区分のTypeHandler.
 */
public class BSPLTypeHandler extends BaseTypeHandler<BSPLType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BSPLType parameter,
            JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public BSPLType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : BSPLType.fromDisplayName(value);
    }

    @Override
    public BSPLType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : BSPLType.fromDisplayName(value);
    }

    @Override
    public BSPLType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : BSPLType.fromDisplayName(value);
    }
}
