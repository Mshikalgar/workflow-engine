package com.mansur.workflow.workflow_engine.config;

import com.mansur.workflow.workflow_engine.entity.ApprovalStep;
import com.mansur.workflow.workflow_engine.entity.enums.RequestType;
import com.mansur.workflow.workflow_engine.entity.enums.Role;
import com.mansur.workflow.workflow_engine.repository.ApprovalStepRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final ApprovalStepRepository approvalStepRepository;

    public DataInitializer(ApprovalStepRepository approvalStepRepository) {
        this.approvalStepRepository = approvalStepRepository;
    }

    @PostConstruct
    public void loadWorkflowConfig() {

        if (approvalStepRepository.count() > 0) {
            return; // already initialized
        }

        // LEAVE workflow
        approvalStepRepository.save(createStep(RequestType.LEAVE, 1, Role.APPROVER));
        approvalStepRepository.save(createStep(RequestType.LEAVE, 2, Role.ADMIN));

        // EXPENSE workflow
        approvalStepRepository.save(createStep(RequestType.EXPENSE, 1, Role.APPROVER));
        approvalStepRepository.save(createStep(RequestType.EXPENSE, 2, Role.APPROVER));
        approvalStepRepository.save(createStep(RequestType.EXPENSE, 3, Role.ADMIN));
    }

    private ApprovalStep createStep(RequestType type, int order, Role role) {
        ApprovalStep step = new ApprovalStep();
        step.setRequestType(type);
        step.setStepOrder(order);
        step.setRole(role);
        return step;
    }
}
