package br.ufpb.dcx.flow.dev.s.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.resources.payment.Payment;

import java.math.BigDecimal;

public class MercadoPagoService {

    public static void configure(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public static Payment generatePixPayment(BigDecimal amount) throws Exception {
        PaymentClient client = new PaymentClient();

        PaymentCreateRequest request = PaymentCreateRequest.builder()
                .transactionAmount(amount)
                .description("FlowPDV Purchase")
                .paymentMethodId("pix")
                .installments(1)
                .payer(PaymentPayerRequest.builder()
                        .email("albieredelima@gmail.com")
                        .build())
                .build();

        return client.create(request);
    }

    public static Payment checkPaymentStatus(Long paymentId) throws Exception {
        PaymentClient client = new PaymentClient();
        return client.get(paymentId);
    }
}