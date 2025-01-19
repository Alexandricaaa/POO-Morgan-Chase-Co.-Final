package org.poo.bank;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;

@Data
public class Exchange {

    private String from;
    private String to;
    private double rate;
    private int timestamp;

    public Exchange(ExchangeInput input){
        this.from = input.getFrom();
        this.to = input.getTo();
        this.rate = input.getRate();
        this.timestamp = input.getTimestamp();
    }

    public static double exchangeRate(final CommandInput command,
                               final String from, final String to) {
        if (from.equals(to)) {
            return command.getAmount();
        }

        double directRate = 0.0;
        double inverseRate = 0.0;
        double indirectRate = 0.0;

        for (Exchange exchange : Bank.exchanges) {
            if (from.equals(exchange.getFrom()) && to.equals(exchange.getTo())) {
                directRate = exchange.getRate();
            } else if (to.equals(exchange.getFrom()) && from.equals(exchange.getTo())) {
                inverseRate = 1 / exchange.getRate();
            }
        }

        if (directRate != 0.0) {
            return command.getAmount() * directRate;
        } else if (inverseRate != 0.0) {
            return command.getAmount() * inverseRate;
        } else {
            indirectRate = findIndirectRate(from, to);
            if (indirectRate != 0.0) {
                return command.getAmount() * indirectRate;
            }
        }

        throw new IllegalArgumentException("No valid exchange path found from "
                + from + " to " + to);
    }

    private static double findIndirectRate(final String from, final String to) {

        for (Exchange firstLeg : Bank.exchanges) {
            if (from.equals(firstLeg.getFrom())) {
                String intermediary = firstLeg.getTo();
                for (Exchange secondLeg : Bank.exchanges) {
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
                for (Exchange secondLeg : Bank.exchanges) {
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


    public static double findExchangeRate(final String from, final String to) {
        if (from.equals(to)) {
            return 1.0; // Rata identității
        }

        // Verificăm rata directă și inversă
        for (Exchange exchange : Bank.exchanges) {
            if (from.equals(exchange.getFrom()) && to.equals(exchange.getTo())) {
                return exchange.getRate(); // Rată directă
            } else if (to.equals(exchange.getFrom()) && from.equals(exchange.getTo())) {
                return 1 / exchange.getRate(); // Rată inversă
            }
        }

        // Căutăm rata indirectă
        return findIndirectRate(from, to);
    }


}
