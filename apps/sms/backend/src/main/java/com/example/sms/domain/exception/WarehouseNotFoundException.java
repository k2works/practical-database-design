package com.example.sms.domain.exception;

/**
 * 倉庫が見つからない場合の例外.
 */
public class WarehouseNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public WarehouseNotFoundException(String warehouseCode) {
        super("倉庫が見つかりません: " + warehouseCode);
    }
}
