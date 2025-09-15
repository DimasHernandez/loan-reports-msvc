package co.com.pragma.model.loanreportevent.gateways;

import co.com.pragma.model.loanreportevent.LoanReportEvent;
import reactor.core.publisher.Mono;

public interface LoanReportEventRepository {

    Mono<LoanReportEvent> save(LoanReportEvent event);
}
