package co.com.pragma.sqs.listener.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.metrics.LoggingMetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SQSLoanApprovedConfigTest {

    @InjectMocks
    private SQSLoanApprovedConfig sqsLoanApprovedConfig;

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private SQSLoanApprovedProperties sqsLoanApprovedProperties;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(sqsLoanApprovedProperties.region()).thenReturn("us-east-1");
        when(sqsLoanApprovedProperties.queueUrl()).thenReturn("http://localhost:4566/00000000000/queue-sqs");
        when(sqsLoanApprovedProperties.waitTimeSeconds()).thenReturn(20);
        when(sqsLoanApprovedProperties.maxNumberOfMessages()).thenReturn(10);
        when(sqsLoanApprovedProperties.numberOfThreads()).thenReturn(1);
    }

    @Test
    void configSQSListenerIsNotNull() {
        assertThat(sqsLoanApprovedConfig.sqsListener(sqsAsyncClient, sqsLoanApprovedProperties, message -> Mono.empty())).isNotNull();
    }

    @Test
    void configSqsIsNotNull() {
        var loggingMetricPublisher = LoggingMetricPublisher.create();
        assertThat(sqsLoanApprovedConfig.configSqs(sqsLoanApprovedProperties, loggingMetricPublisher)).isNotNull();
    }

    @Test
    void configSqsWhenEndpointIsNotNull() {
        var loggingMetricPublisher = LoggingMetricPublisher.create();
        when(sqsLoanApprovedProperties.endpoint()).thenReturn("http://localhost:4566");
        assertThat(sqsLoanApprovedConfig.configSqs(sqsLoanApprovedProperties, loggingMetricPublisher)).isNotNull();
    }

    @Test
    void resolveEndpointIsNull() {
        assertThat(sqsLoanApprovedConfig.resolveEndpoint(sqsLoanApprovedProperties)).isNull();
    }
}
