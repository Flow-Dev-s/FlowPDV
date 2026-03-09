package br.ufpb.dcx.flow.dev.s.dto;
import java.math.BigDecimal;

public record ItemRequest(String code, BigDecimal quantity) {
    public BigDecimal getQuantity() {return quantity;}
    public String getCode() {return code;}
}