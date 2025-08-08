package GudangFinance.Finance.Gudang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.ProductDescription;

public interface ProductDescriptionRepository extends JpaRepository<ProductDescription, Long> {
    Optional<ProductDescription> findByNameIgnoreCase(String name);
}