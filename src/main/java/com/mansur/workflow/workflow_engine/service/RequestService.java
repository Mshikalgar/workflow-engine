package com.mansur.workflow.workflow_engine.service;

import com.mansur.workflow.workflow_engine.entity.Request;
import com.mansur.workflow.workflow_engine.entity.ApprovalHistory;
import com.mansur.workflow.workflow_engine.entity.enums.ActionType;
import com.mansur.workflow.workflow_engine.entity.enums.RequestStatus;
import com.mansur.workflow.workflow_engine.entity.enums.RequestType;
import com.mansur.workflow.workflow_engine.repository.ApprovalHistoryRepository;
import com.mansur.workflow.workflow_engine.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final ApprovalHistoryRepository historyRepository;

    public RequestService(RequestRepository requestRepository,
                          ApprovalHistoryRepository historyRepository) {
        this.requestRepository = requestRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public Request createRequest(RequestType type, String createdBy) {

        Request request = new Request();
        request.setType(type);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy(createdBy);
        request.setCreatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);

        ApprovalHistory history = new ApprovalHistory();
        history.setRequestId(savedRequest.getId());
        history.setAction(ActionType.CREATED);
        history.setActionBy(createdBy);
        history.setActionAt(LocalDateTime.now());

        historyRepository.save(history);

        return savedRequest;
    }
 
    public java.util.Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

}
