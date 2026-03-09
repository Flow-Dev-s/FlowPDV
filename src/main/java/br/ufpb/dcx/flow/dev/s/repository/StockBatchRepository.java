package br.ufpb.dcx.flow.dev.s.repository;

import br.ufpb.dcx.flow.dev.s.model.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
    @Query("SELECT b FROM StockBatch b WHERE b.product.id = :productId " +
            "AND b.currentQuantity > 0 " +
            "ORDER BY b.expirationDate ASC, b.receivedAt ASC")
    List<StockBatch> findAvailableBatches(Long productId);
}
