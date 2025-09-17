package co.com.pragma.api;

import co.com.pragma.usecase.loanstats.LoanStatsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanHandler {

    private final LoanStatsUseCase useCase;

    public Mono<ServerResponse> getLoanStatsUseCase(ServerRequest serverRequest) {
        return useCase.getLoanStats()
                .flatMap(stats -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(stats));
    }
}
