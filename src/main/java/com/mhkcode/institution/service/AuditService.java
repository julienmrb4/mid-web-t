package com.mhkcode.institution.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mhkcode.institution.model.Audit;
import com.mhkcode.institution.repository.AuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Service
public class AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;

        // Configure ObjectMapper for proper serialization
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void logAudit(Object entity, String action, String username) {
        try {
            Audit audit = new Audit();
            audit.setEntityName(entity.getClass().getSimpleName());
            audit.setEntityId(getEntityId(entity));
            audit.setAction(action);
            audit.setUsername(username);
            audit.setTimestamp(LocalDateTime.now());

            // Convert entity to JSON string with proper handling of dates and formatting
            String newValueJson = objectMapper.writeValueAsString(entity);
            audit.setNewValue(newValueJson);

            auditRepository.save(audit);

            logger.debug("Audit log created for {} action on entity {} with ID {}",
                    action, audit.getEntityName(), audit.getEntityId());
        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
            // Don't throw the exception to prevent disrupting the main operation
        }
    }

    public void logAuditWithOldValue(Object oldEntity, Object newEntity, String action, String username) {
        try {
            Audit audit = new Audit();
            audit.setEntityName(newEntity.getClass().getSimpleName());
            audit.setEntityId(getEntityId(newEntity));
            audit.setAction(action);
            audit.setUsername(username);
            audit.setTimestamp(LocalDateTime.now());

            // Convert both old and new entities to JSON
            if (oldEntity != null) {
                audit.setOldValue(objectMapper.writeValueAsString(oldEntity));
            }
            audit.setNewValue(objectMapper.writeValueAsString(newEntity));

            auditRepository.save(audit);

            logger.debug("Audit log created for {} action on entity {} with ID {}",
                    action, audit.getEntityName(), audit.getEntityId());
        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
        }
    }

    private Long getEntityId(Object entity) {
        try {
            if (entity == null) return null;
            return (Long) entity.getClass().getMethod("getUserId").invoke(entity);
        } catch (Exception e) {
            logger.warn("Could not get entity ID", e);
            return null;
        }
    }
}