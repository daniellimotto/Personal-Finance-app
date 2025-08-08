package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.ExpenseSettlement;

public interface ExpenseSettlementRepository extends JpaRepository<ExpenseSettlement, Long> {
    List<ExpenseSettlement> findByExpense_Id(Long expenseId);
}