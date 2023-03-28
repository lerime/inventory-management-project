package com.example.ims.repository;

import com.example.ims.model.Warehouse;
import org.springframework.data.repository.CrudRepository;

public interface WarehouseRepository extends CrudRepository<Warehouse, Long> {
    Warehouse findFirstByNameAndRegionAndCity(String name, String region, String city);
}
