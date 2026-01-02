package com.example.sms.domain.exception;

/**
 * 出荷が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class ShipmentNotFoundException extends ResourceNotFoundException {

    public ShipmentNotFoundException(String shipmentNumber) {
        super("出荷が見つかりません: " + shipmentNumber);
    }

    public ShipmentNotFoundException(Integer id) {
        super("出荷が見つかりません: ID=" + id);
    }
}
