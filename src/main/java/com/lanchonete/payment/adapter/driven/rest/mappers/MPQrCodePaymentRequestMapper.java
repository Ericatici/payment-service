package com.lanchonete.payment.adapter.driven.rest.mappers;

import static com.lanchonete.payment.core.application.config.Constants.DYNAMIC;
import static com.lanchonete.payment.core.application.config.Constants.ORDER_DESCRIPTION;
import static com.lanchonete.payment.core.application.config.Constants.QR;

import java.math.BigDecimal;
import java.util.List;

import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest;
import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest.Config;
import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest.Transactions;
import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest.Config.Qr;
import com.lanchonete.payment.adapter.driven.rest.request.MPQrCodePaymentRequest.Transactions.Payments;

public class MPQrCodePaymentRequestMapper {

    public static MPQrCodePaymentRequest createMPQrCodePaymentRequest(final Long orderId, final BigDecimal totalPrice, final String externalPosId){
         
        return MPQrCodePaymentRequest.builder()
            .type(QR)
            .totalAmount(totalPrice.toString())
            .description(ORDER_DESCRIPTION + orderId)
            .externalReference(orderId.toString())
            .config(getQrConfig(externalPosId))
            .transactions(getQrTransactions(totalPrice))
            .build();
        
    }

    private static Config getQrConfig(final String externalPosId){
        final Qr qr = Qr.builder()
            .externalPosId(externalPosId)
            .mode(DYNAMIC)
            .build();

        return Config.builder().qr(qr).build();
    }

    private static Transactions getQrTransactions(final BigDecimal totalPrice){
        final Payments payments = Payments.builder()
            .amount(totalPrice.toString())
            .build();

        return Transactions.builder().payments(List.of(payments)).build();
    }
    
}

