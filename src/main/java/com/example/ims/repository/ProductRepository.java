package com.example.ims.repository;

import com.example.ims.model.Category;
import com.example.ims.model.Product;
import com.example.ims.model.Warehouse;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAllByWarehouse(Warehouse w);
}
