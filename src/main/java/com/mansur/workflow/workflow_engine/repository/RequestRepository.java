package com.mansur.workflow.workflow_engine.repository;

import com.mansur.workflow.workflow_engine.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
