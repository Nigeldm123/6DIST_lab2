package com.distributedSystems.Banking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount1 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //int for bank id
    private String name;
    private int amount;

    @Repository
    public interface BankRepository extends JpaRepository<AbstractReadWriteAccess.Item, Long> {
        // fill in!
    }

}
