package com.example.app.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.AuditLog;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByEntity(String entity);
    List<AuditLog> findByUsername(String username); //consultas
    List<AuditLog> findByAction(String action);
}
