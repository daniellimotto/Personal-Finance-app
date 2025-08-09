package GudangFinance.Finance.Gudang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import GudangFinance.Finance.Gudang.model.ProductSettlement;

public interface ProductSettlementRepository extends JpaRepository<ProductSettlement, Long> {
    List<ProductSettlement> findBySale_Id(Long saleId);

    @Query("SELECT COALESCE(SUM(ps.quantityGiven), 0) FROM ProductSettlement ps WHERE ps.sale.id = :saleId")
    Integer sumQuantityGivenBySaleId(@Param("saleId") Long saleId);
}