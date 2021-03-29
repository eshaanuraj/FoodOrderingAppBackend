package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;

@Service
public class AddressService {
 
	@Autowired
	private AddressDao addressDao;
	
	@Autowired
	private StateDao stateDao;
	
	
	public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity) 
			throws SaveAddressException, AddressNotFoundException {

		if(addressEntity.getCity() == null || addressEntity.getFlatBuilNo() == null 
				|| addressEntity.getLocality() == null || addressEntity.getPincode() == null) {
			throw new SaveAddressException("SAR-001", "No field can be empty");
		}
		
		String pincode = addressEntity.getPincode(); 
		if(!isDigitsOnly(pincode)) {
			throw new SaveAddressException("SAR-002", "Invalid pincode");
		}
		
		if(addressEntity.getState() == null) {
			 throw new AddressNotFoundException("ANF-002", "No state by this id"); 
		}
		
		UUID uuidObj = UUID.randomUUID();
		addressEntity.setUuid(uuidObj.toString()); 
		
		AddressEntity savedAddressEntity = addressDao.saveAddreeEntity(addressEntity); 
		
		CustomerAddressEntity custAddrEntity = new CustomerAddressEntity();
		custAddrEntity.setAddress(savedAddressEntity); 
		custAddrEntity.setCustomer(customerEntity); 
		
		addressDao.saveCustomerAddressEntity(custAddrEntity);
		return addressEntity;  
		
	}
	
	
	private boolean isDigitsOnly(String pincode) {
	     for(int i=0;i<pincode.length();i++) {
	    	 if(pincode.charAt(i) < '0' || pincode.charAt(i) > '9' ) {
	    		 return false;
	    	 }
	     }
	     return true;
	}


	public List<StateEntity> getAllStates() { 
		List<StateEntity> allStates = addressDao.getAllStates();
		return allStates;
	}


	public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) { 
		List<AddressEntity> addressList = addressDao.getAllSavedAddresses(customerEntity);
		return addressList;
	}


	public String deleteSavedAddress(CustomerEntity customerEntity , Integer addressId) throws AddressNotFoundException { 
		
		if (addressId == null) {
			throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
		}

		// TODO Auto-generated method stub
		Integer customerId = customerEntity.getId(); 
		
		AddressEntity addressEntity = addressDao.getAddressById(addressId.longValue()); 
		if(addressEntity == null) {
			throw new AddressNotFoundException("ANF-003","No address by this id");
		} 
		CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddrByAddrId(addressId, customerId);
		addressDao.deleteSavedAddress(customerAddressEntity);   
		return addressEntity.getUuid(); 
	}
	
	
	public AddressEntity getAddressByUUID(String uuid) throws AddressNotFoundException {
		AddressEntity addressByUuid = addressDao.getAddressByUuid(uuid);  
		if (addressByUuid == null) {
			throw new AddressNotFoundException("ANF-003", "No address by this id");
		}
        return addressByUuid;
	}

	public AddressEntity getAddressByUUID(String addressUUID,CustomerEntity customerEntity) throws AddressNotFoundException {
		if (StringUtils.isBlank(addressUUID)) {
			throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
		}

		// TODO Auto-generated method stub
		Integer customerId = customerEntity.getId(); 
		
		AddressEntity addressEntity = addressDao.getAddressByUuid(addressUUID);  
		if(addressEntity == null) {
			throw new AddressNotFoundException("ANF-003","No address by this id");
		} 
        
		return addressEntity;
	}


    @Transactional
    public AddressEntity getAddressById( Long addressId) {
        return addressDao.getAddressById(addressId);
    }


	public StateEntity getStateByUUID(String uuid) throws SaveAddressException {  
		if(StringUtils.isBlank(uuid)) {
			throw new SaveAddressException("SAR-001", "No field can be empty");
		}
		return stateDao.getStateEntityByUuid(uuid);
	}


	public AddressEntity deleteAddress(AddressEntity addressEntity) { 
		// TODO Auto-generated method stub
		AddressEntity deletedAddress = addressDao.deleteAddress(addressEntity); 
		return deletedAddress;
	}



	
}
