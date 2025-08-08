package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.Expense;
import GudangFinance.Finance.Gudang.model.ExpenseType;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByRelatedProductContainingIgnoreCase(String productName);
}