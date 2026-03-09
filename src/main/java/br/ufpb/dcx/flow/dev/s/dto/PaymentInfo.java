package br.ufpb.dcx.flow.dev.s.dto;

import java.math.BigDecimal;

public record PaymentInfo(
        String sellerId,
        BigDecimal discount,
        String customerCpf,
        BigDecimal cashAmount,
        BigDecimal pixAmount,
        BigDecimal cardAmount
) {}