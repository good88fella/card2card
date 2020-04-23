package ru.sberbank.card2card.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "operations")
@Data
@EqualsAndHashCode(callSuper = true)
public class Operation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_card", nullable = false)
    private Card senderCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_card", nullable = false)
    private Card payeeCard;

    @Column(name = "transfer_amount", nullable = false)
    private double transferAmount;
}
