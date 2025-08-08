package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.ProductSettlement;

public interface ProductSettlementRepository extends JpaRepository<ProductSettlement, Long> {
    List<ProductSettlement> findBySale_Id(Long saleId);
}