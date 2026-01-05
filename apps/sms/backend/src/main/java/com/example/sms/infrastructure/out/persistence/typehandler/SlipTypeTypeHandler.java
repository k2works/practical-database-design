package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.common.SlipType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 伝票区分 TypeHandler.
 */
@MappedTypes(SlipType.class)
public class SlipTypeTypeHandler extends BaseTypeHandler<SlipType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, SlipType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public SlipType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : SlipType.fromDisplayName(value);
    }

    @Override
    public SlipType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : SlipType.fromDisplayName(value);
    }

    @Override
    public SlipType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : SlipType.fromDisplayName(value);
    }
}
