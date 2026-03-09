package br.ufpb.dcx.flow.dev.s.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        LocalDateTime saleDate,
        BigDecimal totalAmount,
        String paymentMethod,
        List<ItemResponse> items
) {}

