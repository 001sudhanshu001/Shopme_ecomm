package com.ShopMe.Service.Impl;

import com.ShopMe.DAO.OrderRepo;
import com.ShopMe.Entity.order.Order;
import com.ShopMe.ExceptionHandler.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final int ORDERS_PER_PAGE = 10;

    private final OrderRepo orderRepo;

    public Page<Order> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = null;
        System.out.println("listBypage called");

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page;

        if (keyword != null) {
            page = orderRepo.findAll(keyword, pageable);
        } else {
            page = orderRepo.findAll(pageable);
        }

        return page;
    }

    public Order get(Integer id) throws OrderNotFoundException {
        try {
            return orderRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Long count = orderRepo.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }

        orderRepo.deleteById(id);
    }

}
