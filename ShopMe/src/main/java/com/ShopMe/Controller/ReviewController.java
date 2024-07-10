package com.ShopMe.Controller;


import com.ShopMe.Entity.Review;
import com.ShopMe.ExceptionHandler.ReviewNotFoundException;
import com.ShopMe.Service.Impl.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

    private final ReviewService reviewService;

    @GetMapping("/reviews")
    public String listFirstPage(Model model) {
        return defaultRedirectURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNUm, Model model,
                             @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword){

        Page<Review> page = reviewService.listByPage(pageNUm, sortField, sortDir, keyword);
        List<Review> reviews = page.getContent();

        long startCount = (long) (pageNUm - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
        long endCount = startCount +  ReviewService.REVIEWS_PER_PAGE - 1;

        if(endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNUm);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listReviews", reviews);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);

        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Review review = reviewService.get(id);
            model.addAttribute("review", review);

            return "reviews/review_detail_modal";
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/reviews/edit/{id}")
    public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Review review = reviewService.get(id);
            model.addAttribute("review", review);
            model.addAttribute("pageTitle", String.format("Edit Review (ID: %d)", id));

            return "reviews/review_form";
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/reviews/save")
    public String saveReview(Review reviewInForm, RedirectAttributes ra) {
        reviewService.save(reviewInForm);
        ra.addFlashAttribute("messageSuccess", "The review ID " + reviewInForm.getId() + " has been updated successfully.");
        return defaultRedirectURL;
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            reviewService.delete(id);
            ra.addFlashAttribute("messageSuccess", "The review ID " + id + " has been deleted.");
        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
        }

        return defaultRedirectURL;
    }
}
