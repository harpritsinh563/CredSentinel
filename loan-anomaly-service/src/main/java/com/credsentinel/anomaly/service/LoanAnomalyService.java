package com.credsentinel.anomaly.service;

import com.loan.anomaly.grpc.LoanAnomalyRequest;
import com.loan.anomaly.grpc.LoanAnomalyResponse;
import com.loan.anomaly.grpc.LoanAnomalyServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService // This registers the service with the gRPC server automatically
@RequiredArgsConstructor
public class LoanAnomalyService extends LoanAnomalyServiceGrpc.LoanAnomalyServiceImplBase {

    private final AnomalyDetectionEngine anomalyEngine;

    @Override
    public void detectAnomaly(LoanAnomalyRequest request, StreamObserver<LoanAnomalyResponse> responseObserver) {
        log.info("Processing gRPC Anomaly Check for Loan ID: {}", request.getLoanId());

        try {
            // 1. Call our business logic engine
            boolean isSuspicious = anomalyEngine.isSuspicious(request);

            // 2. Map boolean to our proto-defined Status (NORMAL / SUSPICIOUS)
            String statusValue = isSuspicious ? "SUSPICIOUS" : "NORMAL";

            // 3. Build the response using the generated builder
            LoanAnomalyResponse response = LoanAnomalyResponse.newBuilder()
                    .setLoanId(request.getLoanId())
                    .setStatus(statusValue)
                    .build();

            // 4. Send the response back to the client (Decision Service)
            responseObserver.onNext(response);

            // 5. Signal that we are done
            responseObserver.onCompleted();

            log.info("Finished Anomaly Check for Loan ID: {}. Result: {}", request.getLoanId(), statusValue);

        } catch (Exception e) {
            log.error("Failed to process anomaly check", e);
            // In gRPC, you send errors via the observer, not by throwing exceptions
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal Anomaly Service Error")
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}