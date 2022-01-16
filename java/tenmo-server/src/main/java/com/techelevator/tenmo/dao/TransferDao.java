package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    // Add funds when sent from user
    public Transfer transferFunds(Transfer transfer);

    public List<Transfer> listTransfers(int userId);

    public int getAccountId(int userId);


}
