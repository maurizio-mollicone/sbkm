package it.ifin.demo.sbkm.stock.repository;

import org.springframework.data.repository.CrudRepository;

import it.ifin.demo.sbkm.stock.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
    
}
