package com.bankx.techtest.Domain;

import java.util.*;

public class AccountHolder {

    private Long accountHolderId;
    private String name;
    private String surname;
    private String idNumber;
    private String address;
    private String contactNumber;
    private String emailAddress;

    public AccountHolder(){};

    public AccountHolder(Long accountHolderId, String name, String surname, String idNumber, String address, String contactNumber,
                         String emailAddress)
    {
        this.accountHolderId = accountHolderId;
        this.name = name;
        this.surname = surname;
        this.idNumber = idNumber;
        this.address = address;
        this.contactNumber = contactNumber;
        this.emailAddress = emailAddress;
    }

    public Long getAccountHolderId() {
        return accountHolderId;
    }

    public void setAccountHolderId(Long accountHolderId) {
        this.accountHolderId = accountHolderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}