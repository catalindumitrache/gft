package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NotEnoughFundsException;

import java.util.concurrent.CompletableFuture;

public interface ITransferService {
    CompletableFuture<Transfer> transfer (Transfer transfer) throws NotEnoughFundsException, InterruptedException;
}
