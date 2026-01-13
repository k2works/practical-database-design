package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.BomUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.domain.model.bom.Bom;
import com.example.pms.domain.model.bom.BomExplosion;
import com.example.pms.domain.model.item.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * BOM マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/bom")
public class BomWebController {

    private final BomUseCase bomUseCase;
    private final ItemUseCase itemUseCase;

    public BomWebController(BomUseCase bomUseCase, ItemUseCase itemUseCase) {
        this.bomUseCase = bomUseCase;
        this.itemUseCase = itemUseCase;
    }

    /**
     * BOM 一覧画面を表示する.
     *
     * @param model モデル
     * @return ビュー名
     */
    @GetMapping
    public String list(Model model) {
        List<Item> items = itemUseCase.getAllItems();
        model.addAttribute("items", items);
        return "bom/list";
    }

    /**
     * 構成表（親品目の子部品一覧）を表示する.
     *
     * @param parentItemCode 親品目コード
     * @param model モデル
     * @return ビュー名
     */
    @GetMapping("/{parentItemCode}")
    public String show(@PathVariable String parentItemCode, Model model) {
        Item parentItem = itemUseCase.getItem(parentItemCode);
        List<Bom> bomList = bomUseCase.getBomByParentItem(parentItemCode);

        model.addAttribute("parentItem", parentItem);
        model.addAttribute("bomList", bomList);
        return "bom/show";
    }

    /**
     * BOM 展開（部品展開）画面を表示する.
     *
     * @param itemCode 品目コード
     * @param quantity 数量（デフォルト1）
     * @param model モデル
     * @return ビュー名
     */
    @GetMapping("/{itemCode}/explode")
    public String explode(
            @PathVariable String itemCode,
            @RequestParam(defaultValue = "1") BigDecimal quantity,
            Model model) {
        Item item = itemUseCase.getItem(itemCode);
        List<BomExplosion> explosionList = bomUseCase.explodeBom(itemCode, quantity);

        model.addAttribute("item", item);
        model.addAttribute("quantity", quantity);
        model.addAttribute("explosionList", explosionList);
        return "bom/explode";
    }

    /**
     * 逆展開（使用先照会）画面を表示する.
     *
     * @param childItemCode 子品目コード
     * @param model モデル
     * @return ビュー名
     */
    @GetMapping("/{childItemCode}/where-used")
    public String whereUsed(@PathVariable String childItemCode, Model model) {
        Item childItem = itemUseCase.getItem(childItemCode);
        List<Bom> whereUsedList = bomUseCase.whereUsed(childItemCode);

        model.addAttribute("childItem", childItem);
        model.addAttribute("whereUsedList", whereUsedList);
        return "bom/where-used";
    }
}
