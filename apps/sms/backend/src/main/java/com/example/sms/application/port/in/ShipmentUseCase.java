package com.example.sms.application.port.in;

import com.example.sms.application.port.in.command.CreateShipmentCommand;
import com.example.sms.application.port.in.command.UpdateShipmentCommand;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentStatus;

import java.util.List;

/**
 * 出荷ユースケース（Input Port）.
 */
public interface ShipmentUseCase {

    /**
     * 出荷を登録する.
     *
     * @param command 登録コマンド
     * @return 登録された出荷
     */
    Shipment createShipment(CreateShipmentCommand command);

    /**
     * 出荷を更新する.
     *
     * @param shipmentNumber 出荷番号
     * @param command 更新コマンド
     * @return 更新された出荷
     */
    Shipment updateShipment(String shipmentNumber, UpdateShipmentCommand command);

    /**
     * 全出荷を取得する.
     *
     * @return 出荷リスト
     */
    List<Shipment> getAllShipments();

    /**
     * ページネーション付きで出荷を取得.
     *
     * @param page ページ番号（0始まり）
     * @param size ページサイズ
     * @param keyword 検索キーワード
     * @return ページ結果
     */
    PageResult<Shipment> getShipments(int page, int size, String keyword);

    /**
     * 出荷番号で出荷を取得する.
     *
     * @param shipmentNumber 出荷番号
     * @return 出荷
     */
    Shipment getShipmentByNumber(String shipmentNumber);

    /**
     * ステータスで出荷を検索する.
     *
     * @param status 出荷ステータス
     * @return 出荷リスト
     */
    List<Shipment> getShipmentsByStatus(ShipmentStatus status);

    /**
     * 受注IDで出荷を検索する.
     *
     * @param orderId 受注ID
     * @return 出荷リスト
     */
    List<Shipment> getShipmentsByOrder(Integer orderId);

    /**
     * 出荷を確定する.
     *
     * @param shipmentNumber 出荷番号
     * @return 確定された出荷
     */
    Shipment confirmShipment(String shipmentNumber);

    /**
     * 出荷を削除する.
     *
     * @param shipmentNumber 出荷番号
     */
    void deleteShipment(String shipmentNumber);
}
