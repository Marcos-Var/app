package com.example.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.app.model.AuditLog;
import com.example.app.model.Product;
import com.example.app.repository.AuditLogRepository;
import com.example.app.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    // Listar todos los productos
    public List<Product> getAllProducts() {
        logAudit("READ", "Product", "ALL", "Listó todos los productos");
        return productRepository.findAll();
    }
    
    // Buscar producto por ID
    public Optional<Product> getProductById(Long id) {
        logAudit("READ", "Product", String.valueOf(id), "Buscó producto por ID");
        return productRepository.findById(id);
    }
    
    // Crear producto
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        logAudit("CREATE", "Product", String.valueOf(savedProduct.getId()), 
                "Creó producto: " + savedProduct.getName());
        return savedProduct;
    }
    
    // Actualizar producto
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        
        Product updatedProduct = productRepository.save(product);
        logAudit("UPDATE", "Product", String.valueOf(id), 
                "Actualizó producto: " + updatedProduct.getName());
        return updatedProduct;
    }
    
    // Eliminar producto
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        productRepository.deleteById(id);
        logAudit("DELETE", "Product", String.valueOf(id), 
                "Eliminó producto: " + product.getName());
    }
    
    // Método auxiliar para registrar auditoría
    private void logAudit(String action, String entity, String entityId, String details) {
        AuditLog log = new AuditLog(action, entity, entityId, details, "system");
        auditLogRepository.save(log);
    }
}
