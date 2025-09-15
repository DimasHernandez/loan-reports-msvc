package co.com.pragma.sqs.listener;

import co.com.pragma.model.loanreportevent.LoanReportEvent;
import co.com.pragma.usecase.loanapproved.LoanApprovedUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSLoanApprovedProcessor implements Function<Message, Mono<Void>> {

    private final LoanApprovedUseCase loanApprovedUseCase;
    private final ObjectMapper mapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Received message from SQS: {}", message.body());

        return Mono.fromCallable(() -> mapper.readValue(message.body(), LoanReportEvent.class))
                .flatMap(loanApprovedUseCase::processMessageApprovedLoanApplicationsReportQueue)
                .doOnSuccess(v -> log.info("Message processed successfully"))
                .doOnError(e -> log.error("Error processing message: {}", e.getMessage(), e))
                .onErrorResume(e -> Mono.empty());
    }
}
