package com.mhkcode.institution.repository;

import com.mhkcode.institution.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit,Long> {
}
