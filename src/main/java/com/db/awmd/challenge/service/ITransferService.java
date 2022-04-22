package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NotAValidAccountException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.exception.SameAccountException;
import com.db.awmd.challenge.exception.ZeroAmountException;

import java.util.concurrent.CompletableFuture;

public interface ITransferService {
    void transfer (Transfer transfer) throws NotEnoughFundsException, SameAccountException,
            ZeroAmountException, NotAValidAccountException;
}
