package com.credsentinel.anomaly.component;

import com.loan.anomaly.grpc.LoanAnomalyRequest;
import com.loan.anomaly.grpc.LoanAnomalyResponse;
import com.loan.anomaly.grpc.LoanAnomalyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "grpc.server.in-process-name=test",
        "grpc.client.test-client.address=in-process:test",
        "grpc.client.test-client.negotiation-type=plaintext"
})
@DirtiesContext
public class LoanAnomalyComponentTest {

    @Autowired
    private GrpcServerProperties grpcServerProperties;

    private ManagedChannel channel;
    @GrpcClient("loanService")
    private LoanAnomalyServiceGrpc.LoanAnomalyServiceBlockingStub blockingStub;

    @BeforeEach
    void setUp() {
        this.channel = InProcessChannelBuilder.forName("test")
                .directExecutor()
                .build();
        blockingStub = LoanAnomalyServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
    }

    @Test
    void shouldDetectAnomaly_WhenSyntheticIdentityPatternDetected() {
        // Arrange: A user with 0 history but a perfect credit score (Anomaly Pattern)
        LoanAnomalyRequest request = LoanAnomalyRequest.newBuilder()
                .setLoanId("loan-123")
                .setUserId("user-999")
                .setCreditScore(850)
                .setPreviousLoansCount(0)
                .build();

        // Act: Call the gRPC service over the network
        LoanAnomalyResponse response = blockingStub.detectAnomaly(request);

        // Assert
        assertNotNull(response);
        assertEquals("loan-123", response.getLoanId());
        assertEquals("SUSPICIOUS", response.getStatus());
    }

    @Test
    void shouldReturnNormal_WhenRequestIsStandard() {
        // Arrange
        LoanAnomalyRequest request = LoanAnomalyRequest.newBuilder()
                .setLoanId("loan-456")
                .setUserId("user-111")
                .setCreditScore(700)
                .setPreviousLoansCount(5)
                .setLoanAmount(5000)
                .setMonthlyIncome(4000)
                .build();

        // Act
        LoanAnomalyResponse response = blockingStub.detectAnomaly(request);

        // Assert
        assertEquals("NORMAL", response.getStatus());
    }
}
