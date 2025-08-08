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

import GudangFinance.Finance.Gudang.model.Sale;
import GudangFinance.Finance.Gudang.repository.SaleRepository;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleRepository saleRepo;

    @GetMapping
    public String listSales(Model model) {
        model.addAttribute("sales", saleRepo.findAll());
        return "sales/list";
    }

    @GetMapping("/new")
    public String newSaleForm(Model model) {
        model.addAttribute("sale", new Sale());
        return "sales/form";
    }

    @PostMapping
    public String saveSale(@ModelAttribute Sale sale) {
        saleRepo.save(sale);
        return "redirect:/sales";
    }

    @GetMapping("/edit/{id}")
    public String editSale(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleRepo.findById(id).orElseThrow());
        return "sales/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSale(@PathVariable Long id) {
        saleRepo.deleteById(id);
        return "redirect:/sales";
    }

    @GetMapping("/search")
    public String searchSales(@RequestParam String keyword, Model model) {
        model.addAttribute("sales", saleRepo.findByBuyerNameContainingIgnoreCase(keyword));
        return "sales/list";
    }
}

