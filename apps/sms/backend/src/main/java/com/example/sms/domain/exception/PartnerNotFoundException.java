package com.example.sms.domain.exception;

/**
 * 取引先が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class PartnerNotFoundException extends ResourceNotFoundException {

    public PartnerNotFoundException(String partnerCode) {
        super("取引先が見つかりません: " + partnerCode);
    }
}
