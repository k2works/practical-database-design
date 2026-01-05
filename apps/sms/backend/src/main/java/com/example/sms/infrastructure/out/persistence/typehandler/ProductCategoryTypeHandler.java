package com.example.sms.infrastructure.out.persistence.typehandler;

import com.example.sms.domain.model.product.ProductCategory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 商品区分 TypeHandler.
 */
@MappedTypes(ProductCategory.class)
public class ProductCategoryTypeHandler extends BaseTypeHandler<ProductCategory> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductCategory parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.getDisplayName());
    }

    @Override
    public ProductCategory getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : ProductCategory.fromDisplayName(value);
    }

    @Override
    public ProductCategory getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : ProductCategory.fromDisplayName(value);
    }

    @Override
    public ProductCategory getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : ProductCategory.fromDisplayName(value);
    }
}
