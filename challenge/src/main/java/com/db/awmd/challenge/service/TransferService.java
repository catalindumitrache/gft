package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.exception.SameAccountException;
import com.db.awmd.challenge.exception.ZeroAmountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class TransferService implements ITransferService {

    private final AccountsService accountsService;
    private final AccountsRepository accountsRepository;
    private final NotificationService notificationService;

    public TransferService  (AccountsService accountsService, AccountsRepository accountsRepository,
                             EmailNotificationService emailNotificationService) {
        this.accountsService = accountsService;
        this.accountsRepository = accountsRepository;
        this.notificationService = emailNotificationService;
    }

    @Async
    @Override
    public Transfer transfer(Transfer transfer) throws
            NotEnoughFundsException, SameAccountException, ZeroAmountException  {
        Account fromAccount = accountsService.getAccount(transfer.getAccountFromId());
        Account toAccount = accountsService.getAccount(transfer.getAccountToId());

        BigDecimal fromRemainingBalance = fromAccount.getBalance().subtract(transfer.getAmount());

        if(fromRemainingBalance.compareTo(BigDecimal.ZERO) >= 0
                && !transfer.getAccountFromId().equals(transfer.getAccountToId())
                && transfer.getAmount().compareTo(BigDecimal.ZERO) > 0){
            toAccount.setBalance(toAccount.getBalance().add(transfer.getAmount()));
            fromAccount.setBalance(fromRemainingBalance);
            accountsRepository.updateAccount(toAccount);
            accountsRepository.updateAccount(fromAccount);

            notificationService.notifyAboutTransfer(fromAccount,transfer.getAmount() +
                    " transferred to account: " + transfer.getAccountToId());
            notificationService.notifyAboutTransfer(toAccount,"Received " + transfer.getAmount() +
                    " from account: " + transfer.getAccountFromId());
        } else if (fromRemainingBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new NotEnoughFundsException("Not enough funds! " +
                    "Available funds: " + fromAccount.getBalance() +
                    " Transfer amount: " + transfer.getAmount());
        } else if (transfer.getAccountFromId().equals(transfer.getAccountToId())) {
            throw new SameAccountException("Both accounts cannot be the same! " + fromAccount.getAccountId());
        } else if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ZeroAmountException("Amount to transfer cannot be 0");
        }

        return transfer;

    }
}
