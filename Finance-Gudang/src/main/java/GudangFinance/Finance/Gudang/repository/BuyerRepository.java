package GudangFinance.Finance.Gudang.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import GudangFinance.Finance.Gudang.model.Buyer;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByNameIgnoreCase(String name);
}
