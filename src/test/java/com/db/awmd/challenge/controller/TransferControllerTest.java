package com.db.awmd.challenge.controller;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.NotAValidAccountException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

  private MockMvc mockMvc;

  @Mock
  private NotificationService notificationService;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() throws Exception {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-1\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-2\",\"balance\":2000}")).andExpect(status().isCreated());

  }

  @Test
  public void transferSuccessfully() throws Exception {
    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.mockMvc.perform(put("/v1/transfers").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-1\",\"accountToId\":\"Id-2\",\"amount\":100}")).andExpect(status().isOk());

    Thread.sleep(1000);

    Account account1 = accountsService.getAccount("Id-1");
    assertThat(account1.getBalance()).isEqualByComparingTo("900");
    Account account2 = accountsService.getAccount("Id-2");
    assertThat(account2.getBalance()).isEqualByComparingTo("2100");
  }



  @Test
  public void transferWithInsufficientFunds() throws Exception {
    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.mockMvc.perform(put("/v1/transfers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFromId\":\"Id-1\",\"accountToId\":\"Id-2\",\"amount\":1100}"))
            .andExpect(status().isBadRequest())
            .andReturn();
  }

  @Test
  public void transferNonExistentAccount() throws Exception{
    doNothing().when(notificationService).notifyAboutTransfer(any(),any());

    this.mockMvc.perform(put("/v1/transfers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFromId\":\"Id-3\",\"accountToId\":\"Id-2\",\"amount\":1100}"))
            .andExpect(status().isBadRequest());

    Account account1 = accountsService.getAccount("Id-1");
    assertThat(account1.getBalance()).isEqualByComparingTo("1000");
    Account account2 = accountsService.getAccount("Id-2");
    assertThat(account2.getBalance()).isEqualByComparingTo("2000");
    Account account3 = accountsService.getAccount("Id-3");
    assertThat(account3).isNull();

  }


}
