package com.mansur.workflow.workflow_engine.repository;
import com.mansur.workflow.workflow_engine.entity.enums.ActionType;
import com.mansur.workflow.workflow_engine.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByRequestIdOrderByActionAtAsc(Long requestId);
    
    long countByRequestIdAndAction(Long requestId, ActionType action);
}
