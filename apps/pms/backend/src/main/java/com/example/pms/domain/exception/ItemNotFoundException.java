package com.example.pms.domain.exception;

/**
 * 品目が見つからない例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ItemNotFoundException extends DomainException {

    public ItemNotFoundException(String itemCode) {
        super("品目が見つかりません: " + itemCode);
    }
}
