package GudangFinance.Finance.Gudang.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import GudangFinance.Finance.Gudang.repository.ExpenseRepository;
import GudangFinance.Finance.Gudang.repository.ExpenseSettlementRepository;
import GudangFinance.Finance.Gudang.repository.ProductSettlementRepository;
import GudangFinance.Finance.Gudang.repository.SaleRepository;

@Service
public class DashboardService {

    @Autowired
    private ExpenseRepository expenseRepo;
    
    @Autowired
    private SaleRepository saleRepo;
    
    @Autowired
    private ProductSettlementRepository productSettlementRepo;
    
    @Autowired
    private ExpenseSettlementRepository expenseSettlementRepo;

    public Map<String, Object> getDashboardTotals() {
        Map<String, Object> totals = new HashMap<>();
        
        try {
            // Calculate total expenses
            BigDecimal totalExpenses = expenseRepo.findAll().stream()
                .map(expense -> expense.getTotalPrice() != null ? expense.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate total sales
            BigDecimal totalSales = saleRepo.findAll().stream()
                .map(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate total product settlements (based on sale prices)
            BigDecimal totalProductSettlements = productSettlementRepo.findAll().stream()
                .map(ps -> {
                    if (ps.getSale() != null && ps.getSale().getPricePerQuantity() != null && ps.getQuantityGiven() != null) {
                        return ps.getSale().getPricePerQuantity().multiply(BigDecimal.valueOf(ps.getQuantityGiven()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate total expense settlements (based on expense prices)
            BigDecimal totalExpenseSettlements = expenseSettlementRepo.findAll().stream()
                .map(es -> {
                    if (es.getExpense() != null && es.getExpense().getPricePerQuantity() != null && es.getQuantityUsed() != null) {
                        return es.getExpense().getPricePerQuantity().multiply(BigDecimal.valueOf(es.getQuantityUsed()));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate net profit/loss
            BigDecimal netProfit = totalSales.add(totalProductSettlements).subtract(totalExpenses).subtract(totalExpenseSettlements);
            
            totals.put("totalExpenses", totalExpenses);
            totals.put("totalSales", totalSales);
            totals.put("totalProductSettlements", totalProductSettlements);
            totals.put("totalExpenseSettlements", totalExpenseSettlements);
            totals.put("netProfit", netProfit);
            
        } catch (Exception e) {
            // In case of any errors, set all values to zero
            totals.put("totalExpenses", BigDecimal.ZERO);
            totals.put("totalSales", BigDecimal.ZERO);
            totals.put("totalProductSettlements", BigDecimal.ZERO);
            totals.put("totalExpenseSettlements", BigDecimal.ZERO);
            totals.put("netProfit", BigDecimal.ZERO);
        }
        
        return totals;
    }
}
