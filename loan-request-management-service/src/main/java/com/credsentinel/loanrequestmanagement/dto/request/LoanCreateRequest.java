package com.credsentinel.loanrequestmanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class LoanCreateRequest {

    private String userId;

    @NotNull
    private BigDecimal loanAmount;

    @NotNull
    @Min(1)
    private Integer tenureDays;

    @NotBlank
    private String sourceChannel;

    /**
     * Raw request payload for async/risk processing
     * (will be stored as JSONB later)
     */
    @NotNull
    private Map<String, Object> requestPayload;

}
