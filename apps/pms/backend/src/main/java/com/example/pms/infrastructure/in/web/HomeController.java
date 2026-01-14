package com.example.pms.infrastructure.in.web;

import com.example.pms.application.port.in.BomUseCase;
import com.example.pms.application.port.in.CalendarUseCase;
import com.example.pms.application.port.in.CompletionResultUseCase;
import com.example.pms.application.port.in.DefectUseCase;
import com.example.pms.application.port.in.DepartmentUseCase;
import com.example.pms.application.port.in.InspectionResultUseCase;
import com.example.pms.application.port.in.IssueUseCase;
import com.example.pms.application.port.in.ItemUseCase;
import com.example.pms.application.port.in.LaborHoursUseCase;
import com.example.pms.application.port.in.LocationUseCase;
import com.example.pms.application.port.in.LotMasterUseCase;
import com.example.pms.application.port.in.MpsUseCase;
import com.example.pms.application.port.in.OrderUseCase;
import com.example.pms.application.port.in.ProcessRouteUseCase;
import com.example.pms.application.port.in.ProcessUseCase;
import com.example.pms.application.port.in.PurchaseOrderUseCase;
import com.example.pms.application.port.in.ReceivingUseCase;
import com.example.pms.application.port.in.StaffUseCase;
import com.example.pms.application.port.in.StockUseCase;
import com.example.pms.application.port.in.StocktakingUseCase;
import com.example.pms.application.port.in.SupplierUseCase;
import com.example.pms.application.port.in.UnitPriceUseCase;
import com.example.pms.application.port.in.UnitUseCase;
import com.example.pms.application.port.in.WorkOrderUseCase;
import com.example.pms.domain.model.process.WorkOrderStatus;
import com.example.pms.domain.model.purchase.PurchaseOrderStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * ホーム画面 Controller.
 */
