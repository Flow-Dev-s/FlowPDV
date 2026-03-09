package br.ufpb.dcx.flow.dev.s.dto;

import java.math.BigDecimal;

public record ItemResponse(
        String productName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}