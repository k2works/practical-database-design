package com.example.pms.domain.model.inventory;

import java.io.Serial;

/**
 * 在庫不足例外.
 */
public class InsufficientStockException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InsufficientStockException(String message) {
        super(message);
    }
}
