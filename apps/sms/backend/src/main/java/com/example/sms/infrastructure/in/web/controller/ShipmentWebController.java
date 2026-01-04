package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.application.port.in.ShipmentUseCase;
import com.example.sms.domain.model.common.PageResult;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.domain.model.shipping.Shipment;
import com.example.sms.domain.model.shipping.ShipmentStatus;
import com.example.sms.infrastructure.in.web.form.ShipmentForm;
import com.example.sms.infrastructure.in.web.form.ShipmentForm.ShipmentDetailForm;
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
 * 出荷画面コントローラー.
 */
@Controller
@RequestMapping("/shipments")
public class ShipmentWebController {

    private final ShipmentUseCase shipmentUseCase;
    private final OrderUseCase orderUseCase;
    private final PartnerUseCase partnerUseCase;
    private final ProductUseCase productUseCase;

    public ShipmentWebController(
            ShipmentUseCase shipmentUseCase,
            OrderUseCase orderUseCase,
            PartnerUseCase partnerUseCase,
            ProductUseCase productUseCase) {
        this.shipmentUseCase = shipmentUseCase;
        this.orderUseCase = orderUseCase;
        this.partnerUseCase = partnerUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 出荷一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        PageResult<Shipment> shipmentPage = shipmentUseCase.getShipments(page, size, keyword);

        model.addAttribute("shipments", shipmentPage.getContent());
        model.addAttribute("page", shipmentPage);
        model.addAttribute("statuses", ShipmentStatus.values());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSize", size);
        return "shipments/list";
    }

    /**
     * 出荷詳細画面を表示.
     */
    @GetMapping("/{shipmentNumber}")
    public String show(@PathVariable String shipmentNumber, Model model) {
        Shipment shipment = shipmentUseCase.getShipmentByNumber(shipmentNumber);
        model.addAttribute("shipment", shipment);
        return "shipments/show";
    }

    /**
     * 出荷登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(
            @RequestParam(required = false) String orderNumber,
            Model model) {
        ShipmentForm form = new ShipmentForm();

        // 受注番号が指定されている場合、受注情報を取得してフォームに設定
        if (orderNumber != null && !orderNumber.isBlank()) {
            SalesOrder order = orderUseCase.getOrderWithDetails(orderNumber);
            form = ShipmentForm.fromOrder(order);
        }

        model.addAttribute("form", form);
        addFormAttributes(model);
        return "shipments/new";
    }

    /**
     * 出荷を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") ShipmentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "shipments/new";
        }

        Shipment shipment = shipmentUseCase.createShipment(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷「" + shipment.getShipmentNumber() + "」を登録しました");
        return "redirect:/shipments";
    }

    /**
     * 出荷編集フォームを表示.
     */
    @GetMapping("/{shipmentNumber}/edit")
    public String editForm(@PathVariable String shipmentNumber, Model model) {
        Shipment shipment = shipmentUseCase.getShipmentByNumber(shipmentNumber);
        model.addAttribute("form", ShipmentForm.from(shipment));
        addFormAttributes(model);
        return "shipments/edit";
    }

    /**
     * 出荷を更新.
     */
    @PostMapping("/{shipmentNumber}")
    public String update(
            @PathVariable String shipmentNumber,
            @Valid @ModelAttribute("form") ShipmentForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "shipments/edit";
        }

        shipmentUseCase.updateShipment(shipmentNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷「" + shipmentNumber + "」を更新しました");
        return "redirect:/shipments/" + shipmentNumber;
    }

    /**
     * 出荷を確定.
     */
    @PostMapping("/{shipmentNumber}/confirm")
    public String confirm(
            @PathVariable String shipmentNumber,
            RedirectAttributes redirectAttributes) {

        shipmentUseCase.confirmShipment(shipmentNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷「" + shipmentNumber + "」を確定しました");
        return "redirect:/shipments/" + shipmentNumber;
    }

    /**
     * 出荷を削除.
     */
    @PostMapping("/{shipmentNumber}/delete")
    public String delete(
            @PathVariable String shipmentNumber,
            RedirectAttributes redirectAttributes) {

        shipmentUseCase.deleteShipment(shipmentNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷「" + shipmentNumber + "」を削除しました");
        return "redirect:/shipments";
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new ShipmentDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "shipments/fragments :: detailRow";
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
        List<SalesOrder> orders = orderUseCase.getAllOrders();
        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", ShipmentStatus.values());
    }
}
