package br.ufpb.dcx.flow.dev.s.service;

import br.ufpb.dcx.flow.dev.s.model.Seller;
import br.ufpb.dcx.flow.dev.s.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Transactional(readOnly = true)
    public String findNameById(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            return sellerRepository.findById(id)
                    .map(Seller::getName)
                    .orElse("Vendedor não encontrado (ID: " + idStr + ")");
        } catch (NumberFormatException e) {
            return "ID Inválido (" + idStr + ")";
        }
    }

    @Transactional(readOnly = true)
    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    @Transactional
    public Seller save(Seller seller) {
        return sellerRepository.save(seller);
    }

    public Optional<Seller> findById(long id) {
        return sellerRepository.findById(id);
    }
}