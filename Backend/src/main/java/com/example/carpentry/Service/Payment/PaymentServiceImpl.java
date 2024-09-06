package com.example.carpentry.Service.Payment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.carpentry.Model.Order.ProjectsList;
import com.example.carpentry.Service.Order.OrderServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Builder;

@Service
public class PaymentServiceImpl implements PaymentService {

        @Autowired
        OrderServiceImpl orderService;

        @Value("${stripe.api.key}")
        private String stripeSecretKey;

        @Override
        public String createPaymentLink(List<ProjectsList> items) throws StripeException {
                Stripe.apiKey = stripeSecretKey;

                Builder params = SessionCreateParams.builder()
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.P24)
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("https://carpentry-shop-client.vercel.app/Success")
                                .setCancelUrl("https://carpentry-shop-client.vercel.app/Fail");

                ResponseEntity<?> checkData = orderService.checkData(items);
                if (checkData.getStatusCode().is2xxSuccessful()) {
                        for (ProjectsList i : items) {
                                params.addLineItem(SessionCreateParams.LineItem.builder()
                                                .setQuantity((long) i.getQuantity())
                                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                                .setCurrency("pln")
                                                                .setUnitAmount((long) i.getProject().getPrice() * 100)
                                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                                                .builder()
                                                                                .setName(i.getProject().getName())
                                                                                .build())
                                                                .build())
                                                .build());
                        }
                        SessionCreateParams readyParams = params.build();

                        Session session = Session.create(readyParams);

                        return session.getUrl();
                } else {
                        return "Error";
                }

        }

}
