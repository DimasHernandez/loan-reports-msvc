package co.com.pragma.api;

import co.com.pragma.api.config.LoanPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class LoanRouterRest {

    private final LoanPath loanPath;

    private final LoanHandler loanHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanHandler loanHandler) {
        return route(GET(loanPath.getLoansReports()), this.loanHandler::getLoanStatsUseCase);
    }
}
