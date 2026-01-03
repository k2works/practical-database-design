package com.example.sms.domain.exception;

/**
 * 倉庫が重複している場合の例外.
 */
public class DuplicateWarehouseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateWarehouseException(String warehouseCode) {
        super("倉庫が既に存在します: " + warehouseCode);
    }
}
