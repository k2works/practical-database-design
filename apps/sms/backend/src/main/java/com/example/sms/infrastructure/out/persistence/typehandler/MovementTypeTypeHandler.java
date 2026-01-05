package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.inventory.MovementType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 移動区分 TypeHandler.
 */
@MappedTypes(MovementType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MovementTypeTypeHandler extends BaseTypeHandler<MovementType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MovementType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public MovementType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : MovementType.fromDisplayName(value);
    }

    @Override
    public MovementType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : MovementType.fromDisplayName(value);
    }

    @Override
    public MovementType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : MovementType.fromDisplayName(value);
    }
}
