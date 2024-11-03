//package com.mhkcode.institution.listener;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mhkcode.institution.model.Audit;
//import com.mhkcode.institution.model.User;
//import com.mhkcode.institution.service.AuditService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import jakarta.persistence.PreRemove;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class AuditListener {
//
//    @Autowired
//    private AuditService auditService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @PrePersist
//    public void prePersist(Object entity) {
//        logChange(entity, "INSERT");
//    }
//
//    @PreUpdate
//    public void preUpdate(Object entity) {
//        logChange(entity, "UPDATE");
//    }
//
//    @PreRemove
//    public void preRemove(Object entity) {
//        logChange(entity, "DELETE");
//    }
//
//    private void logChange(Object entity, String action) {
//        if (entity instanceof User) {
//            User user = (User) entity;
//            Audit audit = new Audit();
//            audit.setEntityName(entity.getClass().getSimpleName());
//            audit.setEntityId(user.getUserId());
//            audit.setAction(action);
//            audit.setTimestamp(LocalDateTime.now());
//
//            // Convert changed fields to JSON
//            Map<String, Object> changedFields = new HashMap<>();
//            changedFields.put("email", user.getEmail());
//            changedFields.put("firstname", user.getFirstname());
//            // Add other fields as needed
//
//            try {
//                audit.setChangedFields(objectMapper.writeValueAsString(changedFields));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            auditService.saveAudit(audit);
//        }
//    }
//}
