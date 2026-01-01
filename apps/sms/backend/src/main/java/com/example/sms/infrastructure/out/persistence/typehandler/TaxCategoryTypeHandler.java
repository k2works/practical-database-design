package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.product.TaxCategory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 税区分 TypeHandler.
 */
@MappedTypes(TaxCategory.class)
public class TaxCategoryTypeHandler extends BaseTypeHandler<TaxCategory> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TaxCategory parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public TaxCategory getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : TaxCategory.fromDisplayName(value);
    }

    @Override
    public TaxCategory getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : TaxCategory.fromDisplayName(value);
    }

    @Override
    public TaxCategory getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : TaxCategory.fromDisplayName(value);
    }
}
