package br.ufpb.dcx.flow.dev.s.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class CashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private BigDecimal initialBalance;
    private BigDecimal finalBalance;
    private boolean open;

    public CashRegister() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalDateTime openingTime) { this.openingTime = openingTime; }

    public LocalDateTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalDateTime closingTime) { this.closingTime = closingTime; }

    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    public BigDecimal getFinalBalance() { return finalBalance; }
    public void setFinalBalance(BigDecimal finalBalance) { this.finalBalance = finalBalance; }

    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
}