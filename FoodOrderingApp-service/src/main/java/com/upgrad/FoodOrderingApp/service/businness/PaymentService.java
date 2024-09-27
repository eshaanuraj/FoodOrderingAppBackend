package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    // Method to get all Payment Methods from the Payment Entity
    @Transactional
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }
    
    
    public PaymentEntity getPaymentByUuid(UUID uuid) throws PaymentMethodNotFoundException {
    	PaymentEntity paymentByUUID = paymentDao.getPaymentByUuid(uuid); 
		// Set Payment Object in OrderEntity as well
		if (paymentByUUID == null) {
			throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
		}
       return paymentByUUID; 
    }


	public PaymentEntity getPaymentByUUID(String uuid) throws PaymentMethodNotFoundException { 
		// TODO Auto-generated method stub
    	PaymentEntity paymentByUUID = paymentDao.getPaymentByUuid(UUID.fromString(uuid)); 
		// Set Payment Object in OrderEntity as well
		if (paymentByUUID == null) {
			throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
		}
       return paymentByUUID; 

	}
    
}
