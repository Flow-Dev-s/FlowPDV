package br.ufpb.dcx.flow.dev.s.service;

import br.ufpb.dcx.flow.dev.s.model.Product;
import br.ufpb.dcx.flow.dev.s.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Busca um produto por código de barras ou SKU.
     * Essencial para a agilidade da loja física.
     */
    @Transactional
    public Product findByCode(String code) {
        return productRepository.findByBarcodeOrSku(code, code)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with code: " + code));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Cadastro de novo produto com validações de negócio.
     */
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists in the database.");
        }
        return productRepository.save(product);
    }

    /**
     * Atualiza o preço de venda atual (Markup/Price Management)
     */
    @Transactional
    public void updatePrice(Long productId, BigDecimal newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        product.setPrice(newPrice);
    }
}