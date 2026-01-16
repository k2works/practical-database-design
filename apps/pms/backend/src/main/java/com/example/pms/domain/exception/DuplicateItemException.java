package com.example.pms.domain.exception;

/**
 * 品目コード重複例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class DuplicateItemException extends DomainException {

    public DuplicateItemException(String itemCode) {
        super("品目コードが既に存在します: " + itemCode);
    }
}