@Controller
@SuppressWarnings({"PMD.ExcessiveParameterList", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public class HomeController {

    private final ItemUseCase itemUseCase;
    private final BomUseCase bomUseCase;
    private final ProcessUseCase processUseCase;
    private final ProcessRouteUseCase processRouteUseCase;
    private final SupplierUseCase supplierUseCase;
    private final UnitPriceUseCase unitPriceUseCase;
    private final DepartmentUseCase departmentUseCase;
    private final LocationUseCase locationUseCase;
    private final UnitUseCase unitUseCase;
    private final CalendarUseCase calendarUseCase;
    private final StaffUseCase staffUseCase;
    private final DefectUseCase defectUseCase;
    private final LotMasterUseCase lotMasterUseCase;
    private final MpsUseCase mpsUseCase;
    private final OrderUseCase orderUseCase;
    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final ReceivingUseCase receivingUseCase;
    private final WorkOrderUseCase workOrderUseCase;
    private final CompletionResultUseCase completionResultUseCase;
    private final LaborHoursUseCase laborHoursUseCase;
    private final StockUseCase stockUseCase;
    private final IssueUseCase issueUseCase;
    private final StocktakingUseCase stocktakingUseCase;
    private final InspectionResultUseCase inspectionResultUseCase;

    public HomeController(
            ItemUseCase itemUseCase,
            BomUseCase bomUseCase,
            ProcessUseCase processUseCase,
            ProcessRouteUseCase processRouteUseCase,
            SupplierUseCase supplierUseCase,
            UnitPriceUseCase unitPriceUseCase,
            DepartmentUseCase departmentUseCase,
            LocationUseCase locationUseCase,
            UnitUseCase unitUseCase,
            CalendarUseCase calendarUseCase,
            StaffUseCase staffUseCase,
            DefectUseCase defectUseCase,
            LotMasterUseCase lotMasterUseCase,
            MpsUseCase mpsUseCase,
            OrderUseCase orderUseCase,
            PurchaseOrderUseCase purchaseOrderUseCase,
            ReceivingUseCase receivingUseCase,
            WorkOrderUseCase workOrderUseCase,
            CompletionResultUseCase completionResultUseCase,
            LaborHoursUseCase laborHoursUseCase,
            StockUseCase stockUseCase,
            IssueUseCase issueUseCase,
            StocktakingUseCase stocktakingUseCase,
            InspectionResultUseCase inspectionResultUseCase) {
        this.itemUseCase = itemUseCase;
        this.bomUseCase = bomUseCase;
        this.processUseCase = processUseCase;
        this.processRouteUseCase = processRouteUseCase;
        this.supplierUseCase = supplierUseCase;
        this.unitPriceUseCase = unitPriceUseCase;
        this.departmentUseCase = departmentUseCase;
        this.locationUseCase = locationUseCase;
        this.unitUseCase = unitUseCase;
        this.calendarUseCase = calendarUseCase;
        this.staffUseCase = staffUseCase;
        this.defectUseCase = defectUseCase;
        this.lotMasterUseCase = lotMasterUseCase;
        this.mpsUseCase = mpsUseCase;
        this.orderUseCase = orderUseCase;
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.receivingUseCase = receivingUseCase;
        this.workOrderUseCase = workOrderUseCase;
        this.completionResultUseCase = completionResultUseCase;
        this.laborHoursUseCase = laborHoursUseCase;
        this.stockUseCase = stockUseCase;
        this.issueUseCase = issueUseCase;
        this.stocktakingUseCase = stocktakingUseCase;
        this.inspectionResultUseCase = inspectionResultUseCase;
    }

    /**
     * ホーム画面を表示.
     *
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/")
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("currentDate", today);

        // サマリー情報（件数カード）
        int pendingWorkOrderCount = workOrderUseCase.getWorkOrdersByStatus(WorkOrderStatus.NOT_STARTED).size()
                + workOrderUseCase.getWorkOrdersByStatus(WorkOrderStatus.IN_PROGRESS).size();
        model.addAttribute("pendingWorkOrderCount", pendingWorkOrderCount);

        int pendingPurchaseOrderCount = purchaseOrderUseCase.getOrdersByStatus(PurchaseOrderStatus.ORDERED).size()
                + purchaseOrderUseCase.getOrdersByStatus(PurchaseOrderStatus.PARTIALLY_RECEIVED).size();
        model.addAttribute("pendingPurchaseOrderCount", pendingPurchaseOrderCount);

        model.addAttribute("stockCount", stockUseCase.getAllStocks().size());
        model.addAttribute("orderCount", orderUseCase.getAllOrders().size());

        // マスタ系カウント
        model.addAttribute("itemCount", itemUseCase.getAllItems().size());
        model.addAttribute("processCount", processUseCase.getAllProcesses().size());
        model.addAttribute("processRouteCount", processRouteUseCase.getProcessRoutes(0, 1, null).getTotalElements());
        model.addAttribute("supplierCount", supplierUseCase.getAllSuppliers().size());
        model.addAttribute("unitPriceCount", unitPriceUseCase.getAllUnitPrices().size());
        model.addAttribute("departmentCount", departmentUseCase.getAllDepartments().size());
        model.addAttribute("locationCount", locationUseCase.getAllLocations().size());
        model.addAttribute("unitCount", unitUseCase.getAllUnits().size());
        model.addAttribute("calendarCount", calendarUseCase.getAllCalendars().size());
        model.addAttribute("staffCount", staffUseCase.getAllStaff().size());
        model.addAttribute("defectCount", defectUseCase.getAllDefects().size());
        model.addAttribute("lotCount", lotMasterUseCase.getLotMasterList(0, 1, null).getTotalElements());

        // 計画系カウント
        model.addAttribute("mpsCount", mpsUseCase.getAllMps().size());

        // 購買系カウント
        model.addAttribute("purchaseOrderCount", purchaseOrderUseCase.getAllOrders().size());
        model.addAttribute("receivingCount", receivingUseCase.getAllReceivings().size());

        // 工程系カウント
        model.addAttribute("workOrderCount", workOrderUseCase.getAllWorkOrders().size());
        model.addAttribute("completionResultCount", completionResultUseCase.getAllCompletionResults().size());
        model.addAttribute("laborHoursCount", laborHoursUseCase.getAllLaborHours().size());

        // 在庫系カウント
        model.addAttribute("issueCount", issueUseCase.getAllIssues().size());
        model.addAttribute("stocktakingCount", stocktakingUseCase.getAllStocktakings().size());

        // 品質系カウント
        model.addAttribute("inspectionResultCount", inspectionResultUseCase.getAllInspectionResults().size());

        return "index";
    }
}
