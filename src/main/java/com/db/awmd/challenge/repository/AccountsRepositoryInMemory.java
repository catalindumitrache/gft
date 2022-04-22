package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.exception.NotAValidAccountException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  /**
   * Collection of accounts
   */
  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  /**
   * Creates a new account
   *
   * @param account to create
   * @throws DuplicateAccountIdException if the account already exists
   */
  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);

    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  /**
   * Adds the specified amount to the account with id accountId
   *
   * @param accountId of the account
   * @param amount to add
   * @throws NotAValidAccountException if the account is not found in the available accounts
   */
  @Override
  public void updateAccount(String accountId, BigDecimal amount)
          throws NotAValidAccountException, NotEnoughFundsException {
    accounts.compute(accountId, (k, v) -> {
      if(v == null) {
        throw new NotAValidAccountException("Account id " + accountId + " is not a valid account!");
      }

      if(v.getBalance().compareTo(amount.abs()) < 0 && amount.compareTo(BigDecimal.ZERO)<0){
        throw new NotEnoughFundsException("Not enough funds! " +
                "Available funds: " + v.getBalance() +
                " Transfer amount: " + amount.abs());
      }

      v.setBalance(v.getBalance().add(amount));

      return v;
    });
  }

  /**
   * @param accountId to find
   * @return account with the given id
   */
  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  /**
   * Empties the collection of accounts
   */
  @Override
  public void clearAccounts() {
    accounts.clear();
  }

}
