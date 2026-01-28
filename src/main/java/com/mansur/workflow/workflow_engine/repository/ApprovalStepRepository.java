package com.mansur.workflow.workflow_engine.repository;

import com.mansur.workflow.workflow_engine.entity.ApprovalStep;
import com.mansur.workflow.workflow_engine.entity.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {

    List<ApprovalStep> findByRequestTypeOrderByStepOrder(RequestType requestType);
}
