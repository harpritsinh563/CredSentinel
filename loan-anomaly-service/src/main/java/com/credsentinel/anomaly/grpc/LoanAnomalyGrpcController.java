package com.credsentinel.anomaly.grpc;

import com.credsentinel.anomaly.service.AnomalyDetectionEngine;
import com.loan.anomaly.grpc.LoanAnomalyRequest;
import com.loan.anomaly.grpc.LoanAnomalyResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.credsentinel.anomaly.constants.AnomalyDecisionStatus.NORMAL;
import static com.credsentinel.anomaly.constants.AnomalyDecisionStatus.SUSPICIOUS;

@Slf4j
@RequiredArgsConstructor
public class LoanAnomalyGrpcController {

    private final AnomalyDetectionEngine anomalyEngine;

    public void detectAnomaly(LoanAnomalyRequest request, StreamObserver<LoanAnomalyResponse> responseObserver) {
        log.info("Received anomaly check request for Loan ID: {} and User ID: {}",
                request.getLoanId(), request.getUserId());

        try {
            // 1. Execute Business Logic
            boolean isSuspicious = anomalyEngine.isSuspicious(request);
            String status = isSuspicious ? SUSPICIOUS.name() : NORMAL.name();

            // 2. Build gRPC Response
            LoanAnomalyResponse response = LoanAnomalyResponse.newBuilder()
                    .setLoanId(request.getLoanId())
                    .setStatus(status)
                    .build();

            // 3. Send and Complete
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Anomaly check completed for Loan ID: {}. Result: {}", request.getLoanId(), status);

        } catch (Exception e) {
            log.error("Error processing anomaly check", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Internal error during anomaly detection")
                    .asRuntimeException());
        }
    }
}