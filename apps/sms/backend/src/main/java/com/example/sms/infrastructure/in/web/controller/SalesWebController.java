package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.SalesUseCase;
import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.sales.Sales;
import com.example.sms.domain.model.sales.SalesStatus;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.infrastructure.in.web.form.SalesForm;
import com.example.sms.infrastructure.in.web.form.SalesForm.SalesDetailForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 売上画面コントローラー.
 */
@Controller
@RequestMapping("/sales")
public class SalesWebController {

    private final SalesUseCase salesUseCase;
    private final ShipmentUseCase shipmentUseCase;
    private final PartnerUseCase partnerUseCase;
    private final ProductUseCase productUseCase;

    public SalesWebController(
            SalesUseCase salesUseCase,
            ShipmentUseCase shipmentUseCase,
            PartnerUseCase partnerUseCase,
            ProductUseCase productUseCase) {
        this.salesUseCase = salesUseCase;
        this.shipmentUseCase = shipmentUseCase;
        this.partnerUseCase = partnerUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 売上一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Sales> salesPage = salesUseCase.getSales(page, size, keyword);

        model.addAttribute("salesList", salesPage.getContent());
        model.addAttribute("page", salesPage);
        model.addAttribute("statuses", SalesStatus.values());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "sales/list";
    }

    /**
     * 売上詳細画面を表示.
     */
    @GetMapping("/{salesNumber}")
    public String show(@PathVariable String salesNumber, Model model) {
        Sales sales = salesUseCase.getSalesByNumber(salesNumber);
        model.addAttribute("sales", sales);
        return "sales/show";
    }

    /**
     * 売上登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(
            @RequestParam(required = false) String shipmentNumber,
            Model model) {
        SalesForm form = new SalesForm();

        // 出荷番号が指定されている場合、出荷情報を取得してフォームに設定
        if (shipmentNumber != null && !shipmentNumber.isBlank()) {
            Shipment shipment = shipmentUseCase.getShipmentByNumber(shipmentNumber);
            form = SalesForm.fromShipment(shipment);
        }

        model.addAttribute("form", form);
        addFormAttributes(model);
        return "sales/new";
    }

    /**
     * 売上を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") SalesForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "sales/new";
        }

        Sales sales = salesUseCase.createSales(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "売上「" + sales.getSalesNumber() + "」を登録しました");
        return "redirect:/sales";
    }

    /**
     * 売上編集フォームを表示.
     */
    @GetMapping("/{salesNumber}/edit")
    public String editForm(@PathVariable String salesNumber, Model model) {
        Sales sales = salesUseCase.getSalesByNumber(salesNumber);
        model.addAttribute("form", SalesForm.from(sales));
        addFormAttributes(model);
        return "sales/edit";
    }

    /**
     * 売上を更新.
     */
    @PostMapping("/{salesNumber}")
    public String update(
            @PathVariable String salesNumber,
            @Valid @ModelAttribute("form") SalesForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "sales/edit";
        }

        salesUseCase.updateSales(salesNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "売上「" + salesNumber + "」を更新しました");
        return "redirect:/sales/" + salesNumber;
    }

    /**
     * 売上をキャンセル.
     */
    @PostMapping("/{salesNumber}/cancel")
    public String cancel(
            @PathVariable String salesNumber,
            RedirectAttributes redirectAttributes) {

        salesUseCase.cancelSales(salesNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "売上「" + salesNumber + "」をキャンセルしました");
        return "redirect:/sales/" + salesNumber;
    }

    /**
     * 売上を削除.
     */
    @PostMapping("/{salesNumber}/delete")
    public String delete(
            @PathVariable String salesNumber,
            RedirectAttributes redirectAttributes) {

        salesUseCase.deleteSales(salesNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "売上「" + salesNumber + "」を削除しました");
        return "redirect:/sales";
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new SalesDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "sales/fragments :: detailRow";
    }

    /**
     * 商品情報を取得（htmx用JSON）.
     */
    @GetMapping("/product-info")
    @ResponseBody
    public Product getProductInfo(@RequestParam String productCode) {
        return productUseCase.getProductByCode(productCode);
    }

    /**
     * フォームの共通属性を追加.
     */
    private void addFormAttributes(Model model) {
        List<Partner> customers = partnerUseCase.getCustomers();
        List<Product> products = productUseCase.getAllProducts();
        List<Shipment> shipments = shipmentUseCase.getAllShipments();
        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
        model.addAttribute("shipments", shipments);
        model.addAttribute("statuses", SalesStatus.values());
    }
}
