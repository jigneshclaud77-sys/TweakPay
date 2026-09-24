package com.example.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumeratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name = "orders")
@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Item> items;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "payment_type", nullable = false)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public enum PaymentMode {
        CASH(1),
        UPI(2),
        CARD(3),
        NEFT(4);

        @EnumeratedValue
        private final int paymentMode;

        private PaymentMode(int paymentMode) {
            this.paymentMode = paymentMode;
        }
    }

    public enum OrderStatus{
        ACCEPTED(1),
        CANCELLED(2),
        INPROGRESS(3),
        DELIVERED(4);

        @EnumeratedValue
        private final int orderStatus;

        private OrderStatus(int orderStatus){
            this.orderStatus= orderStatus;
        }

    }


}
