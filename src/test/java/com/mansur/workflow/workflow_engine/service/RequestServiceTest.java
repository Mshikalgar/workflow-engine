package com.mansur.workflow.workflow_engine.service;

import com.mansur.workflow.workflow_engine.entity.Request;
import com.mansur.workflow.workflow_engine.entity.enums.RequestStatus;
import com.mansur.workflow.workflow_engine.entity.enums.RequestType;
import com.mansur.workflow.workflow_engine.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @Test
    void createRequest_shouldCreatePendingRequest() {

        Request saved = new Request();
        saved.setId(1L);
        saved.setStatus(RequestStatus.PENDING);

        when(requestRepository.save(any(Request.class)))
                .thenReturn(saved);

        Request result =
                requestService.createRequest(RequestType.LEAVE, "mansur");

        assertThat(result.getStatus()).isEqualTo(RequestStatus.PENDING);
        verify(requestRepository).save(any(Request.class));
    }
}
