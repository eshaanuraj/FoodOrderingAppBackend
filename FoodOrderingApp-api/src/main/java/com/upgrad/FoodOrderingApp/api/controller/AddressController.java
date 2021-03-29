package com.upgrad.FoodOrderingApp.api.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.DeleteAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

@RestController
public class AddressController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomerService customerService;
	
	

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
			@RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
			throws SignUpRestrictedException, AuthenticationFailedException, AuthorizationFailedException,
			SaveAddressException, AddressNotFoundException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		CustomerEntity customerEntity = customerService.getCustomer(accessToken);

		AddressEntity addressEntity = new AddressEntity();

		String pincode = saveAddressRequest.getPincode();

		addressEntity.setPincode(pincode);
		String city = saveAddressRequest.getCity();
		addressEntity.setCity(city);
		String flatBuildingName = saveAddressRequest.getFlatBuildingName();
		addressEntity.setFlatBuilNo(flatBuildingName);
		String locality = saveAddressRequest.getLocality();
		addressEntity.setLocality(locality);
		String stateUuid = saveAddressRequest.getStateUuid();
		addressEntity.setActive(1);
		
		StateEntity stateByUUID = addressService.getStateByUUID(stateUuid); 
		addressEntity.setState(stateByUUID);
 
		AddressEntity savedAddress = addressService.saveAddress(customerEntity, addressEntity); 
		SaveAddressResponse saveAddressResponse = new SaveAddressResponse();
		saveAddressResponse.setId(savedAddress.getUuid()); 
		saveAddressResponse.setStatus("ADDRESS SUCCESSFULLY REGISTERED");

		return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<AddressListResponse> getAllSavedAddresses(
			@RequestHeader("authorization") final String authorization)
			throws AuthenticationFailedException, AuthorizationFailedException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		CustomerEntity customerEntity = customerService.getCustomer(accessToken);
		List<AddressEntity> addressEntityList = addressService.getAllAddress(customerEntity);

		List<AddressList> listOfAddressList = new ArrayList<AddressList>();

		for (AddressEntity addressEntity : addressEntityList) {
			AddressList addressList = new AddressList();
			addressList.setCity(addressEntity.getCity());
			addressList.setFlatBuildingName(addressEntity.getFlatBuilNo());
			addressList.setId(UUID.fromString(addressEntity.getUuid()));
			addressList.setLocality(addressEntity.getLocality());
			addressList.setPincode(addressEntity.getPincode());

			AddressListState addressListState = new AddressListState();
			addressListState.setId(UUID.fromString(addressEntity.getState().getUuid()));
			addressListState.setStateName(addressEntity.getState().getStateName());

			addressList.setState(addressListState);

			listOfAddressList.add(addressList);
		}

		AddressListResponse addressListResponse = new AddressListResponse();
		addressListResponse.setAddresses(listOfAddressList);
		return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<DeleteAddressResponse> deleteSavedAddresses(
			@RequestHeader("authorization") final String authorization, @PathVariable("address_id") String addressUUId)
			throws AuthenticationFailedException, AuthorizationFailedException, AddressNotFoundException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		CustomerEntity customerEntity = customerService.getCustomer(accessToken);

		AddressEntity addressByUUID = addressService.getAddressByUUID(addressUUId, customerEntity);  
		addressService.deleteAddress(addressByUUID);
		DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse();
		deleteAddressResponse.setId(UUID.fromString(addressUUId));
		deleteAddressResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");

		return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, path = "/states",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, 
	               produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<List<StateEntity>> getAllStates() {

		List<StateEntity> allStatesList = addressService.getAllStates();
		return new ResponseEntity<List<StateEntity>>(allStatesList, HttpStatus.OK);

	}

}
