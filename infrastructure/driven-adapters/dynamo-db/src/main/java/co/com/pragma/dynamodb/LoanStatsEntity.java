package co.com.pragma.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;

@DynamoDbBean
public class LoanStatsEntity {

    private String statId;

    private long totalApprovedLoans;

    private BigDecimal totalApprovedAmount;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("statId")
    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
    }

    @DynamoDbAttribute("totalApprovedLoans")
    public long getTotalApprovedLoans() {
        return totalApprovedLoans;
    }

    public void setTotalApprovedLoans(long totalApprovedLoans) {
        this.totalApprovedLoans = totalApprovedLoans;
    }

    @DynamoDbAttribute("totalApprovedAmount")
    public BigDecimal getTotalApprovedAmount() {
        return totalApprovedAmount;
    }

    public void setTotalApprovedAmount(BigDecimal totalApprovedAmount) {
        this.totalApprovedAmount = totalApprovedAmount;
    }
}
