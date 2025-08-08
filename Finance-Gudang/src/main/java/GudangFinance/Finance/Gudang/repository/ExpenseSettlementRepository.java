package GudangFinance.Finance.Gudang.repository;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import GudangFinance.Finance.Gudang.model.ExpenseSettlement;

public interface ExpenseSettlementRepository extends JpaRepository<ExpenseSettlement, Long> {
    List<ExpenseSettlement> findByExpense_Id(Long expenseId);

    @Query("SELECT COALESCE(SUM(es.amountUsed), 0) FROM ExpenseSettlement es WHERE es.expense.id = :expenseId")
    BigDecimal sumAmountUsedByExpenseId(@Param("expenseId") Long expenseId);

    @Query("SELECT COALESCE(SUM(es.quantityUsed), 0) FROM ExpenseSettlement es WHERE es.expense.id = :expenseId")
    Integer sumQuantityUsedByExpenseId(@Param("expenseId") Long expenseId);
}