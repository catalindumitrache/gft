package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.NotAValidAccountException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  void updateAccount(Account account) throws NotAValidAccountException;

  Account getAccount(String accountId);

  void clearAccounts();
}
