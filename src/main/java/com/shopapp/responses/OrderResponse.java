package com.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shopapp.models.Order;
import com.shopapp.models.OrderDetail;
import com.shopapp.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse extends BaseResponse{
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String email;

    private String address;

    private String note;

    @JsonProperty("order_date")
    private LocalDate orderDate;

    private OrderStatus status;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("tracking_number")
    private String trackingNumber;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("order_details")
    private List<OrderDetail> orderDetails;

    public static OrderResponse fromOrder (Order order) {
        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .shippingMethod(order.getShippingMethod())
                .shippingDate(order.getShippingDate())
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(order.getPaymentMethod())
                .active(order.isActive())
                .orderDetails(order.getOrderDetails())
                .build();
        return orderResponse;
    }
}
