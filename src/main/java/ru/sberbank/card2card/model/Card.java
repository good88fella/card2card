package ru.sberbank.card2card.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "cards")
@Data
@EqualsAndHashCode(exclude = { "balance", "incomingTransfers", "outgoingTransfers" })
@ToString(exclude = { "incomingTransfers", "outgoingTransfers" })
public class Card {

    @Id
    @Column(name = "card_number", nullable = false)
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

    @Generated(GenerationTime.INSERT)
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @Generated(GenerationTime.ALWAYS)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    private Date updated;

    @Generated(GenerationTime.INSERT)
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 25)
    private Status status;
}
