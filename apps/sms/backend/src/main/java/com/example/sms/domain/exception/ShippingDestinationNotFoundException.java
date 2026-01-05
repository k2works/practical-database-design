package com.example.sms.domain.exception;

/**
 * 出荷先が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ShippingDestinationNotFoundException extends ResourceNotFoundException {

    public ShippingDestinationNotFoundException(String partnerCode, String branchNumber, String shippingNumber) {
        super("出荷先が見つかりません: " + partnerCode + "-" + branchNumber + "-" + shippingNumber);
    }
}
