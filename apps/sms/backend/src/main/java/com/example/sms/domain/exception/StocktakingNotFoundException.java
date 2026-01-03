package com.example.sms.domain.exception;

/**
 * 棚卸が見つからない場合の例外.
 */
@SuppressWarnings("PMD.MissingSerialVersionUID")
public class StocktakingNotFoundException extends ResourceNotFoundException {

    public StocktakingNotFoundException(String stocktakingNumber) {
        super("棚卸が見つかりません: " + stocktakingNumber);
    }

    public StocktakingNotFoundException(Integer id) {
        super("棚卸が見つかりません: ID=" + id);
    }
}
