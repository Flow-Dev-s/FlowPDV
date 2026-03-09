package br.ufpb.dcx.flow.dev.s.model;

import br.ufpb.dcx.flow.dev.s.model.enums.UnitType;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private UnitType unit;

    @Column(unique = true)
    private String barcode;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    public List<StockBatch> getBatches() {
        return batches;
    }

    public void setBatches(List<StockBatch> batches) {
        this.batches = batches;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "product")
    private List<StockBatch> batches;
}
