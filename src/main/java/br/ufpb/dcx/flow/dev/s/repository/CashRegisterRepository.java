package br.ufpb.dcx.flow.dev.s.repository;

import br.ufpb.dcx.flow.dev.s.model.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CashRegisterRepository extends JpaRepository<CashRegister, Long> {
    Optional<CashRegister> findByOpenTrue();
}