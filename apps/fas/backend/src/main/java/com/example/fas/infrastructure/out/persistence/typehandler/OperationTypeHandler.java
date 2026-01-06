package com.example.fas.infrastructure.out.persistence.typehandler;

import com.example.fas.domain.model.audit.ChangeLog.OperationType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * 操作種別 TypeHandler.
 * PostgreSQL ENUM（操作種別） ↔ Java Enum（OperationType）の変換
 */
@MappedTypes(OperationType.class)
public class OperationTypeHandler extends BaseTypeHandler<OperationType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
            OperationType parameter, JdbcType jdbcType) throws SQLException {
        // Java Enum → PostgreSQL ENUM
        ps.setObject(i, parameter.name(), Types.OTHER);
    }

    @Override
    public OperationType getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : OperationType.valueOf(value);
    }

    @Override
    public OperationType getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : OperationType.valueOf(value);
    }

    @Override
    public OperationType getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : OperationType.valueOf(value);
    }
}
