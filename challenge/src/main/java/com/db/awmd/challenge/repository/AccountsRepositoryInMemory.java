package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.db.awmd.challenge.exception.NotAValidAccountException;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);

    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public void updateAccount(Account account) throws NotAValidAccountException {
    accounts.compute(account.getAccountId(), (k, v) -> {
      if(v == null) {
        throw new NotAValidAccountException("Account id " + account.getAccountId() + " is not a valid account!");
      }
        return account;
    });
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

}
