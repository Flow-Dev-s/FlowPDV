package br.ufpb.dcx.flow.dev.s.service;

import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.StockBatch;
import br.ufpb.dcx.flow.dev.s.repository.StockBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.List;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StockServiceTest {

    @Mock
    private StockBatchRepository batchRepository;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTotalStock() {
        Product p = new Product();
        p.setId(1L);

        StockBatch lote1 = new StockBatch(); lote1.setCurrentQuantity(new BigDecimal("10.0"));
        StockBatch lote2 = new StockBatch(); lote2.setCurrentQuantity(new BigDecimal("5.0"));

        when(batchRepository.findAvailableBatches(1L)).thenReturn(List.of(lote1, lote2));

        BigDecimal total = stockService.getTotalStock(1L);
        assertEquals(new BigDecimal("15.0"), total);
    }
}