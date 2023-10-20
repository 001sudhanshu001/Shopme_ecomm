package com.ShopMe.Controller.order;

import com.ShopMe.Entity.order.Order;
import com.ShopMe.Entity.settings.Setting;
import com.ShopMe.ExceptionHandler.OrderNotFoundException;
import com.ShopMe.Service.Impl.OrderService;
import com.ShopMe.Service.Impl.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    private final OrderService orderService;
    private final SettingService settingService;

    @GetMapping("/orders")
    public String listFirstPage(Model model){

//        return this.listByPage(1,model,"name", "asc",null);

        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNUm, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Order> page = this.orderService.listByPage(pageNUm, sortField, sortDir, keyword);
        List<Order> listOrders = page.getContent();
        page.getContent().forEach(o-> System.out.println(o.getCustomer().getFirstName()));


        long startCount = (long) (pageNUm - 1) * OrderService.ORDERS_PER_PAGE + 1;
        long endCount = startCount +  OrderService.ORDERS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNUm);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        System.out.println("----------------------------------");
        page.getContent().forEach(o-> System.out.println(o.getCustomer().getFirstName()));


        return "orders/orders";
    }


    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Integer id, Model model,
                                   RedirectAttributes ra, HttpServletRequest request) {
        try {
            Order order = orderService.get(id);

            loadCurrencySetting(request);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
            return defaultRedirectURL;
        }

    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("messageSuccess", "The order ID " + id + " has been deleted.");
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    public void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings= settingService.getCurrencySettings();


        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }


}