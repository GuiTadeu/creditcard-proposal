package com.orange.credicard.wallet;

import com.orange.credicard.card.Card;

import javax.persistence.*;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private Card card;

    public Wallet(String name, Card card) {
        this.name = name;
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Card getCard() {
        return card;
    }
}
