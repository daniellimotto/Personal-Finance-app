package GudangFinance.Finance.Gudang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import GudangFinance.Finance.Gudang.model.Expense;
import GudangFinance.Finance.Gudang.repository.ExpenseRepository;
import GudangFinance.Finance.Gudang.repository.ExpenseTypeRepository;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private ExpenseTypeRepository expenseTypeRepo;

    @GetMapping
    public String listExpenses(Model model) {
        model.addAttribute("expenses", expenseRepo.findAll());
        return "expenses/list";
    }

    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
        return "expenses/form";
    }

    @PostMapping
    public String saveExpense(@ModelAttribute Expense expense) {
        expenseRepo.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model) {
        model.addAttribute("expense", expenseRepo.findById(id).orElseThrow());
        model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
        return "expenses/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepo.deleteById(id);
        return "redirect:/expenses";
    }

    @GetMapping("/search")
    public String searchExpenses(@RequestParam String keyword, Model model) {
        model.addAttribute("expenses", expenseRepo.findByRelatedProductContainingIgnoreCase(keyword));
        return "expenses/list";
    }
}
