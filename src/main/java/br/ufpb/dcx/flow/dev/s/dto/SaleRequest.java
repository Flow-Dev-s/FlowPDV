package br.ufpb.dcx.flow.dev.s.dto;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequest(
        List<ItemRequest> items,
        String sellerId,
        BigDecimal discount,
        String customerCpf,
        BigDecimal cashAmount,
        BigDecimal pixAmount,
        BigDecimal cardAmount
) {
    public List<ItemRequest> getItems() { return items; }
}