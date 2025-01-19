package org.poo.bank;

import lombok.Data;
import org.poo.utils.Utils;

@Data
public class Card {
    private String cardNumber;
    private String status = "active";
    private boolean oneTime = false;
    private boolean isFrozen = false;

    public Card(){
        cardNumber = Utils.generateCardNumber();
    }


    public Card(Card card) {
        this.cardNumber = card.getCardNumber();
        this.status = card.getStatus();
    }
}
