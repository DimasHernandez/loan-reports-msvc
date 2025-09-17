package co.com.pragma.dynamodb;

import co.com.pragma.model.loanstats.LoanStats;
import co.com.pragma.model.loanstats.gateways.LoanStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.math.BigDecimal;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DynamoLoanStatsAdapter implements LoanStatsRepository {

    private static final String TABLE_NAME = "loan_stats";
    private static final String STATS_ID = "loan_stats_summary_1010";

    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    @Override
    public Mono<LoanStats> getStats() {
        var getRequest = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("statId", AttributeValue.fromS(STATS_ID)))
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.getItem(getRequest))
                .map(response -> {
                    if (!response.hasItem()) return new LoanStats(0, BigDecimal.ZERO);

                    Map<String, AttributeValue> item = response.item();
                    return LoanStats.builder()
                            .totalApprovedLoans(Long.parseLong(item.get("totalApprovedLoans").n()))
                            .totalApprovedAmount(new BigDecimal(item.get("totalApprovedAmount").n()))
                            .build();
                });
    }

    @Override
    public Mono<Void> updateStats(BigDecimal amountToAdd) {
        var updateRequest = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("statId", AttributeValue.fromS(STATS_ID)))
                .updateExpression("SET totalApprovedLoans = if_not_exists(totalApprovedLoans, :zero) + :inc, " +
                        "totalApprovedAmount = if_not_exists(totalApprovedAmount, :zeroAmount) + :amount")
                .expressionAttributeValues(Map.of(
                        ":inc", AttributeValue.fromN("1"),
                        ":amount", AttributeValue.fromN(amountToAdd.toPlainString()),
                        ":zero", AttributeValue.fromN("0"),
                        ":zeroAmount", AttributeValue.fromN("0")
                ))
                .build();

        return Mono.fromFuture(() -> dynamoDbAsyncClient.updateItem(updateRequest)).then();
    }
}
