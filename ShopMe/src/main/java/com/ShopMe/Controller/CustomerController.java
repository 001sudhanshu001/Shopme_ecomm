package com.ShopMe.Controller;

import com.ShopMe.Entity.Country;
import com.ShopMe.Entity.Customer;
import com.ShopMe.ExceptionHandler.CustomerNotFoundException;
import com.ShopMe.Service.Impl.CustomerService;
import com.ShopMe.Service.Impl.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public String listFirstPage(Model model){
        return listByPage(model, 1, "firstName", "asc", null);
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(Model model,
                             @PathVariable(name = "pageNum") int pageNum,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){
        Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Customer> listCustomers = page.getContent();

        long startCount = (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  ProductService.PRODUCTS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listCustomers", listCustomers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {

        customerService.updateCustomerEnabledStatus(id,enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }


    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("error_message", ex.getMessage());

        }
        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            Customer customer = customerService.get(id);
            model.addAttribute("customer", customer);
            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {

        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_form";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("error_message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
        customerService.save(customer);
        ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");

        return "redirect:/customers";
    }
//
//    @GetMapping("/customers/export/csv")
//    public void exportToCSV(HttpServletResponse response) throws IOException {
//
//        LOGGER.info("CustomerController | exportToCSV is called");
//
//        List<Customer> listCustomers = service.listAll();
//
//        LOGGER.info("CustomerController | exportToCSV | listCustomers.size() : " + listCustomers.size());
//
//        CustomerCsvExporter exporter = new CustomerCsvExporter();
//
//        LOGGER.info("CustomerController | exportToCSV | export is starting");
//
//        exporter.export(listCustomers, response);
//
//        LOGGER.info("CustomerController | exportToCSV | export completed");
//    }
//
//    @GetMapping("/customers/export/excel")
//    public void exportToExcel(HttpServletResponse response) throws IOException {
//
//        LOGGER.info("CustomerController | exportToExcel is called");
//
//        List<Customer> listCustomers = service.listAll();
//
//        LOGGER.info("CustomerController | exportToExcel | listCustomers.size() : " + listCustomers.size());
//
//        CustomerExcelExporter exporter = new CustomerExcelExporter();
//
//        LOGGER.info("CustomerController | exportToExcel | export is starting");
//
//        exporter.export(listCustomers, response);
//
//        LOGGER.info("CustomerController | exportToExcel | export completed");
//    }
//
//    @GetMapping("/customers/export/pdf")
//    public void exportToPDF(HttpServletResponse response) throws IOException {
//
//
//        List<Customer> listCustomers = customerService.listAll();
//
//        LOGGER.info("UserController | exportToPDF | listCustomers.size() : " + listCustomers.size());
//
//        CustomerPdfExporter exporter = new CustomerPdfExporter();
//
//
//        exporter.export(listCustomers, response);
//
//    }
}
