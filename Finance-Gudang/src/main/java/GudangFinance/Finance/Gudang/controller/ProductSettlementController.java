package GudangFinance.Finance.Gudang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import GudangFinance.Finance.Gudang.model.ProductSettlement;
import GudangFinance.Finance.Gudang.repository.ProductSettlementRepository;
import GudangFinance.Finance.Gudang.repository.SaleRepository;

@Controller
@RequestMapping("/product-settlements")
public class ProductSettlementController {

    @Autowired
    private ProductSettlementRepository productSettlementRepo;

    @Autowired
    private SaleRepository saleRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("settlements", productSettlementRepo.findAll());
        return "product_settlements/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("settlement", new ProductSettlement());
        model.addAttribute("sales", saleRepo.findAll());
        return "product_settlements/form";
    }

    @PostMapping
    public String save(@ModelAttribute ProductSettlement settlement) {
        productSettlementRepo.save(settlement);
        return "redirect:/product-settlements";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("settlement", productSettlementRepo.findById(id).orElseThrow());
        model.addAttribute("sales", saleRepo.findAll());
        return "product_settlements/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productSettlementRepo.deleteById(id);
        return "redirect:/product-settlements";
    }
}

