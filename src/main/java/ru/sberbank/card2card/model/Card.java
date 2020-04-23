package ru.sberbank.card2card.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "cards")
@Data
@EqualsAndHashCode(exclude = { "balance", "incomingTransfers", "outgoingTransfers" }, callSuper = true)
@ToString(exclude = { "incomingTransfers", "outgoingTransfers" })
public class Card extends BaseEntity {

    @Column(name = "card_number", nullable = false, unique = true)
    private long cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_holder", nullable = false)
    private User user;

    @Column(name = "balance")
    private double balance;

    @OneToMany(mappedBy = "senderCard", fetch = FetchType.LAZY)
    private Set<Operation> incomingTransfers;

    @OneToMany(mappedBy = "payeeCard", fetch = FetchType.LAZY)
    private Set<Operation> outgoingTransfers;
}
