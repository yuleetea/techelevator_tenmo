package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    public BigDecimal retrieveBalance(int userId);



}
