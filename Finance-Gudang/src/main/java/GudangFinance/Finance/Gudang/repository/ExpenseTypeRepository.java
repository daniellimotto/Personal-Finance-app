package GudangFinance.Finance.Gudang.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.ExpenseType;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
}
