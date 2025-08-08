package GudangFinance.Finance.Gudang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByNameIgnoreCase(String name);
}
