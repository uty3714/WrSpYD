package com.hnyf.wrsp.event;

import java.io.Serializable;

public class EventCheckAccont implements Serializable {

    private String accountName;
    private String accountPath;

    public EventCheckAccont(){}

    public EventCheckAccont(String accountName) {
        this.accountName = accountName;
    }

    public EventCheckAccont(String accountName, String accountPath) {
        this.accountName = accountName;
        this.accountPath = accountPath;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPath() {
        return accountPath;
    }

    public void setAccountPath(String accountPath) {
        this.accountPath = accountPath;
    }
}
