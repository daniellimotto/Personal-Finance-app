package GudangFinance.Finance.Gudang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.ExpenseType;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
    Optional<ExpenseType> findByNameIgnoreCase(String name);
}
