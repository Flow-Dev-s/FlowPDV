package br.ufpb.dcx.flow.dev.s.service;

import br.ufpb.dcx.flow.dev.s.dto.ItemRequest;
import br.ufpb.dcx.flow.dev.s.dto.ItemResponse;
import br.ufpb.dcx.flow.dev.s.dto.SaleRequest;
import br.ufpb.dcx.flow.dev.s.dto.SaleResponse;
import br.ufpb.dcx.lima.albiere.dto.*;
import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.model.Sale;
import br.ufpb.dcx.flow.dev.s.model.SaleItem;
import br.ufpb.dcx.flow.dev.s.repository.SaleRepository;
import br.ufpb.dcx.flow.dev.s.repository.SellerRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {


        private final SaleRepository saleRepository;
        private final ProductService productService;
        private final StockService stockService;
        private final SellerRepository sellerRepository;

        public SaleService(SaleRepository saleRepository, ProductService productService,
                           StockService stockService, SellerRepository sellerRepository) {
            this.saleRepository = saleRepository;
            this.productService = productService;
            this.stockService = stockService;
            this.sellerRepository = sellerRepository;
        }

    @Transactional
    public SaleResponse finishSale(SaleRequest request) {
        Sale sale = new Sale();
        Long idVendedor = Long.parseLong(request.sellerId());
        String nomeVendedor = sellerRepository.findById(idVendedor)
                .map(seller -> seller.getName())
                .orElse("Vendedor Desconhecido (ID: " + request.sellerId() + ")");

        sale.setSellerName(nomeVendedor);
        sale.setSaleDate(LocalDateTime.now());
        sale.setCustomerCPF(request.customerCpf());
        sale.setDiscount(request.discount());

        BigDecimal totalItens = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        for (ItemRequest itemReq : request.items()) {
            Product product = productService.findByCode(itemReq.code());

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.getPrice());

            BigDecimal valorSubtotal = product.getPrice().multiply(itemReq.quantity());
            item.setSubtotal(valorSubtotal);

            totalItens = totalItens.add(valorSubtotal);
            saleItems.add(item);

            stockService.decrementStock(product.getId(), itemReq.quantity());
        }

        sale.setItems(saleItems);
        sale.setTotalAmount(totalItens.subtract(request.discount()));

        String detalhesPgto = String.format("Dinheiro: %.2f | PIX: %.2f | Cartão: %.2f",
                request.cashAmount(), request.pixAmount(), request.cardAmount());
        sale.setPaymentMethod(detalhesPgto);

        saleRepository.save(sale);

        return convertToResponse(sale);
    }

    private SaleResponse convertToResponse(Sale sale) {
        List<ItemResponse> items = sale.getItems().stream()
                .map(item -> new ItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                )).toList();

        return new SaleResponse(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                items
        );
    }

    @Transactional
    public List<Sale> findAll() {
        List<Sale> sales = saleRepository.findAll();
        sales.forEach(sale -> Hibernate.initialize(sale.getItems()));
        return sales;
    }

    @Transactional
    public SaleResponse findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com o ID: " + id));
        return convertToResponse(sale);
    }

    @Transactional
    public List<SaleResponse> findAllResponses() {
        return saleRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }
}