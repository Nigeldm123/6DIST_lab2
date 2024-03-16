package com.distributedSystems.Banking;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.NoSuchElementException;

@Configuration
@SpringBootApplication

public class BankingAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(BankingAppApplication.class, args);
	}
}

@RestController
class BankAccountController {

	@Autowired
	BankRepository bankRepository;

	@RequestMapping("/bankAccounts")
	Collection<BankAccount> bankAccounts() {
		System.out.println("request received");
		return this.bankRepository.findAll();
	}

	@RequestMapping("/bankAccounts/{id}/balance")
	BankAccount getBalance(@PathVariable("id") long id){
		BankAccount bankAccount = bankRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank account not found"));
		bankAccount.getBalance();
		return bankAccount;
	}

	@PostMapping("/bankAccounts/{id}/withdraw")
	BankAccount withdraw(@PathVariable("id") long id, @RequestParam("amount") double amount) {
		BankAccount bankAccount = bankRepository.findById(id).orElseThrow(() -> new RuntimeException("Bank account not found"));
		bankAccount.withdraw(amount);
		bankRepository.save(bankAccount);
		return bankAccount;
	}

	@PostMapping("/bankAccounts/{name}/add")
	public ResponseEntity<?> add(@PathVariable("name") String name) {
		// Check if an account with the same name already exists
		if (bankRepository.findByName(name).isEmpty()) {
			// Create a new bank account with an initial amount of 0.0
			BankAccount bankAccount = new BankAccount(name, 0.0);

			// Save the new bank account to the repository
			bankRepository.save(bankAccount);

			return ResponseEntity.ok(bankAccount);
		} else {
			// Account with the same name already exists, return an error response
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Bank account with name " + name + " already exists");
		}
	}

	@RequestMapping("/bankAccounts/{name}")
	BankAccount getAccountByName(@PathVariable("name") String name){
		Collection<BankAccount> accounts = bankRepository.findByName(name);
		if (accounts.isEmpty()) {
			throw new RuntimeException("Bank account with name " + name + " not found");
		}
		return accounts.iterator().next();
	}

	@PostMapping("/bankAccounts/{identifier}/deposit")
	ResponseEntity<BankAccount> deposit(
			@PathVariable("identifier") String identifier,
			@RequestParam("amount") double amount
	) {
		try {
			BankAccount bankAccount;

			// Check if the identifier is a number (assuming it's an ID)
			if (identifier.matches("\\d+")) {
				long id = Long.parseLong(identifier);
				bankAccount = bankRepository.findById(id).orElseThrow();
			} else {
				// Assume it's a name
				bankAccount = bankRepository.findByName(identifier).stream().findFirst().orElseThrow();
			}

			bankAccount.deposit(amount);
			bankRepository.save(bankAccount);

			return ResponseEntity.ok(bankAccount);
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
}

@Component
class BankApplicationCommandLineRunner implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		// Save BankAccount entities to the database
		this.bankRepository.save(new BankAccount("Nigel",100.0));
		this.bankRepository.save(new BankAccount("Femke",150.0));
		this.bankRepository.save(new BankAccount("Gones",0.0));
		this.bankRepository.save(new BankAccount("Rien",180.0));

		// Print all BankAccount entities
		for (BankAccount b : this.bankRepository.findAll()) {
			System.out.println(b.toString());
		}
	}

	@Autowired
	BankRepository bankRepository;
}

@Repository
interface BankRepository extends JpaRepository<BankAccount, Long> {
	Collection<BankAccount> findByName(String name);
}

@Entity
class BankAccount {
	@Id
	@GeneratedValue
	private long id;
	private String name;
	private double amount;

	public BankAccount(String name, double amount) {
		super();
		this.name = name;
		this.amount = amount;
	}
	public BankAccount() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return amount;
	}

	public void deposit(double amount) {
		if (amount > 0) {
			this.amount += amount;
		} else {
			throw new IllegalArgumentException("Amount must be positive");
		}
	}

	public void withdraw(double amount) {
		if (amount > 0 && amount <= this.amount) {
			this.amount -= amount;
		} else {
			throw new IllegalArgumentException("Invalid withdrawal amount");
		}
	}

	@Override
	public String toString() {
		return "BankAccount{" +
				"id=" + id +
				", name='" + name + '\'' +
				", balance=" + amount +
				'}';
	}
}