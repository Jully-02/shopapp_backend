package com.shopapp.repositories;

import com.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.address LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " +
            "OR o.email LIKE %:keyword%)")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

/*
INSERT INTO orders (user_id, fullname, email, phone_number, address, note, status, total_money)
VALUES
    (2, 'John Smith', 'john@example.com', '1234567890', '123 Main St', 'Note 1', 'pending', 500),
    (6, 'Eric Thompson', 'eric@example.com', '9876543210', '456 Elm St', 'Note 2', 'pending', 400),
    (2, 'Hans', 'hans@example.com', '5555555555', '789 Oak St', 'Note 3', 'pending', 300),
    (6, 'Alice Johnson', 'alice@example.com', '5551234567', '789 Cherry Ave', 'Note 4', 'pending', 200),
    (2, 'Robert Williams', 'robert@example.com', '5559876543', '321 Maple Rd', 'Note 5', 'pending', 100),
    (2, 'Sarah Davis', 'sarah@example.com', '5554445555', '987 Elm St', 'Note 6', 'pending', 250),
    (6, 'Michael Anderson', 'michael@example.com', '5556667777', '654 Oak Ave', 'Note 7', 'pending', 350),
    (2, 'Emma Wilson', 'emma@example.com', '5558889999', '789 Maple Ln', 'Note 8', 'pending', 450),
    (2, 'Olivia Brown', 'olivia@example.com', '5551112222', '987 Pine St', 'Note 47', 'pending', 350),
    (6, 'William Davis', 'william@example.com', '5553334444', '654 Elm Ave', 'Note 48', 'pending', 250),
    (2, 'Sophia Wilson', 'sophia@example.com', '5555556666', '789 Oak Ln', 'Note 49', 'pending', 150),
    (6, 'Alexander Anderson', 'alexander@example.com', '5557778888', '456 Maple Lane', 'Note 50', 'pending', 450),
    (2, 'Ava Thompson', 'ava@example.com', '5559990000', '987 Walnut Rd', 'Note 51', 'pending', 550),
    (6, 'Daniel Johnson', 'daniel@example.com', '5552223333', '654 Pine Ave', 'Note 52', 'pending', 650),
    (2, 'Mia Williams', 'mia@example.com', '5554445555', '789 Elm St', 'Note 53', 'pending', 750),
    (6, 'James Davis', 'james@example.com', '5556667777', '456 Oak Ave', 'Note 54', 'pending', 850),
    (6, 'Benjamin Thompson', 'benjamin@example.com', '5550001111', '654 Walnut Rd', 'Note 56', 'pending', 550),
    (2, 'Sophia Anderson', 'sophia@example.com', '5551112222', '987 Pine St', 'Note 57', 'pending', 350),
    (6, 'Elijah Davis', 'elijah@example.com', '5553334444', '654 Elm Ave', 'Note 58', 'pending', 250),
    (2, 'Ava Wilson', 'ava@example.com', '5555556666', '789 Oak Ln', 'Note 59', 'pending', 150),
    (6, 'Oliver Thompson', 'oliver@example.com', '5557778888', '456 Maple Lane', 'Note 60', 'pending', 450),
    (2, 'Mia Johnson', 'mia@example.com', '5559990000', '987 Walnut Rd', 'Note 61', 'pending', 550),
    (6, 'James Williams', 'james@example.com', '5552223333', '654 Pine Ave', 'Note 62', 'pending', 650),
    (2, 'Charlotte Davis', 'charlotte@example.com', '5554445555', '789 Elm St', 'Note 63', 'pending', 750),
    (6, 'Benjamin Wilson', 'benjamin@example.com', '5556667777', '456 Oak Ave', 'Note 64', 'pending', 850),
    (2, 'Amelia Thompson', 'amelia@example.com', '5558889999', '321 Maple Ln', 'Note 65', 'pending', 950),
    (6, 'Henry Johnson', 'henry@example.com', '5550001111', '654 Walnut Rd', 'Note 66', 'pending', 550),
    (6, 'Emily Davis', 'emily@example.com', '5552223333', '456 Walnut Lane', 'Note 46', 'pending', 150);


INSERT INTO order_details (order_id, product_id, price, number_of_products, total_money)
VALUES
    -- 100 records with order_id from 11 to 30
    (11, 1, 10.99, 2, 21.98),
    (11, 2, 5.99, 3, 17.97),
    (11, 3, 8.49, 1, 8.49),
    (12, 1, 10.99, 2, 21.98),
    (12, 2, 5.99, 3, 17.97),
    (12, 3, 8.49, 1, 8.49),
    (13, 6, 12.99, 3, 38.97),
    (14, 7, 6.99, 1, 6.99),
    (15, 8, 14.99, 2, 29.98),
    (16, 9, 11.49, 1, 11.49),
    (17, 10, 8.99, 3, 26.97),
    (18, 11, 13.99, 2, 27.98),
    (20, 12, 10.49, 1, 10.49),
    (20, 13, 7.49, 2, 14.98),
    (19, 1, 10.99, 2, 21.98),
    (19, 2, 5.99, 3, 17.97),
    (21, 3, 8.49, 1, 8.49),
    (21, 14, 9.99, 2, 19.98),
    (22, 15, 5.99, 3, 17.97),
    (22, 16, 8.49, 1, 8.49),
    (23, 17, 10.99, 2, 21.98),
    (24, 18, 5.99, 3, 17.97),
    (24, 19, 8.49, 1, 8.49),
    (25, 20, 6.99, 2, 13.98),
    (25, 21, 14.99, 1, 14.99),
    (26, 22, 11.49, 3, 34.47),
    (26, 23, 8.99, 2, 17.98),
    (27, 24, 13.99, 1, 13.99),
    (27, 25, 10.49, 3, 31.47),
    (28, 26, 7.49, 2, 14.98),
    (28, 27, 9.99, 1, 9.99),
    (28, 28, 5.99, 3, 17.97),
    (29, 29, 8.49, 1, 8.49),
    (29, 30, 10.99, 2, 21.98),
    (29, 31, 5.99, 3, 17.97),
    (30, 32, 8.49, 1, 8.49),
    (30, 33, 6.99, 2, 13.98),
    (30, 34, 14.99, 1, 14.99),
    (13, 35, 11.49, 3, 34.47),
    (13, 36, 8.99, 2, 17.98),
    (16, 37, 13.99, 1, 13.99),
    (22, 38, 10.49, 3, 31.47),
    (21, 39, 7.49, 2, 14.98),
    (23, 40, 9.99, 1, 9.99),
    (25, 41, 5.99, 3, 17.97),
    (27, 42, 8.49, 1, 8.49),
    (28, 43, 10.99, 2, 21.98),
    (29, 44, 5.99, 3, 17.97),
    (30, 45, 8.49, 1, 8.49),
    (24, 46, 6.99, 2, 13.98),
    (22, 47, 14.99, 1, 14.99),
    (27, 48, 11.49, 3, 34.47),
    (27, 49, 8.99, 2, 17.98),
    (28, 50, 13.99, 1, 13.99),
    (29, 51, 10.49, 3, 31.47),
    (29, 52, 7.49, 2, 14.98),
    (15, 53, 9.99, 1, 9.99),
    (13, 54, 5.99, 3, 17.97);

* */

