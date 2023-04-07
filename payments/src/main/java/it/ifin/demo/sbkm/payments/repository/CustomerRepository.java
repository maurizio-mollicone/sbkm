package it.ifin.demo.sbkm.payments.repository;

import org.springframework.data.repository.CrudRepository;

import it.ifin.demo.sbkm.payments.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
}
