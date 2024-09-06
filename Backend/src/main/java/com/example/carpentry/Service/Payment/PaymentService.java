package com.example.carpentry.Service.Payment;

import java.util.List;

import com.example.carpentry.Model.Order.ProjectsList;
import com.stripe.exception.StripeException;

public interface PaymentService {

    public String createPaymentLink(List<ProjectsList> items) throws StripeException;
}
