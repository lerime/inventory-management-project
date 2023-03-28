package com.example.ims.controller;

import com.example.ims.enums.TransactionType;
import com.example.ims.model.Category;
import com.example.ims.model.History;
import com.example.ims.model.Product;
import com.example.ims.model.Warehouse;
import com.example.ims.repository.CategoryRepository;
import com.example.ims.repository.HistoryRepository;
import com.example.ims.repository.ProductRepository;
import com.example.ims.repository.WarehouseRepository;
import com.example.ims.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;


@Controller
@RequestMapping(path = "/product")
public class ProductController {
    final int CRITICAL_QUANTITY_THRESHOLD = 2;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @PostMapping(path = "/")
    public @ResponseBody String addNewProduct(
            @RequestParam String name,
            @RequestParam Integer quantity,
            @RequestParam String categoryName) {
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            category = new Category();
            category.setName(categoryName);
            categoryRepository.save(category);
        }
        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setCategory(category);
        productRepository.save(product);
        HistoryService.createHistoryRecord(TransactionType.PRODUCT_ADD, product.getId());
        return "success";
    }

    @PatchMapping("/update/{id}")
    public @ResponseBody String updateProduct(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Long categoryId
    ) {
//        if (name == null && quantity == null && categoryName == null) {
//            return "no action";
//        }
        Optional<Product> rs = productRepository.findById(id);
        if (rs.isPresent()) {
            Product product = rs.get();
            if (quantity != null && quantity == CRITICAL_QUANTITY_THRESHOLD) {
                // todo mail at
            }
            product.setQuantity(quantity);
            product.setName(name);
            Category category = categoryRepository.findById(categoryId).get();
            product.setCategory(category);
            productRepository.save(product);
            HistoryService.createHistoryRecord(TransactionType.PRODUCT_UPDATE, product.getId());
        }
        return "success";
    }

    @PutMapping("/remove-stock/{id}/")
    public @ResponseBody String updateInventoryQuantity(
            @PathVariable Long id
    ) {
        Optional<Product> rs = productRepository.findById(id);
        if (rs.isPresent()) {
            Product product = rs.get();

            int quantity = product.getQuantity();
            if (--quantity == CRITICAL_QUANTITY_THRESHOLD) {
                System.out.println("Quantity Warning!");
            }
            product.setQuantity(quantity);
            productRepository.save(product);
            HistoryService.createHistoryRecord(TransactionType.PRODUCT_TAKE_OUT_OF_INVENTORY, product.getId());

        }
        return "success";
    }

    @PutMapping("/add-stock/{id}/")
    public @ResponseBody String addInventoryQuantity(@PathVariable Long id) {
        Optional<Product> rs = productRepository.findById(id);
        if (!rs.isPresent()) {
            return "fail";
        }
        Product product = rs.get();
        int quantity = product.getQuantity();
        product.setQuantity(++quantity);
        productRepository.save(product);
        HistoryService.createHistoryRecord(TransactionType.PRODUCT_ADD_STOCK, product.getId());
        return "success";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Product> getAllProducts(
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String warehouseCity,
            @RequestParam(required = false) String warehouseRegion
    ) {
        Warehouse warehouse = warehouseRepository.findFirstByNameAndRegionAndCity(warehouseName, warehouseRegion, warehouseCity);
        if (warehouse != null) {
            return productRepository.findAll();
        }
        System.out.println("all");
        return productRepository.findAll();
    }

    @GetMapping(path = "/{id}/warehouse")
    public @ResponseBody Warehouse getWarehouseInfo(@PathVariable Long id) {
        Optional<Product> rs = productRepository.findById(id);
        if (!rs.isPresent()) {
            return null;
        }
        Product product = rs.get();
        return product.getWarehouse();
    }

    @DeleteMapping("/{id}")
    public @ResponseBody String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        HistoryService.createHistoryRecord(TransactionType.PRODUCT_DELETE, id);
        return "success";
    }

}
