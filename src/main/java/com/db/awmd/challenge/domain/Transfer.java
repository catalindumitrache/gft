package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Transfer {

  @NotNull
  @NotEmpty
  private final String accountFromId;

  @NotNull
  @NotEmpty
  private final String accountToId;

  @NotNull
  @Min(value = 0, message = "Amount must be positive.")
  private BigDecimal amount;

  @JsonCreator
  public Transfer(@JsonProperty("accountFromId") String accountFromId,
                  @JsonProperty("accountToId") String accountToId,
                  @JsonProperty("amount") BigDecimal amount) {
    this.accountFromId = accountFromId;
    this.accountToId = accountToId;
    this.amount = amount;
  }
}
