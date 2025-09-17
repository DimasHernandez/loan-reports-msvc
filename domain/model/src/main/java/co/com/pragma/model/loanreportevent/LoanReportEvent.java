package co.com.pragma.model.loanreportevent;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LoanReportEvent {

    private UUID loanId;

    private String documentNumber;

    private String status;

    private BigDecimal amount;

    private LocalDateTime updatedAt;
}
