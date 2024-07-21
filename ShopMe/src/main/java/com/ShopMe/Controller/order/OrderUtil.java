package com.ShopMe.Controller.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.ShopMe.Entity.order.Order;
import com.ShopMe.Entity.settings.Setting;
import com.ShopMe.Service.Impl.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderUtil {

    public static void loadCurrencySetting(HttpServletRequest request, SettingService settingService) {

        List<Setting> currencySettings = settingService.getCurrencySettings();

        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    /*
    public static void updateOrderTracks(Order order, HttpServletRequest request) {

        LOGGER.info("OrderUtil | updateOrderTracks is called");

        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();

        LOGGER.info("OrderUtil | updateOrderTracks | orderTracks : " + orderTracks.toString());
        LOGGER.info("OrderUtil | updateOrderTracks | orderTracks.size : " + orderTracks.size());

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        LOGGER.info("OrderUtil | updateOrderTracks | trackIds.length : " + trackIds.length);

        for (int i = 0; i < trackIds.length; i++) {
            OrderTrack trackRecord = new OrderTrack();

            Integer trackId = Integer.parseInt(trackIds[i]);

            LOGGER.info("OrderUtil | updateOrderTracks | trackId : " + trackId);

            if (trackId > 0) {
                trackRecord.setId(trackId);
            }

            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);


            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                e.printStackTrace();
            }


            orderTracks.add(trackRecord);
        }
    }

    public static void updateProductDetails(Order order, HttpServletRequest request) {

        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            Integer detailId = Integer.parseInt(detailIds[i]);

            if (detailId > 0) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);

        }

    }
     */
}
