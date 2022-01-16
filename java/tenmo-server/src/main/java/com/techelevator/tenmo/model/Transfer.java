package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int transferID;
    // 1 is request and 2 is send
    private int transferTypeId;
    // 1 is pending, 2 is approved, and 3 is rejected
    private int transferStatusID;
    private int accountTo;
    private int accountFrom;
    private BigDecimal amount;
    private String transferStatus;
    private String transferType;

    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusID() {
        return transferStatusID;
    }

    public void setTransferStatusID(int transferStatusID) {
        this.transferStatusID = transferStatusID;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    private void setTransferStatus(int id) {
        if (id == 1) {
            this.transferStatus = "Pending";
        } else if (id == 2) {
            this.transferStatus = "Approved";
        } else {
            this.transferStatus = "Rejected";
        }
    }

    private void setTransferType(int id) {
        if (id == 1) {
            this.transferType = "Request";
        } else {
            this.transferType = "Send";
        }
    }
}
