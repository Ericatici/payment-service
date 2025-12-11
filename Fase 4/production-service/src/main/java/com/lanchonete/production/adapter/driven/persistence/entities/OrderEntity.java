package com.lanchonete.production.adapter.driven.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lanchonete.production.core.domain.model.Order;
import com.lanchonete.production.core.domain.model.OrderItem;
import com.lanchonete.production.core.domain.model.enums.OrderStatusEnum;
import com.lanchonete.production.core.domain.model.enums.PaymentStatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@EqualsAndHashCode(callSuper = false)
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_cpf")
    private String customerCpf;

    @Column(name = "customer_name")
    private String customerName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(name = "payment_status") 
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;

    @Column(name = "qr_code_data") 
    private String qrCodeData;

    @Column(name = "payment_id") 
    private String paymentId;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    public Order toOrder() {
        List<OrderItem> itemList = this.getItems() != null ? this.getItems().stream()
                .map(item -> OrderItem.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .itemPrice(item.getItemPrice())
                        .itemsTotalPrice(item.getItemsTotalPrice())
                        .createdDate(item.getCreatedDate())
                        .updatedDate(item.getUpdatedDate())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new)) : new ArrayList<>();

        return Order.builder()
                .id(this.getId())
                .customerCpf(this.getCustomerCpf())
                .customerName(this.getCustomerName())
                .items(itemList)
                .orderDate(this.getOrderDate())
                .status(this.getStatus())
                .paymentStatus(this.getPaymentStatus())
                .qrCodeData(this.getQrCodeData())
                .paymentId(this.getPaymentId())
                .totalPrice(this.getTotalPrice())
                .createdDate(this.getCreatedDate())
                .updatedDate(this.getUpdatedDate())
                .build();
    }
}
