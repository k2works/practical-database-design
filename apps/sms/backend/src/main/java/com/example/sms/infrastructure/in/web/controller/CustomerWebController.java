package com.example.sms.infrastructure.in.web.controller;

import com.example.sms.application.port.in.CustomerUseCase;
import com.example.sms.application.port.in.ShippingDestinationUseCase;
import com.example.sms.application.port.in.command.CreateCustomerCommand;
import com.example.sms.application.port.in.command.UpdateCustomerCommand;
import com.example.sms.domain.model.partner.BillingType;
import com.example.sms.domain.model.partner.Customer;
import com.example.sms.domain.model.partner.PaymentMethod;
import com.example.sms.domain.model.partner.ShippingDestination;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * 顧客マスタ画面コントローラー.
 */
@Controller
@RequestMapping("/customers")
public class CustomerWebController {

    private final CustomerUseCase customerUseCase;
    private final ShippingDestinationUseCase shippingDestinationUseCase;

    public CustomerWebController(CustomerUseCase customerUseCase,
                                 ShippingDestinationUseCase shippingDestinationUseCase) {
        this.customerUseCase = customerUseCase;
        this.shippingDestinationUseCase = shippingDestinationUseCase;
    }

    /**
     * 顧客一覧画面を表示.
     */
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Customer> customers = customerUseCase.getAllCustomers();

        if (keyword != null && !keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
            customers = customers.stream()
                .filter(c -> (c.getCustomerCode() != null
                        && c.getCustomerCode().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (c.getCustomerName() != null
                        && c.getCustomerName().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                    || (c.getCustomerNameKana() != null
                        && c.getCustomerNameKana().toLowerCase(Locale.ROOT).contains(lowerKeyword)))
                .toList();
        }

        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);
        return "customers/list";
    }

    /**
     * 顧客詳細画面を表示.
     */
    @GetMapping("/{customerCode}/{branchNumber}")
    public String show(@PathVariable String customerCode,
                       @PathVariable String branchNumber,
                       Model model) {
        Customer customer = customerUseCase.getCustomerByCodeAndBranch(customerCode, branchNumber);
        List<ShippingDestination> shippingDestinations =
            shippingDestinationUseCase.getShippingDestinationsByCustomer(customerCode, branchNumber);

        model.addAttribute("customer", customer);
        model.addAttribute("shippingDestinations", shippingDestinations);
        return "customers/show";
    }

    /**
     * 顧客登録画面を表示.
     */
    @GetMapping("/new")
    public String newCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("billingTypes", BillingType.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("isNew", true);
        return "customers/form";
    }

    /**
     * 顧客を登録.
     */
    @PostMapping
    public String create(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
        CreateCustomerCommand command = new CreateCustomerCommand(
            customer.getCustomerCode(),
            customer.getCustomerBranchNumber(),
            customer.getCustomerCategory(),
            customer.getBillingCode(),
            customer.getBillingBranchNumber(),
            customer.getCollectionCode(),
            customer.getCollectionBranchNumber(),
            customer.getCustomerName(),
            customer.getCustomerNameKana(),
            customer.getOurRepresentativeCode(),
            customer.getCustomerRepresentativeName(),
            customer.getCustomerDepartmentName(),
            customer.getCustomerPostalCode(),
            customer.getCustomerPrefecture(),
            customer.getCustomerAddress1(),
            customer.getCustomerAddress2(),
            customer.getCustomerPhone(),
            customer.getCustomerFax(),
            customer.getCustomerEmail(),
            customer.getBillingType(),
            customer.getClosingDay1(),
            customer.getPaymentMonth1(),
            customer.getPaymentDay1(),
            customer.getPaymentMethod1(),
            customer.getClosingDay2(),
            customer.getPaymentMonth2(),
            customer.getPaymentDay2(),
            customer.getPaymentMethod2()
        );
        customerUseCase.createCustomer(command);
        redirectAttributes.addFlashAttribute("successMessage",
            "顧客を登録しました: " + customer.getCustomerCode());
        return "redirect:/customers";
    }

