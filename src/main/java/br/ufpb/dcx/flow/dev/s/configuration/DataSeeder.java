package br.ufpb.dcx.flow.dev.s.configuration;

import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.Seller;
import br.ufpb.dcx.flow.dev.s.model.StockBatch;
import br.ufpb.dcx.flow.dev.s.repository.ProductRepository;
import br.ufpb.dcx.flow.dev.s.repository.SellerRepository;
import br.ufpb.dcx.flow.dev.s.repository.StockBatchRepository;
import br.ufpb.dcx.flow.dev.s.model.*;
import br.ufpb.dcx.flow.dev.s.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final StockBatchRepository batchRepository;
    private final SellerRepository sellerRepository;

    public DataSeeder(ProductRepository productRepository, StockBatchRepository batchRepository, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🌱 Iniciando o cadastro de teste...");

        if(productRepository.count() == 0) {
            Product mouse = new Product();
            mouse.setName("Mouse Gamer");
            mouse.setSku("RDG-K617");
            mouse.setBarcode("789000111");
            mouse.setPrice(new BigDecimal("150.00"));

            productRepository.save(mouse);

            StockBatch batch = new StockBatch();
            batch.setProduct(mouse);
            batch.setCurrentQuantity(new BigDecimal("10.000"));
            batch.setExpirationDate(LocalDate.now().plusYears(1));
            batch.setReceivedAt(LocalDate.now());
            batch.setCostPrice(new BigDecimal("80.00"));
            batch.setBatchNumber("BATCH-01");

            batchRepository.save(batch);
        }
        if(sellerRepository.count() == 0) {
            Seller seller1 = new Seller();
            seller1.setCpf("123.456.789-01");
            seller1.setName("Albiere");
            seller1.setId(new Long(1));

        }
        System.out.println("✅ PRODUTO E LOTE CADASTRADOS COM SUCESSO!");
    }
}