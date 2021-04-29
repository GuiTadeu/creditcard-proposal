package com.orange.credicard.biometry;

import com.orange.credicard.card.Card;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
public class Biometry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String value;

    @NotNull
    @ManyToOne
    private Card card;

    @PastOrPresent
    private LocalDateTime createdAt = LocalDateTime.now();

    public Biometry(String value, Card card) {
        this.value = value;
        this.card = card;
    }

    public Long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public Card getCard() {
        return card;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
