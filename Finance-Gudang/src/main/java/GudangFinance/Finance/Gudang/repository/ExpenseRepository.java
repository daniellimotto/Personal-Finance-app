package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByProduct_NameContainingIgnoreCase(String productName);
}