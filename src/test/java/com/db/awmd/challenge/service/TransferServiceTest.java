package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransferServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private ITransferService transferService;

  @Mock
  private NotificationService notificationService;

  @Before
  public void createAccounts() {
    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();

    Account account = new Account("1");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    account = new Account("2");
    account.setBalance(new BigDecimal(2000));
    this.accountsService.createAccount(account);
  }

  @Test
  public void transferCorrectly() throws Exception {
    Transfer transfer = new Transfer("1", "2", BigDecimal.valueOf(100));

    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.transferService.transfer(transfer);
    Thread.sleep(1000);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(BigDecimal.valueOf(900));
  }

  @Test
  public void transferZero() throws Exception {
    Transfer transfer = new Transfer("1", "2", BigDecimal.valueOf(0));

    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.transferService.transfer(transfer);
    Thread.sleep(1000);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(BigDecimal.valueOf(1000));
  }


  @Test
  public void transferMore() throws Exception {
    Transfer transfer = new Transfer("1", "2", BigDecimal.valueOf(5000));

    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.transferService.transfer(transfer);
    Thread.sleep(1000);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(BigDecimal.valueOf(1000));
  }

  @Test
  public void transferSameAccount() throws Exception {
    Transfer transfer = new Transfer("1", "1", BigDecimal.valueOf(100));

    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.transferService.transfer(transfer);
    Thread.sleep(1000);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(BigDecimal.valueOf(1000));
  }

  @Test
  public void transferNonExistentAccount() throws Exception {
    Transfer transfer = new Transfer("1", "15", BigDecimal.valueOf(100));

    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.transferService.transfer(transfer);
    Thread.sleep(1000);
    assertThat(this.accountsService.getAccount("1").getBalance()).isEqualTo(BigDecimal.valueOf(1000));
  }
}
