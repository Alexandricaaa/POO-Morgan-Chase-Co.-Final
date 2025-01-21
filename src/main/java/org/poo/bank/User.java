package org.poo.bank;

import lombok.Data;
import org.poo.fileio.UserInput;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Data
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private String plan;
    private String role;
    private String planType;

    private boolean rejected = false;
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private Set<Account> accounts = new LinkedHashSet<>();
    private Map<String, String> employeeRole = new LinkedHashMap<>();
    private Map<Integer, Double> spendings = new HashMap<>();
    private Map<String, List<Transaction>> trPerAcc = new LinkedHashMap<>();


    public User(final UserInput input) {
        this.firstName = input.getFirstName();
        this.lastName = input.getLastName();
        this.email = input.getEmail();
        this.birthDate = input.getBirthDate();
        this.occupation = input.getOccupation();
    }


    public User(final User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.accounts = new HashSet<>();
        for (Account account : user.getAccounts()) {
            this.accounts.add(new Account(account));
        }
    }

    /**
     * Calculates the User age
     */
    public static int userAge(final String birthDate) {
        String[] parts = birthDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        LocalDate dob = LocalDate.of(year, month, day);
        LocalDate now = LocalDate.now();
        return Period.between(dob, now).getYears();
    }
}
