package GudangFinance.Finance.Gudang.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Sale sale;  // Links to the sale

    private Integer quantityGiven;

    private LocalDateTime dateGiven;

    private String notes;

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Integer getQuantityGiven() {
        return quantityGiven;
    }

    public void setQuantityGiven(Integer quantityGiven) {
        this.quantityGiven = quantityGiven;
    }

    public LocalDateTime getDateGiven() {
        return dateGiven;
    }

    public void setDateGiven(LocalDateTime dateGiven) {
        this.dateGiven = dateGiven;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

