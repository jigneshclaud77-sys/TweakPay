package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumeratedValue;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item", uniqueConstraints = {
        @UniqueConstraint(name = "name", columnNames = { "item_name" })
}, check = {
        @CheckConstraint(name = "chk_amount", constraint = "amount >= 0 and amount <= 100000000")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseAuditEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "item_name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @JsonBackReference
    private Order order;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private AvailableStatus status;

    public enum AvailableStatus {
        ACTIVE(1),
        INACTIVE(2);

        @EnumeratedValue
        private final int status;

        private AvailableStatus(int status) {
            this.status = status;
        }
    }
}
