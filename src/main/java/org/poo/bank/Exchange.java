package org.poo.bank;

import lombok.Data;
import org.poo.fileio.ExchangeInput;

@Data
public class Exchange {

    private String from;
    private String to;
    private double rate;
    private int timestamp;
    private Bank bank = null;

    public Exchange(final ExchangeInput input) {
        this.from = input.getFrom();
        this.to = input.getTo();
        this.rate = input.getRate();
        this.timestamp = input.getTimestamp();
    }
    public Exchange(final Bank bank) {
        this.bank = bank;
    }

    private double findIndirectRate(final String from, final String to) {

        for (Exchange firstLeg : bank.getExchanges()) {
            if (from.equals(firstLeg.getFrom())) {
                String intermediary = firstLeg.getTo();
                for (Exchange secondLeg : bank.getExchanges()) {
                    if (intermediary.equals(secondLeg.getFrom())
                            && to.equals(secondLeg.getTo())) {
                        return firstLeg.getRate() * secondLeg.getRate();
                    }
                    if (to.equals(secondLeg.getFrom())
                            && intermediary.equals(secondLeg.getTo())) {
                        return firstLeg.getRate() * (1 / secondLeg.getRate());
                    }
                }
            }
            if (from.equals(firstLeg.getTo())) {
                String intermediary = firstLeg.getFrom();
                for (Exchange secondLeg : bank.getExchanges()) {
                    if (intermediary.equals(secondLeg.getFrom())
                            && to.equals(secondLeg.getTo())) {
                        return (1 / firstLeg.getRate()) * secondLeg.getRate();
                    }
                    if (to.equals(secondLeg.getFrom())
                            && intermediary.equals(secondLeg.getTo())) {
                        return (1 / firstLeg.getRate()) * (1 / secondLeg.getRate());
                    }
                }
            }
        }
        return 0.0;
    }

    public double findExchangeRate(final String from, final String to) {
        if (from.equals(to)) {
            return 1.0;
        }

        for (Exchange exchange : bank.getExchanges()) {
            if (from.equals(exchange.getFrom()) && to.equals(exchange.getTo())) {
                return exchange.getRate();
            } else if (to.equals(exchange.getFrom()) && from.equals(exchange.getTo())) {
                return 1 / exchange.getRate();
            }
        }
        return findIndirectRate(from, to);
    }
}
