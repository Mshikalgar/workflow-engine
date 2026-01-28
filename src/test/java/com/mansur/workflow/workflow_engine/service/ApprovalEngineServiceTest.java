package com.mansur.workflow.workflow_engine.service;

import com.mansur.workflow.workflow_engine.entity.*;
import com.mansur.workflow.workflow_engine.entity.enums.*;
import com.mansur.workflow.workflow_engine.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApprovalEngineServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ApprovalStepRepository stepRepository;

    @Mock
    private ApprovalHistoryRepository historyRepository;

    @InjectMocks
    private ApprovalEngineService approvalEngineService;

    // ✅ TEST 1: Happy path – first approver approves
    @Test
    void approve_shouldSucceed_forValidApprover() {

        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy("mansur");
        request.setType(RequestType.LEAVE);

        ApprovalStep step = new ApprovalStep();
        step.setRole(Role.APPROVER);
        step.setStepOrder(1);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(stepRepository.findByRequestTypeOrderByStepOrder(RequestType.LEAVE))
                .thenReturn(List.of(step));
        when(historyRepository.findByRequestIdOrderByActionAtAsc(1L))
                .thenReturn(List.of()); // no previous approvals

        approvalEngineService.approve(1L, "approverUser", Role.APPROVER);

        verify(historyRepository).save(any(ApprovalHistory.class));
        verify(requestRepository).save(request);
        assertEquals(RequestStatus.APPROVED, request.getStatus());
    }

    // ❌ TEST 2: Creator cannot approve own request
    @Test
    void approve_shouldFail_ifCreatorApproves() {

        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy("mansur");
        request.setType(RequestType.LEAVE);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalEngineService.approve(1L, "mansur", Role.APPROVER)
        );

        assertEquals("Requester cannot approve own request", ex.getMessage());
    }

    // ❌ TEST 3: Wrong role tries to approve
    @Test
    void approve_shouldFail_forWrongRole() {

        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedBy("mansur");
        request.setType(RequestType.LEAVE);

        ApprovalStep step = new ApprovalStep();
        step.setRole(Role.APPROVER);
        step.setStepOrder(1);

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        when(stepRepository.findByRequestTypeOrderByStepOrder(RequestType.LEAVE))
                .thenReturn(List.of(step));

        when(historyRepository.findByRequestIdOrderByActionAtAsc(1L))
                .thenReturn(List.of());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalEngineService.approve(
                	    1L,
                	    "financeUser",
                	    Role.REQUESTER   // not ADMIN, not APPROVER
                	)
        );

        System.out.println("ACTUAL MESSAGE = " + ex.getMessage());

    }



    // ❌ TEST 4: Request not in PENDING state
    @Test
    void approve_shouldFail_ifRequestNotPending() {

        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.APPROVED);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> approvalEngineService.approve(1L, "user", Role.APPROVER)
        );

        assertEquals("Request is not in PENDING state", ex.getMessage());
    }
    
   

}
