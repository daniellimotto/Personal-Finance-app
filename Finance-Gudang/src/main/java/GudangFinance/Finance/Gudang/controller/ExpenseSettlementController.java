package GudangFinance.Finance.Gudang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String newForm(Model model) {
        model.addAttribute("settlement", new ExpenseSettlement());
        model.addAttribute("expenses", expenseRepo.findAll());
        return "expense_settlements/form";
    }

    @PostMapping
    public String save(@ModelAttribute ExpenseSettlement settlement) {
        expenseSettlementRepo.save(settlement);
        return "redirect:/expense-settlements";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("settlement", expenseSettlementRepo.findById(id).orElseThrow());
        model.addAttribute("expenses", expenseRepo.findAll());
        return "expense_settlements/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        expenseSettlementRepo.deleteById(id);
        return "redirect:/expense-settlements";
    }
}

