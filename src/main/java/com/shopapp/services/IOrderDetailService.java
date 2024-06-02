package com.shopapp.services;

import com.shopapp.dtos.OrderDetailDTO;
import com.shopapp.exceptions.DataNotFoundException;
import com.shopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail (OrderDetailDTO orderDetailDTO) throws Exception;

    OrderDetail getOrderDetail (Long id) throws Exception;

    OrderDetail updateOrderDetail (Long id, OrderDetailDTO orderDetailDTO) throws Exception;

    void deleteOrderDetail (Long id);

    List<OrderDetail> findByOrderId (Long orderId);
}
