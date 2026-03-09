package br.ufpb.dcx.flow.dev.s.repository;

import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.model.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
    List<Sale> findByPaymentMethod(PaymentMethod method);
    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate >= :start")
    java.math.BigDecimal calculateTotalRevenueFrom(LocalDateTime start);

}