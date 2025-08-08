package GudangFinance.Finance.Gudang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByNameIgnoreCase(String name);
}   