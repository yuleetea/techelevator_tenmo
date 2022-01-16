package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AppController {

    @Autowired
    AccountDao accountDao;

    @Autowired
    UserDao userDao;

    @Autowired
    TransferDao transferDao;

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    // Principal asks for a token and then deserializes it
    public Balance obtainBalance(Principal principal) {

        Balance balanceObject = new Balance();

        String name = principal.getName();
        int userId = userDao.findIdByUsername(name);
        // looking for the client to send over the token
        BigDecimal currentBalance = accountDao.retrieveBalance(userId);

        balanceObject.setBalance(currentBalance);

        return balanceObject;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.PUT)
    public Transfer transferFunds(@RequestBody Transfer transfer, Principal principal){
        String name = principal.getName();
        // this grabs currentUser
        int currentUser = userDao.findIdByUsername(name);
        transfer.setAccountFrom(currentUser);
        Transfer updatedTransfer = transferDao.transferFunds(transfer);

        return updatedTransfer;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path="/user", method = RequestMethod.PUT)
    public User getUser(@RequestBody User user){
        return userDao.getUser(user);
    }

    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> getTransfers(Principal principal){
        String name = principal.getName();
        // this grabs currentUser
        int currentUser = userDao.findIdByUsername(name);
        return transferDao.listTransfers(currentUser);
    }

    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public int getAccountId(Principal principal) {
        String name = principal.getName();
        // this grabs currentUser
        int currentUser = userDao.findIdByUsername(name);
        return transferDao.getAccountId(currentUser);
    }



}
