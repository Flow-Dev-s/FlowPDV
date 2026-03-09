package br.ufpb.dcx.flow.dev.s.service;


import br.ufpb.dcx.flow.dev.s.model.CashRegister;
import br.ufpb.dcx.flow.dev.s.repository.CashRegisterRepository;
import br.ufpb.dcx.flow.dev.s.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CashRegisterService {

    private final CashRegisterRepository repository;
    private final SaleRepository saleRepository;

    public CashRegisterService(CashRegisterRepository repository, SaleRepository saleRepository) {
        this.repository = repository;
        this.saleRepository = saleRepository;
    }

    public CashRegister openRegister(BigDecimal amount) {
        CashRegister register = new CashRegister();
        register.setOpeningTime(LocalDateTime.now());
        register.setInitialBalance(amount);
        register.setOpen(true);
        return repository.save(register);
    }

    public Optional<CashRegister> getCashRegisterOpen() {
        return repository.findByOpenTrue();
    }

    @Transactional
    public CashRegister closeRegister() {
        CashRegister current = getCashRegisterOpen()
                .orElseThrow(() -> new RuntimeException("No open register found!"));

        BigDecimal totalSales = saleRepository.calculateTotalRevenueFrom(current.getOpeningTime());
        if (totalSales == null) totalSales = BigDecimal.ZERO;

        current.setFinalBalance(current.getInitialBalance().add(totalSales));
        current.setClosingTime(LocalDateTime.now());
        current.setOpen(false);

        return repository.save(current);
    }
}