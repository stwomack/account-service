package com.example;

import org.springframework.context.annotation.PropertySource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by swomack on 1/22/16.
 */
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private String accountName;
    private String accountType;

    public Account(String accountName, String accountType) {
        this.accountName = accountName;
        this.accountType = accountType;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}
