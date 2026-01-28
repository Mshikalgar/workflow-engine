package com.mansur.workflow.workflow_engine.service;

import com.mansur.workflow.workflow_engine.entity.ApprovalHistory;
import com.mansur.workflow.workflow_engine.entity.ApprovalStep;
import com.mansur.workflow.workflow_engine.entity.Request;
import com.mansur.workflow.workflow_engine.entity.enums.ActionType;
import com.mansur.workflow.workflow_engine.entity.enums.RequestStatus;
import com.mansur.workflow.workflow_engine.entity.enums.Role;
import com.mansur.workflow.workflow_engine.repository.ApprovalHistoryRepository;
import com.mansur.workflow.workflow_engine.repository.ApprovalStepRepository;
import com.mansur.workflow.workflow_engine.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalEngineService {

    private final RequestRepository requestRepository;
    private final ApprovalStepRepository stepRepository;
    private final ApprovalHistoryRepository historyRepository;

    public ApprovalEngineService(RequestRepository requestRepository,
                                 ApprovalStepRepository stepRepository,
                                 ApprovalHistoryRepository historyRepository) {
        this.requestRepository = requestRepository;
        this.stepRepository = stepRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void approve(Long requestId, String actor, Role actorRole) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not in PENDING state");
        }

        if (request.getCreatedBy().equals(actor)) {
            throw new IllegalStateException("Requester cannot approve own request");
        }

        // 🚫 Admin must NOT use normal approve flow
        if (actorRole == Role.ADMIN) {
            throw new IllegalStateException(
                "Admin cannot approve using approve endpoint. Use override."
            );
        }

        List<ApprovalStep> steps =
                stepRepository.findByRequestTypeOrderByStepOrder(request.getType());

        int currentStep = getApprovedCount(requestId);

        ApprovalStep step = steps.get(currentStep);
        
        System.out.println("STATUS = " + request.getStatus());
        System.out.println("CREATED BY = " + request.getCreatedBy());
        System.out.println("ACTOR = " + actor);
        System.out.println("STEP ROLE = " + step.getRole());
        System.out.println("ACTOR ROLE = " + actorRole);

        if (step.getRole() != actorRole) {
            throw new IllegalStateException("User not authorized for this step");
        }

        saveHistory(requestId, ActionType.APPROVED, actor);

        if (currentStep + 1 == steps.size()) {
            request.setStatus(RequestStatus.APPROVED);
            requestRepository.save(request);
        }
    }

    @Transactional
    public void reject(Long requestId, String actor) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);

        saveHistory(requestId, ActionType.REJECTED, actor);
    }

    private int getApprovedCount(Long requestId) {
        return historyRepository
                .findByRequestIdOrderByActionAtAsc(requestId)
                .stream()
                .filter(h -> h.getAction() == ActionType.APPROVED)
                .toList()
                .size();
    }

    private void saveHistory(Long requestId, ActionType action, String actor) {
        ApprovalHistory history = new ApprovalHistory();
        history.setRequestId(requestId);
        history.setAction(action);
        history.setActionBy(actor);
        history.setActionAt(LocalDateTime.now());
        historyRepository.save(history);
    }
    
    @Transactional
    public void adminOverride(Long requestId, String adminUser) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Request not found"));

        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        saveHistory(requestId, ActionType.OVERRIDDEN, adminUser);
    }
    
    public java.util.List<ApprovalHistory> getHistory(Long requestId) {
        return historyRepository.findByRequestIdOrderByActionAtAsc(requestId);
    }


}
