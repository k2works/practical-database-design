package com.example.pms.application.port.in;

import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;

import java.math.BigDecimal;
import java.util.List;

/**
 * BOM ユースケース（Input Port）.
 */
public interface BomUseCase {

    /**
     * 親品目コードで BOM を取得する.
     *
     * @param parentItemCode 親品目コード
     * @return BOM リスト
     */
    List<Bom> getBomByParentItem(String parentItemCode);

    /**
     * BOM 展開（部品展開）を実行する.
     *
     * @param itemCode 品目コード
     * @param quantity 数量
     * @return 展開結果
     */
    List<BomExplosion> explodeBom(String itemCode, BigDecimal quantity);

    /**
     * 逆展開（使用先照会）を実行する.
     *
     * @param childItemCode 子品目コード
     * @return 使用先リスト
     */
    List<Bom> whereUsed(String childItemCode);
}
