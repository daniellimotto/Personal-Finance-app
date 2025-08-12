package GudangFinance.Finance.Gudang.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import GudangFinance.Finance.Gudang.model.Buyer;
import GudangFinance.Finance.Gudang.model.Company;
import GudangFinance.Finance.Gudang.model.PaymentMethod;
import GudangFinance.Finance.Gudang.model.Product;
import GudangFinance.Finance.Gudang.model.Sale;
import GudangFinance.Finance.Gudang.repository.BuyerRepository;
import GudangFinance.Finance.Gudang.repository.CompanyRepository;
import GudangFinance.Finance.Gudang.repository.PaymentMethodRepository;
import GudangFinance.Finance.Gudang.repository.ProductRepository;
import GudangFinance.Finance.Gudang.repository.SaleRepository;
import GudangFinance.Finance.Gudang.repository.ProductSettlementRepository;

@Controller
@RequestMapping("/sales")
public class SaleController {

    @Autowired private SaleRepository saleRepo;
    @Autowired private PaymentMethodRepository paymentMethodRepo;
    @Autowired private CompanyRepository companyRepo;
    @Autowired private BuyerRepository buyerRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private ProductSettlementRepository productSettlementRepo;

    @GetMapping
    public String listSales(Model model) {
        model.addAttribute("sales", saleRepo.findAll());
        return "sales/list";
    }

    @GetMapping("/new")
    public String newSaleForm(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("companyOptions", companyRepo.findAll());
        model.addAttribute("buyerOptions", buyerRepo.findAll());
        model.addAttribute("productOptions", productRepo.findAll());
        model.addAttribute("paymentMethodOptions", paymentMethodRepo.findAll());
        return "sales/form";
    }

    @PostMapping
    public String saveSale(@RequestParam(required = false) Long id,
                           @RequestParam String code,
                           @RequestParam String companyName,
                           @RequestParam String buyerName,
                           @RequestParam String productName,
                           @RequestParam Integer quantity,
                           @RequestParam BigDecimal pricePerQuantity,
                           @RequestParam BigDecimal totalAmount,
                           @RequestParam LocalDate purchaseDate,
                           @RequestParam String paymentMethod) {
        Sale sale = (id != null) ? saleRepo.findById(id).orElse(new Sale()) : new Sale();
        sale.setCode(code);

        Company company = companyRepo.findByNameIgnoreCase(companyName)
            .orElseGet(() -> companyRepo.save(newCompany(companyName)));
        sale.setCompany(company);

        Buyer buyer = buyerRepo.findByNameIgnoreCase(buyerName)
            .orElseGet(() -> buyerRepo.save(newBuyer(buyerName)));
        sale.setBuyer(buyer);

        Product product = productRepo.findByNameIgnoreCase(productName)
            .orElseGet(() -> productRepo.save(newProduct(productName)));
        sale.setProduct(product);

        sale.setQuantity(quantity);
        sale.setPricePerQuantity(pricePerQuantity);
        sale.setTotalAmount(totalAmount);
        sale.setPurchaseDate(purchaseDate);

        PaymentMethod paymentEntity = paymentMethodRepo.findByNameIgnoreCase(paymentMethod)
            .orElseGet(() -> {
                PaymentMethod pm = new PaymentMethod();
                pm.setName(paymentMethod);
                return paymentMethodRepo.save(pm);
            });
        sale.setPaymentMethod(paymentEntity);

        saleRepo.save(sale);
        return "redirect:/sales";
    }

    private Company newCompany(String name) {
        Company c = new Company();
        c.setName(name);
        return c;
    }

    private Buyer newBuyer(String name) {
        Buyer b = new Buyer();
        b.setName(name);
        return b;
    }

    private Product newProduct(String name) {
        Product p = new Product();
        p.setName(name);
        return p;
    }

    @GetMapping("/edit/{id}")
    public String editSale(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleRepo.findById(id).orElseThrow());
        model.addAttribute("companyOptions", companyRepo.findAll());
        model.addAttribute("buyerOptions", buyerRepo.findAll());
        model.addAttribute("productOptions", productRepo.findAll());
        model.addAttribute("paymentMethodOptions", paymentMethodRepo.findAll());
        return "sales/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteSale(@PathVariable Long id) {
        // Remove child product settlements to avoid FK constraint violations
        var children = productSettlementRepo.findBySale_Id(id);
        if (children != null && !children.isEmpty()) {
            productSettlementRepo.deleteAll(children);
        }
        saleRepo.deleteById(id);
        return "redirect:/sales";
    }

    @GetMapping("/search")
    public String searchSales(@RequestParam String keyword, Model model) {
        model.addAttribute("sales", saleRepo.findByBuyer_NameContainingIgnoreCase(keyword));
        return "sales/list";
    }
}

