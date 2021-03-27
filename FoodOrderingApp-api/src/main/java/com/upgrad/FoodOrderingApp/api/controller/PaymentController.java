package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    /** Implements the Get Payment Methods enpoint
     * This method exposes the endpoint /payment
     * @return Payment methods list
     */
    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> getAllPaymentMethods() {

        // First get List of payment methods from Payment table
        List<PaymentEntity> paymentEntityList = new ArrayList<>();

        // Add all the payment method records to the array list
        paymentEntityList.addAll(paymentService.getAllPaymentMethods());

        // Variable to store the payment response list
        PaymentListResponse paymentListResponse = new PaymentListResponse();

        // Loop through the paymentEntityList and add each record to Payment response
        for (PaymentEntity paymentEntity : paymentEntityList) {

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setId(UUID.fromString(paymentEntity.getUuid()));
            paymentResponse.setPaymentName(paymentEntity.getPaymentName());
            paymentListResponse.addPaymentMethodsItem(paymentResponse);
        }

        // Return the PaymentListResponse with OK https status
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);

    }

}