    /**
     * 顧客編集画面を表示.
     */
    @GetMapping("/{customerCode}/{branchNumber}/edit")
    public String edit(@PathVariable String customerCode,
                       @PathVariable String branchNumber,
                       Model model) {
        Customer customer = customerUseCase.getCustomerByCodeAndBranch(customerCode, branchNumber);
        model.addAttribute("customer", customer);
        model.addAttribute("billingTypes", BillingType.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("isNew", false);
        return "customers/form";
    }

    /**
     * 顧客を更新.
     */
    @PostMapping("/{customerCode}/{branchNumber}")
    public String update(@PathVariable String customerCode,
                         @PathVariable String branchNumber,
                         @ModelAttribute Customer customer,
                         RedirectAttributes redirectAttributes) {
        UpdateCustomerCommand command = new UpdateCustomerCommand(
            customer.getCustomerCategory(),
            customer.getBillingCode(),
            customer.getBillingBranchNumber(),
            customer.getCollectionCode(),
            customer.getCollectionBranchNumber(),
            customer.getCustomerName(),
            customer.getCustomerNameKana(),
            customer.getOurRepresentativeCode(),
            customer.getCustomerRepresentativeName(),
            customer.getCustomerDepartmentName(),
            customer.getCustomerPostalCode(),
            customer.getCustomerPrefecture(),
            customer.getCustomerAddress1(),
            customer.getCustomerAddress2(),
            customer.getCustomerPhone(),
            customer.getCustomerFax(),
            customer.getCustomerEmail(),
            customer.getBillingType(),
            customer.getClosingDay1(),
            customer.getPaymentMonth1(),
            customer.getPaymentDay1(),
            customer.getPaymentMethod1(),
            customer.getClosingDay2(),
            customer.getPaymentMonth2(),
            customer.getPaymentDay2(),
            customer.getPaymentMethod2()
        );
        customerUseCase.updateCustomer(customerCode, branchNumber, command);
        redirectAttributes.addFlashAttribute("successMessage",
            "顧客を更新しました: " + customerCode);
        return "redirect:/customers/" + customerCode + "/" + branchNumber;
    }

    // ===== 出荷先管理 =====

    /**
     * 出荷先登録画面を表示.
     */
    @GetMapping("/{customerCode}/{branchNumber}/shipping-destinations/new")
    public String newShippingDestination(@PathVariable String customerCode,
                                         @PathVariable String branchNumber,
                                         Model model) {
        Customer customer = customerUseCase.getCustomerByCodeAndBranch(customerCode, branchNumber);
        ShippingDestination shippingDestination = ShippingDestination.builder()
            .partnerCode(customerCode)
            .customerBranchNumber(branchNumber)
            .build();

        model.addAttribute("customer", customer);
        model.addAttribute("shippingDestination", shippingDestination);
        model.addAttribute("isNew", true);
        return "customers/shipping-destination-form";
    }

    /**
     * 出荷先を登録.
     */
    @PostMapping("/{customerCode}/{branchNumber}/shipping-destinations")
    public String createShippingDestination(@PathVariable String customerCode,
                                            @PathVariable String branchNumber,
                                            @ModelAttribute ShippingDestination shippingDestination,
                                            RedirectAttributes redirectAttributes) {
        shippingDestination.setPartnerCode(customerCode);
        shippingDestination.setCustomerBranchNumber(branchNumber);
        shippingDestinationUseCase.createShippingDestination(shippingDestination);
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷先を登録しました: " + shippingDestination.getShippingNumber());
        return "redirect:/customers/" + customerCode + "/" + branchNumber;
    }

    /**
     * 出荷先編集画面を表示.
     */
    @GetMapping("/{customerCode}/{branchNumber}/shipping-destinations/{shippingNumber}/edit")
    public String editShippingDestination(@PathVariable String customerCode,
                                          @PathVariable String branchNumber,
                                          @PathVariable String shippingNumber,
                                          Model model) {
        Customer customer = customerUseCase.getCustomerByCodeAndBranch(customerCode, branchNumber);
        ShippingDestination shippingDestination =
            shippingDestinationUseCase.getShippingDestination(customerCode, branchNumber, shippingNumber);

        model.addAttribute("customer", customer);
        model.addAttribute("shippingDestination", shippingDestination);
        model.addAttribute("isNew", false);
        return "customers/shipping-destination-form";
    }

    /**
     * 出荷先を更新.
     */
    @PostMapping("/{customerCode}/{branchNumber}/shipping-destinations/{shippingNumber}")
    public String updateShippingDestination(@PathVariable String customerCode,
                                            @PathVariable String branchNumber,
                                            @PathVariable String shippingNumber,
                                            @ModelAttribute ShippingDestination shippingDestination,
                                            RedirectAttributes redirectAttributes) {
        shippingDestination.setPartnerCode(customerCode);
        shippingDestination.setCustomerBranchNumber(branchNumber);
        shippingDestination.setShippingNumber(shippingNumber);
        shippingDestinationUseCase.updateShippingDestination(shippingDestination);
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷先を更新しました: " + shippingNumber);
        return "redirect:/customers/" + customerCode + "/" + branchNumber;
    }

    /**
     * 出荷先を削除.
     */
    @PostMapping("/{customerCode}/{branchNumber}/shipping-destinations/{shippingNumber}/delete")
    public String deleteShippingDestination(@PathVariable String customerCode,
                                            @PathVariable String branchNumber,
                                            @PathVariable String shippingNumber,
                                            RedirectAttributes redirectAttributes) {
        shippingDestinationUseCase.deleteShippingDestination(customerCode, branchNumber, shippingNumber);
        redirectAttributes.addFlashAttribute("successMessage",
            "出荷先を削除しました: " + shippingNumber);
        return "redirect:/customers/" + customerCode + "/" + branchNumber;
    }
}
