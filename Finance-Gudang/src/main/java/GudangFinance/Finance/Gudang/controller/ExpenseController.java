package GudangFinance.Finance.Gudang.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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
import GudangFinance.Finance.Gudang.model.ExpenseType;
import GudangFinance.Finance.Gudang.model.PaymentMethod;
import GudangFinance.Finance.Gudang.model.ProductDescription;
import GudangFinance.Finance.Gudang.model.Supplier;
import GudangFinance.Finance.Gudang.repository.ExpenseRepository;
import GudangFinance.Finance.Gudang.repository.ExpenseTypeRepository;
import GudangFinance.Finance.Gudang.repository.PaymentMethodRepository;
import GudangFinance.Finance.Gudang.repository.ProductDescriptionRepository;
import GudangFinance.Finance.Gudang.repository.SupplierRepository;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired private ExpenseRepository expenseRepo;
    @Autowired private ExpenseTypeRepository expenseTypeRepo;
    @Autowired private ProductDescriptionRepository productRepo;
    @Autowired private SupplierRepository supplierRepo;
    @Autowired private PaymentMethodRepository paymentMethodRepo;

    @GetMapping
    public String listExpenses(Model model) {
        model.addAttribute("expenses", expenseRepo.findAll());
        return "expenses/list";
    }

    @GetMapping("/new")
    public String newExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
        model.addAttribute("productOptions", productRepo.findAll());
        model.addAttribute("supplierOptions", supplierRepo.findAll());
        model.addAttribute("paymentMethodOptions", paymentMethodRepo.findAll());
        return "expenses/form";
    }

    @PostMapping
    public String saveExpense(@RequestParam(required = false) Long id,
                              @RequestParam String newType,
                              @RequestParam String product,
                              @RequestParam String supplier,
                              @RequestParam String paymentMethod,
                              @RequestParam(required = false) LocalDate expenseDate,
                              @RequestParam(required = false) Integer quantity,
                              @RequestParam(required = false) BigDecimal pricePerQuantity,
                              @RequestParam(required = false) BigDecimal totalPrice) {

        // Create or get existing expense
        Expense expense = new Expense();
        if (id != null) {
            expense = expenseRepo.findById(id).orElse(new Expense());
        }

        // Handle Expense Type
        ExpenseType expenseType = expenseTypeRepo.findAll().stream()
            .filter(type -> type.getName().equalsIgnoreCase(newType))
            .findFirst()
            .orElseGet(() -> {
                ExpenseType newTypeEntity = new ExpenseType();
                newTypeEntity.setName(newType);
                return expenseTypeRepo.save(newTypeEntity);
            });
        expense.setExpenseType(expenseType);

        // Handle Product
        Optional<ProductDescription> productOpt = productRepo.findByNameIgnoreCase(product);
        ProductDescription productEntity = productOpt.orElseGet(() -> {
            ProductDescription newProduct = new ProductDescription();
            newProduct.setName(product);
            return productRepo.save(newProduct);
        });
        expense.setProduct(productEntity);

        // Handle Supplier
        Optional<Supplier> supplierOpt = supplierRepo.findByNameIgnoreCase(supplier);
        Supplier supplierEntity = supplierOpt.orElseGet(() -> {
            Supplier newSupplier = new Supplier();
            newSupplier.setName(supplier);
            return supplierRepo.save(newSupplier);
        });
        expense.setSupplier(supplierEntity);

        // Handle Payment Method
        Optional<PaymentMethod> paymentOpt = paymentMethodRepo.findByNameIgnoreCase(paymentMethod);
        PaymentMethod paymentEntity = paymentOpt.orElseGet(() -> {
            PaymentMethod newPayment = new PaymentMethod();
            newPayment.setName(paymentMethod);
            return paymentMethodRepo.save(newPayment);
        });
        expense.setPaymentMethod(paymentEntity);

        // Set other fields
        expense.setExpenseDate(expenseDate);
        expense.setQuantity(quantity);
        expense.setPricePerQuantity(pricePerQuantity);
        expense.setTotalPrice(totalPrice);

        expenseRepo.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model) {
        Expense expense = expenseRepo.findById(id).orElseThrow();
        model.addAttribute("expense", expense);
        model.addAttribute("expenseTypes", expenseTypeRepo.findAll());
        model.addAttribute("productOptions", productRepo.findAll());
        model.addAttribute("supplierOptions", supplierRepo.findAll());
        model.addAttribute("paymentMethodOptions", paymentMethodRepo.findAll());
        return "expenses/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id) {
        expenseRepo.deleteById(id);
        return "redirect:/expenses";
    }

    @GetMapping("/search")
    public String searchExpenses(@RequestParam String keyword, Model model) {
        model.addAttribute("expenses", expenseRepo.findByProduct_NameContainingIgnoreCase(keyword));
        return "expenses/list";
    }
}
