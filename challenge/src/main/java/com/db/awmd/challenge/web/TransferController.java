package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.NotAValidAccountException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.service.ITransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/transfers")
@Slf4j
public class TransferController {

  private final ITransferService transferService;

  @Autowired
  public TransferController(ITransferService transferService) {
    this.transferService = transferService;
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> transfer(@RequestBody @Valid Transfer transfer) {
    log.info("Transfer {}", transfer);
    try {
      this.transferService.transfer(transfer);
    }
    catch(NotAValidAccountException | NotEnoughFundsException exception){
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    catch (InterruptedException interruptedException){
      return new ResponseEntity<>(interruptedException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
