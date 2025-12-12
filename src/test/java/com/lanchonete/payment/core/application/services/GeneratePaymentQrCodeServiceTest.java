package com.lanchonete.payment.core.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lanchonete.payment.core.application.dto.PaymentDataDTO;
import com.lanchonete.payment.core.domain.model.PaymentData;
import com.lanchonete.payment.core.domain.repositories.PaymentRepository;
import com.lanchonete.payment.mocks.PaymentDataDTOMock;
import com.lanchonete.payment.mocks.PaymentDataMock;

@ExtendWith(MockitoExtension.class)
class GeneratePaymentQrCodeServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private GeneratePaymentQrCodeService generatePaymentQrCodeService;

    private PaymentDataDTO paymentDataDTO;
    private PaymentData paymentData;

    @BeforeEach
    void setUp() {
        paymentDataDTO = PaymentDataDTOMock.createPaymentDataMock();
        paymentData = PaymentDataMock.createPaymentDataMock();
    }

    @Test
    void shouldGeneratePaymentQrCodeSuccessfully() {
        when(paymentRepository.getPaymentData(anyLong(), any(BigDecimal.class))).thenReturn(paymentData);

        PaymentData result = generatePaymentQrCodeService.generatePaymentQrCode(paymentDataDTO);

        assertNotNull(result);
        assertEquals(paymentData.getPaymentId(), result.getPaymentId());
        assertEquals(paymentData.getQrCode(), result.getQrCode());
          
        verify(paymentRepository, times(1)).getPaymentData(paymentDataDTO.getOrderId(), paymentDataDTO.getTotalPrice());
    }

    @Test
    void shouldReturnPaymentDataWhenValid() {
        PaymentData validPaymentData = PaymentDataMock.createPaymentDataMock();
        when(paymentRepository.getPaymentData(anyLong(), any(BigDecimal.class))).thenReturn(validPaymentData);

        PaymentData result = generatePaymentQrCodeService.generatePaymentQrCode(paymentDataDTO);

        assertNotNull(result);
        assertEquals(validPaymentData.getPaymentId(), result.getPaymentId());
        assertEquals(validPaymentData.getQrCode(), result.getQrCode());
        
        verify(paymentRepository, times(1)).getPaymentData(paymentDataDTO.getOrderId(), paymentDataDTO.getTotalPrice());
    }
}

