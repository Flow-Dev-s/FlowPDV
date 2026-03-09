package br.ufpb.dcx.flow.dev.s.repository;

import br.ufpb.dcx.flow.dev.s.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByBarcodeOrSku(String barcode, String sku);
    boolean existsBySku(String sku);
}
