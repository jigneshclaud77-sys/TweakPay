package com.example.demo.entity;

import java.sql.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.config.UserAuditListner;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

@Builder
@Getter
@Table(name = "item", uniqueConstraints = {
        @UniqueConstraint(name = "name", columnNames = { "item_name" })
}, check = {
        @CheckConstraint(name = "chk_amount", constraint = "amount >= 0 and amount <= 100000000")
})
@Entity
@EntityListeners(UserAuditListner.class)
@AllArgsConstructor
@NoArgsConstructor
public class Item {

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

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;

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
