package br.ufpb.dcx.flow.dev.s.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_batches")
public class StockBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String batchNumber;


    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal currentQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    @Column(name = "received_at", nullable = false)
    private LocalDate receivedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDate receivedAt) {
        this.receivedAt = receivedAt;
    }

    public StockBatch() {}

    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    public boolean isNearExpiration(int daysThreshold) {
        if (expirationDate == null) return false;
        return expirationDate.isBefore(LocalDate.now().plusDays(daysThreshold)) && !isExpired();
    }

    public void subtractQuantity(BigDecimal amount) {
        if (amount.compareTo(currentQuantity) > 0) {
            throw new IllegalArgumentException("Insufficient stock in this batch");
        }
        this.currentQuantity = this.currentQuantity.subtract(amount);
    }
}