package GudangFinance.Finance.Gudang.controller;

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

import GudangFinance.Finance.Gudang.model.ProductSettlement;
import GudangFinance.Finance.Gudang.model.Sale;
import GudangFinance.Finance.Gudang.repository.ProductSettlementRepository;
import GudangFinance.Finance.Gudang.repository.SaleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product-settlements")
public class ProductSettlementController {

    @Autowired
    private ProductSettlementRepository productSettlementRepo;

    @Autowired
    private SaleRepository saleRepo;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("settlements", productSettlementRepo.findAll());
        model.addAttribute("q", query);
        return "product_settlements/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(value = "q", required = false) String query,
                          @RequestParam(value = "saleId", required = false) Long saleId,
                          Model model) {
        model.addAttribute("settlement", new ProductSettlement());

        List<Sale> selectableSales = saleRepo.findAll();
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase();
            selectableSales = selectableSales.stream().filter(s ->
                (s.getBuyer() != null && s.getBuyer().getName() != null && s.getBuyer().getName().toLowerCase().contains(q))
                || (s.getCompany() != null && s.getCompany().getName() != null && s.getCompany().getName().toLowerCase().contains(q))
                || (s.getProduct() != null && s.getProduct().getName() != null && s.getProduct().getName().toLowerCase().contains(q))
                || (s.getPurchaseDate() != null && s.getPurchaseDate().toString().contains(q))
            ).collect(Collectors.toList());
        }

        // Exclude fully settled sales (remaining <= 0)
        selectableSales = selectableSales.stream().filter(s -> {
            Integer given = productSettlementRepo.sumQuantityGivenBySaleId(s.getId());
            int total = s.getQuantity() != null ? s.getQuantity() : 0;
            int used = given != null ? given : 0;
            return (total - used) > 0;
        }).collect(Collectors.toList());

        model.addAttribute("sales", selectableSales);
        return "product_settlements/form";
    }

    @PostMapping
    public String save(@ModelAttribute ProductSettlement settlement, BindingResult bindingResult, Model model) {
        if (settlement.getSale() == null || settlement.getSale().getId() == null) {
            bindingResult.rejectValue("sale", "sale.required", "Please choose a sale");
        } else if (settlement.getQuantityGiven() == null || settlement.getQuantityGiven() <= 0) {
            bindingResult.rejectValue("quantityGiven", "quantity.invalid", "Quantity must be greater than 0");
        } else {
            Long saleId = settlement.getSale().getId();
            Sale sale = saleRepo.findById(saleId).orElse(null);
            if (sale == null) {
                bindingResult.rejectValue("sale", "sale.notfound", "Selected sale not found");
            } else {
                Integer given = productSettlementRepo.sumQuantityGivenBySaleId(saleId);
                if (given == null) given = 0;
                int remaining = (sale.getQuantity() != null ? sale.getQuantity() : 0) - given;
                if (settlement.getId() != null) {
                    ProductSettlement existing = productSettlementRepo.findById(settlement.getId()).orElse(null);
                    if (existing != null && existing.getQuantityGiven() != null) {
                        remaining += existing.getQuantityGiven();
                    }
                }
                if (settlement.getQuantityGiven() > remaining) {
                    bindingResult.rejectValue("quantityGiven", "quantity.exceeds", "Quantity exceeds remaining (" + remaining + ")");
                }
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("settlement", settlement);
            model.addAttribute("sales", saleRepo.findAll());
            return "product_settlements/form";
        }

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

