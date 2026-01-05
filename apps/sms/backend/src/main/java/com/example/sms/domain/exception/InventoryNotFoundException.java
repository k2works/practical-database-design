package com.example.sms.domain.exception;

/**
 * 在庫が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class InventoryNotFoundException extends ResourceNotFoundException {

    public InventoryNotFoundException(Integer id) {
        super("在庫が見つかりません: ID=" + id);
    }

    public InventoryNotFoundException(String warehouseCode, String productCode) {
        super("在庫が見つかりません: 倉庫=" + warehouseCode + ", 商品=" + productCode);
    }
}
