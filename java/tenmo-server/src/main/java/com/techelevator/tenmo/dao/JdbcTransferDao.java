package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Transfer transferFunds(Transfer transferred){
        // set our object of Transfer to Rejected by default
        transferred.setTransferStatusID(3);
        int currentUser = transferred.getAccountFrom();
        BigDecimal amount = transferred.getAmount();
        int userSendTo = transferred.getAccountTo();

        //TODO check if balance is greater than amount
        if (accountDao.retrieveBalance(currentUser).compareTo(amount) >= 0){
            // subtract the amount to transfer from the current user balance
            BigDecimal currentUserBalance = accountDao.retrieveBalance(currentUser);
            currentUserBalance = currentUserBalance.subtract(amount);

            String sql = "Update accounts SET balance = ? WHERE user_id = ?";
            // takes in the sql string, current balance, and the current user
            jdbcTemplate.update(sql, currentUserBalance , currentUser);


            //TODO add updated sqls for userSendTo
            BigDecimal userSendToBalance = accountDao.retrieveBalance(userSendTo);
            userSendToBalance = userSendToBalance.add(amount);
            String sqlSendTo = "Update accounts SET balance = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlSendTo, userSendToBalance , userSendTo);

            // after we update the sendTo and sendFrom balance we set the status of the transfer
            // to approved
            transferred.setTransferStatusID(2);

            // create a new transfers data set and get the receipt
            /*
            Ran into a fk error where we tried to create a table using the userId values, but the transfer table specifically took in
            accountId instead so we had to go create a getAccountId method below and then wrap the userid inside of it
             */

            String transferSql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES(?, ?, ?, ?, ?) RETURNING transfer_id";
            int transferID = jdbcTemplate.queryForObject(transferSql, Integer.class, transferred.getTransferTypeId(), transferred.getTransferStatusID(), getAccountId(transferred.getAccountFrom()), getAccountId(transferred.getAccountTo()), transferred.getAmount());
            // after this is all finished we set the receipt ID to the newly created transfer object
            transferred.setTransferID(transferID);
        }
        //TODO return status property of transfer class

        return transferred;
    }

    public List<Transfer> listTransfers(int userId){
        int accountId = getAccountId(userId);
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE account_from = ? OR account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            Transfer transfer = new Transfer();
            transfer.setTransferID(results.getInt("transfer_id"));
            transfer.setTransferTypeId(results.getInt("transfer_type_id"));
            transfer.setTransferStatusID(results.getInt("transfer_status_id"));
            transfer.setAccountFrom(results.getInt("account_from"));
            transfer.setAccountTo(results.getInt("account_to"));
            transfer.setAmount(results.getBigDecimal("amount"));
            transfers.add(transfer);
        }
        return transfers;
    }

    public int getAccountId(int userId){
        String sqlAccount = "select account_id from accounts where user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlAccount, userId);
        int accountId = 0;
        if (results.next()) {

            accountId = results.getInt("account_id");

        }
        return accountId;
    }
}
