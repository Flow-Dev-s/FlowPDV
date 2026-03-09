package br.ufpb.dcx.flow.dev.s.service;

import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.StockBatch;
import br.ufpb.dcx.flow.dev.s.repository.ProductRepository;
import br.ufpb.dcx.flow.dev.s.repository.StockBatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StockService {
    private final StockBatchRepository batchRepository;
    private final ProductRepository productRepository;

    public StockService(StockBatchRepository batchRepository, ProductRepository productRepository) {
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
    }

    /**
     * Baixa a quantidade do estoque utilizando a estratégia FEFO (First Expired, First Out).
     * @param productId ID do produto vendido
     * @param quantityToSell Quantidade total a ser subtraída
     */
    @Transactional
    public void decrementStock(Long productId, BigDecimal quantityToSell) {
        List<StockBatch> availableBatches = batchRepository.findAvailableBatches(productId);

        BigDecimal totalAvailable = availableBatches.stream()
                .map(StockBatch::getCurrentQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAvailable.compareTo(quantityToSell) < 0) {
            throw new RuntimeException("Insufficient total stock for product ID: " + productId);
        }

        BigDecimal remainingToDecrement = quantityToSell;

        for (StockBatch batch : availableBatches) {
            if (remainingToDecrement.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal batchQty = batch.getCurrentQuantity();

            if (batchQty.compareTo(remainingToDecrement) <= 0) {
                remainingToDecrement = remainingToDecrement.subtract(batchQty);
                batch.setCurrentQuantity(BigDecimal.ZERO);
            } else {
                batch.setCurrentQuantity(batchQty.subtract(remainingToDecrement));
                remainingToDecrement = BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getTotalStock(Long id) {
        List<StockBatch> availableBatches = batchRepository.findAvailableBatches(id);
        if (availableBatches == null || availableBatches.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return availableBatches.stream()
                .map(StockBatch::getCurrentQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void addStock(Long productId, BigDecimal quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        StockBatch batch = new StockBatch();
        batch.setProduct(product);
        batch.setCurrentQuantity(quantity);

        batchRepository.save(batch);
    }
}
