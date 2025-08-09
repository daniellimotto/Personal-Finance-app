package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByBuyer_NameContainingIgnoreCase(String buyerName);
    List<Sale> findByCompany_NameContainingIgnoreCase(String companyName);
    List<Sale> findByProduct_NameContainingIgnoreCase(String productName);
}