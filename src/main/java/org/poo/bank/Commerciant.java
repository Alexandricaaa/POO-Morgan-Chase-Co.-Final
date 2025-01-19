package org.poo.bank;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

@Data
public class Commerciant {

    private String name;
    private int id;
    private String account;
    private String type;  // daca e food clothes tech
    private String cashbackStrategy;

    public Commerciant(CommerciantInput commerciant) {
        this.account = commerciant.getAccount();
        this.id = commerciant.getId();
        this.type = commerciant.getType();
        this.cashbackStrategy = commerciant.getCashbackStrategy();
        this.name = commerciant.getCommerciant();
    }

    public static Commerciant findCommerciant(CommandInput command, Bank bank, Account a){
        boolean found = false;
        for(Commerciant commerciant : bank.getCommerciants()){
            if(commerciant.getName().equals(command.getCommerciant())){
                for(Commerciant c : a.getCommerciants()){
                    if(c.getName().equals(commerciant.getName())){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    a.getCommerciants().add(commerciant);
                }
                return commerciant;
            }
        }
        return null;
    }

    public static void incrementNumOfTr(CommandInput command, Bank bank, Account account){

        double amount = command.getAmount();
        double ron = amount * Exchange.findExchangeRate(command.getCurrency(), "RON");

        Commerciant commerciant = findCommerciant(command, bank, account);
        if(commerciant != null){
            if(!commerciant.getCashbackStrategy().equals("nrOfTransactions")){
                account.setThresholdAmount(account.getThresholdAmount() + ron);
                return;
            }
            for(Commerciant c : bank.getCommerciants()){
                if(c.getName().equals(command.getCommerciant())){
                    if (!account.getNumberOfTransactions().containsKey(c)) {
                        account.getNumberOfTransactions().put(c, 1);
                        break;
                    }
                    else{
                        account.getNumberOfTransactions().put(c, account.getNumberOfTransactions().getOrDefault(c, 0) + 1);

                    }
                }
            }
        }
    }
}
