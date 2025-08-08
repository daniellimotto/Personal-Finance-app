package GudangFinance.Finance.Gudang.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import GudangFinance.Finance.Gudang.model.Expense;
import GudangFinance.Finance.Gudang.model.ExpenseSettlement;
import GudangFinance.Finance.Gudang.repository.ExpenseRepository;
import GudangFinance.Finance.Gudang.repository.ExpenseSettlementRepository;

@Controller
@RequestMapping("/expense-settlements")
public class ExpenseSettlementController {

    @Autowired
    private ExpenseSettlementRepository expenseSettlementRepo;

    @Autowired
    private ExpenseRepository expenseRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("settlements", expenseSettlementRepo.findAll());
        return "expense_settlements/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(value = "q", required = false) String query,
                          @RequestParam(value = "expenseId", required = false) Long expenseId,
                          Model model) {
        ExpenseSettlement settlement = new ExpenseSettlement();
        model.addAttribute("settlement", settlement);

        List<Expense> allExpenses = expenseRepo.findAll();
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            allExpenses = allExpenses.stream().filter(e ->
                (e.getProduct() != null && e.getProduct().getName() != null && e.getProduct().getName().toLowerCase().contains(q))
                || (e.getExpenseDate() != null && e.getExpenseDate().toString().contains(q))
                || (e.getExpenseType() != null && e.getExpenseType().getName() != null && e.getExpenseType().getName().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }
        model.addAttribute("expenses", allExpenses);

        if (expenseId != null) {
            Expense expense = expenseRepo.findById(expenseId).orElse(null);
            if (expense != null) {
                Integer usedUnits = expenseSettlementRepo.sumQuantityUsedByExpenseId(expenseId);
                if (usedUnits == null) usedUnits = 0;
                int remainingUnits = (expense.getQuantity() != null ? expense.getQuantity() : 0) - usedUnits;
                model.addAttribute("remainingUnits", remainingUnits);
                settlement.setExpense(expense);
            }
        }

        return "expense_settlements/form";
    }

    @PostMapping
    public String save(@ModelAttribute ExpenseSettlement settlement, BindingResult bindingResult, Model model) {
        if (settlement.getExpense() == null || settlement.getExpense().getId() == null) {
            bindingResult.rejectValue("expense", "expense.required", "Please choose an expense");
        } else if (settlement.getQuantityUsed() == null || settlement.getQuantityUsed() <= 0) {
            bindingResult.rejectValue("quantityUsed", "quantity.invalid", "Quantity used must be greater than 0");
        } else {
            Long expenseId = settlement.getExpense().getId();
            Expense expense = expenseRepo.findById(expenseId).orElse(null);
            if (expense == null) {
                bindingResult.rejectValue("expense", "expense.notfound", "Selected expense not found");
            } else {
                Integer usedUnits = expenseSettlementRepo.sumQuantityUsedByExpenseId(expenseId);
                if (usedUnits == null) usedUnits = 0;
                int remainingUnits = (expense.getQuantity() != null ? expense.getQuantity() : 0) - usedUnits;
                if (settlement.getId() != null) {
                    // If editing, add back previous quantity before validation
                    var existing = expenseSettlementRepo.findById(settlement.getId()).orElse(null);
                    if (existing != null && existing.getQuantityUsed() != null) {
                        remainingUnits += existing.getQuantityUsed();
                    }
                }
                if (settlement.getQuantityUsed() > remainingUnits) {
                    bindingResult.rejectValue("quantityUsed", "quantity.exceeds", "Quantity exceeds remaining (" + remainingUnits + ")");
                }
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("settlement", settlement);
            model.addAttribute("expenses", expenseRepo.findAll());
            return "expense_settlements/form";
        }

        expenseSettlementRepo.save(settlement);
        return "redirect:/expense-settlements";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        ExpenseSettlement settlement = expenseSettlementRepo.findById(id).orElseThrow();
        model.addAttribute("settlement", settlement);
        model.addAttribute("expenses", expenseRepo.findAll());
        return "expense_settlements/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        expenseSettlementRepo.deleteById(id);
        return "redirect:/expense-settlements";
    }
}

