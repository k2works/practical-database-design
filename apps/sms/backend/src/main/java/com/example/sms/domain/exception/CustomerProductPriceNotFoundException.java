package com.example.sms.domain.exception;

/**
 * 顧客別販売単価が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class CustomerProductPriceNotFoundException extends ResourceNotFoundException {

    public CustomerProductPriceNotFoundException(String productCode, String partnerCode, String startDate) {
        super("顧客別販売単価が見つかりません: " + productCode + "-" + partnerCode + "-" + startDate);
    }
}
