package com.shopapp.controllers;

import com.shopapp.components.LocalizationUtils;
import com.shopapp.dtos.OrderDetailDTO;
import com.shopapp.models.OrderDetail;
import com.shopapp.responses.OrderDetailResponse;
import com.shopapp.services.OrderDetailService;
import com.shopapp.utils.MessageKeys;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail (
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail (
            @Valid @PathVariable Long id
    ) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get a list of order_details of a certain order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails (
            @Valid @PathVariable("orderId") Long orderId
    ) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(orderDetail -> OrderDetailResponse.fromOrderDetail(orderDetail))
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail (
            @Valid @PathVariable("id") Long id,
            @RequestBody OrderDetailDTO orderDetailDTO
    ) {
       try {
           OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
           return ResponseEntity.ok(orderDetail);
       } catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail (
            @Valid @PathVariable("id") Long id
    ) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY,id));
    }
}
