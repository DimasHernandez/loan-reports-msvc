package co.com.pragma.api;

import co.com.pragma.api.config.LoanPath;
import co.com.pragma.model.loanstats.LoanStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reports",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = LoanHandler.class,
                    beanMethod = "getLoanStatsUseCase",
                    operation = @Operation(
                            operationId = "getLoanStats",
                            summary = "As an administrator user, view the total number of approved loan applications and the total amount of all those loan applications",
                            description = "View statistics on approved loan applications",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "View statistics on approved loan applications",
                                            content = @Content(schema = @Schema(implementation = LoanStats.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(LoanHandler loanHandler) {
        return route(GET(loanPath.getLoansReports()), this.loanHandler::getLoanStatsUseCase);
    }
}
