package co.com.pragma.model.loanstats;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanStats {

    private long totalApprovedLoans;

    private BigDecimal totalApprovedAmount;
}
