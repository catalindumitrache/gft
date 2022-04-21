package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NotAValidAccountException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.exception.SameAccountException;
import com.db.awmd.challenge.exception.ZeroAmountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

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

        if(!Objects.isNull(fromAccount) && !Objects.isNull(toAccount) && !transfer.getAccountFromId().equals(transfer.getAccountToId())) {
            accountsRepository.updateAccount(transfer.getAccountFromId(), BigDecimal.ZERO.subtract(transfer.getAmount()));
            accountsRepository.updateAccount(transfer.getAccountToId(), transfer.getAmount());

            notificationService.notifyAboutTransfer(fromAccount,transfer.getAmount() +
                    " transferred to account: " + transfer.getAccountToId());
            notificationService.notifyAboutTransfer(toAccount,"Received " + transfer.getAmount() +
                    " from account: " + transfer.getAccountFromId());

        } else if (transfer.getAccountFromId().equals(transfer.getAccountToId())) {
            throw new SameAccountException("Both accounts cannot be the same! " + fromAccount.getAccountId());
        } else if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ZeroAmountException("Amount to transfer cannot be 0!");
        } else if (Objects.isNull(fromAccount)) {
            throw new NotAValidAccountException("Account " + fromAccount.getAccountId() + " is not a valid account!");
        } else if (Objects.isNull(toAccount)) {
            throw new NotAValidAccountException("Account " + toAccount.getAccountId() + " is not a valid account!");
        }

        return transfer;
    }

}
