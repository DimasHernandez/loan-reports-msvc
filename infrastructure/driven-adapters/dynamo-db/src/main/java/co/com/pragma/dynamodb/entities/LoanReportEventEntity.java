package co.com.pragma.dynamodb.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class LoanReportEventEntity {

    private UUID loanId;

    private String documentNumber;

    private String status;

    private BigDecimal amount;

    private LocalDateTime updatedAt;

    public LoanReportEventEntity() {
    }

    public LoanReportEventEntity(UUID loanId, String documentNumber, String status, BigDecimal amount, LocalDateTime updatedAt) {
        this.loanId = loanId;
        this.documentNumber = documentNumber;
        this.status = status;
        this.amount = amount;
        this.updatedAt = updatedAt;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("loanId")
    public UUID getLoanId() {
        return loanId;
    }

    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }

    @DynamoDbAttribute("documentNumber")
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @DynamoDbAttribute("updatedAt")
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
