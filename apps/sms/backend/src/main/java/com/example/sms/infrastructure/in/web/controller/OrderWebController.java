package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.OrderUseCase;
import com.example.sms.application.port.in.PartnerUseCase;
import com.example.sms.application.port.in.ProductUseCase;
import com.example.sms.domain.model.partner.Partner;
import com.example.sms.domain.model.product.Product;
import com.example.sms.domain.model.sales.OrderStatus;
import com.example.sms.domain.model.sales.SalesOrder;
import com.example.sms.infrastructure.in.web.form.OrderForm;
import com.example.sms.infrastructure.in.web.form.OrderForm.OrderDetailForm;
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
import java.util.Locale;

/**
 * 受注画面コントローラー.
 */
@Controller
@RequestMapping("/orders")
public class OrderWebController {

    private final OrderUseCase orderUseCase;
    private final PartnerUseCase partnerUseCase;
    private final ProductUseCase productUseCase;

    public OrderWebController(
            OrderUseCase orderUseCase,
            PartnerUseCase partnerUseCase,
            ProductUseCase productUseCase) {
        this.orderUseCase = orderUseCase;
        this.partnerUseCase = partnerUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 受注一覧画面を表示.
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<SalesOrder> orders = getFilteredOrders(status, customerCode);

        // キーワードでフィルタ
        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            orders = orders.stream()
                .filter(o -> o.getOrderNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                    || (o.getCustomerOrderNumber() != null
                        && o.getCustomerOrderNumber().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("customers", partnerUseCase.getCustomers());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCustomerCode", customerCode);
        model.addAttribute("keyword", keyword);
        return "orders/list";
    }

    private List<SalesOrder> getFilteredOrders(OrderStatus status, String customerCode) {
        if (status != null) {
            return orderUseCase.getOrdersByStatus(status);
        } else if (customerCode != null && !customerCode.isBlank()) {
            return orderUseCase.getOrdersByCustomer(customerCode);
        } else {
            return orderUseCase.getAllOrders();
        }
    }

    /**
     * 受注詳細画面を表示.
     */
    @GetMapping("/{orderNumber}")
    public String show(@PathVariable String orderNumber, Model model) {
        SalesOrder order = orderUseCase.getOrderWithDetails(orderNumber);
        model.addAttribute("order", order);
        return "orders/show";
    }

    /**
     * 受注登録フォームを表示.
     */
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new OrderForm());
        addFormAttributes(model);
        return "orders/new";
    }

    /**
     * 受注を登録.
     */
    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") OrderForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "orders/new";
        }

        SalesOrder order = orderUseCase.createOrder(form.toCreateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + order.getOrderNumber() + "」を登録しました");
        return "redirect:/orders";
    }

    /**
     * 受注編集フォームを表示.
     */
    @GetMapping("/{orderNumber}/edit")
    public String editForm(@PathVariable String orderNumber, Model model) {
        SalesOrder order = orderUseCase.getOrderWithDetails(orderNumber);
        model.addAttribute("form", OrderForm.from(order));
        addFormAttributes(model);
        return "orders/edit";
    }

    /**
     * 受注を更新.
     */
    @PostMapping("/{orderNumber}")
    public String update(
            @PathVariable String orderNumber,
            @Valid @ModelAttribute("form") OrderForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "orders/edit";
        }

        orderUseCase.updateOrder(orderNumber, form.toUpdateCommand());
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + orderNumber + "」を更新しました");
        return "redirect:/orders/" + orderNumber;
    }

    /**
     * 受注をキャンセル.
     */
    @PostMapping("/{orderNumber}/cancel")
    public String cancel(
            @PathVariable String orderNumber,
            @RequestParam Integer version,
            RedirectAttributes redirectAttributes) {

        orderUseCase.cancelOrder(orderNumber, version);
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + orderNumber + "」をキャンセルしました");
        return "redirect:/orders/" + orderNumber;
    }

    /**
     * 受注を削除.
     */
    @PostMapping("/{orderNumber}/delete")
    public String delete(
            @PathVariable String orderNumber,
            RedirectAttributes redirectAttributes) {

        orderUseCase.deleteOrder(orderNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "受注「" + orderNumber + "」を削除しました");
        return "redirect:/orders";
    }

    /**
     * 明細行を追加（htmx用フラグメント）.
     */
    @GetMapping("/add-detail-row")
    public String addDetailRow(@RequestParam int index, Model model) {
        model.addAttribute("index", index);
        model.addAttribute("detail", new OrderDetailForm());
        model.addAttribute("products", productUseCase.getAllProducts());
        return "orders/fragments :: detailRow";
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
        model.addAttribute("customers", customers);
        model.addAttribute("products", products);
    }
}
