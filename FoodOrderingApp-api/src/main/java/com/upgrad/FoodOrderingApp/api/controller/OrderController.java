package com.upgrad.FoodOrderingApp.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantity;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem.TypeEnum;
import com.upgrad.FoodOrderingApp.api.model.OrderList;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddress;
import com.upgrad.FoodOrderingApp.api.model.OrderListAddressState;
import com.upgrad.FoodOrderingApp.api.model.OrderListCoupon;
import com.upgrad.FoodOrderingApp.api.model.OrderListCustomer;
import com.upgrad.FoodOrderingApp.api.model.OrderListPayment;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;

@RestController
public class OrderController {

	@Autowired
	private AuthenticationService authenticationService;
	    

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private AddressService addressService;
	
	
	@Autowired
	private RestaurantService restaurantService;
	
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
    		@PathVariable("coupon_name") String couponName) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException, CouponNotFoundException {


        String[] splitText = authorization.split("Basic "); 
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        
        if (decodedTextArray.length == 2) {
            CustomerAuthTokenEntity customerAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);
            
            if(customerAuthToken == null) {
            	throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in"); 
            }
            if(customerAuthToken.getLogoutAt() != null) {
            	throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
            } 
            if(customerAuthToken.getExpiresAt() != null) {
            	throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint."); 
            }
            
            if(StringUtils.isEmpty(couponName)){
            	throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
            }
            
            CouponEntity couponByName = orderService.getCouponByName(couponName); 
            if(couponByName == null) {
            	throw new CouponNotFoundException("CPF-001","No coupon by this name");
            }
            
            CouponDetailsResponse response = new CouponDetailsResponse();
            response.setId(couponByName.getUuid());  
            response.setCouponName(couponByName.getCouponName()); 
            response.setPercent(couponByName.getPercent()); 
            return new ResponseEntity<CouponDetailsResponse>(response,HttpStatus.OK);
        }
        
    	return null;
    	
    }
    
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OrderList>> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException {

        String[] splitText = authorization.split("Basic "); 
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        
        if (decodedTextArray.length == 2) {
            CustomerAuthTokenEntity customerAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);
            
            if(customerAuthToken == null) {
            	throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in"); 
            }
            if(customerAuthToken.getLogoutAt() != null) {
            	throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
            } 
            if(customerAuthToken.getExpiresAt() != null) {
            	throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint."); 
            }
            
            CustomerEntity customer = customerAuthToken.getCustomer(); 
            
            List<OrderEntity> allOrdersForCustomer = orderService.getAllOrdersForCustomer(customer); 
            
            List<OrderList> completeOrderList = new ArrayList<OrderList>();
            
            for(OrderEntity orderEntity : allOrdersForCustomer) {
            	OrderList orderList = new OrderList();
            	
            	orderList.setAddress(getOrderListAddress(orderEntity));
            	orderList.setBill(BigDecimal.valueOf(orderEntity.getBill())); 
            	orderList.setCoupon(getOrderListCoupon(orderEntity)); 
            	orderList.setCustomer(getOrderListCustomer(orderEntity)); 
            	orderList.setDate(orderEntity.getOrderedDate().toString()); 
            	orderList.setDiscount(BigDecimal.valueOf(orderEntity.getDiscount())); 
            	orderList.setId(UUID.fromString(orderEntity.getUuid()));
            	orderList.setItemQuantities(getOrderListItemQuantities(orderEntity)); 
            	orderList.setPayment(getOrderListPayment(orderEntity));  
            	
            	completeOrderList.add(orderList);
            	
            }
            
            return  new ResponseEntity<List<OrderList>>(completeOrderList,HttpStatus.OK); 
            
        }
        
    	return null;
    	
    }
    
    
    private OrderListAddress getOrderListAddress(OrderEntity orderEntity) {
    	AddressEntity address = orderEntity.getAddress(); 
    	OrderListAddress orderListAddr = new OrderListAddress();
    	orderListAddr.setCity(address.getCity()); 
    	orderListAddr.setFlatBuildingName(address.getFlatBuilNo()); 
    	orderListAddr.setId(UUID.fromString(address.getUuid())); 
    	orderListAddr.setLocality(address.getLocality()); 
    	orderListAddr.setPincode(address.getPincode()); 
    	
    	OrderListAddressState addrState = new OrderListAddressState();
    	addrState.setId(UUID.fromString(address.getState().getStateUuid())); 
    	addrState.setStateName(address.getState().getStateName()); 
    	
    	orderListAddr.setState(addrState); 
    	return orderListAddr; 
    }
    
    private OrderListCoupon getOrderListCoupon(OrderEntity orderEntity) {
    	
    	CouponEntity coupon = orderEntity.getCoupon(); 
    	
    	OrderListCoupon orderListCoupon = new OrderListCoupon();
    	orderListCoupon.setCouponName(coupon.getCouponName()); 
    	orderListCoupon.setId(coupon.getUuid()); 
    	orderListCoupon.setPercent(coupon.getPercent()); 
    	
    	return orderListCoupon;
    	
    }
    
    private OrderListCustomer getOrderListCustomer(OrderEntity orderEntity) {
    	
    	CustomerEntity customerEntity = orderEntity.getCustomer(); 
    	
    	OrderListCustomer orderListCustomer = new OrderListCustomer();
    	orderListCustomer.setContactNumber(customerEntity.getContactNumber()); 
    	orderListCustomer.setEmailAddress(customerEntity.getEmail()); 
    	orderListCustomer.setFirstName(customerEntity.getFirstName()); 
    	orderListCustomer.setId(UUID.fromString(customerEntity.getUuid()));
    	orderListCustomer.setLastName(customerEntity.getLastName()); 
    	
    	return orderListCustomer; 
    	
    }
    
    private List<ItemQuantityResponse> getOrderListItemQuantities(OrderEntity orderEntity) {
    	List<ItemQuantityResponse> itemQuantityList = new ArrayList<ItemQuantityResponse>();
    	
    	List<OrderItemEntity> orderItems = orderEntity.getOrderItems(); 
    	
    	for(OrderItemEntity orderItemEntity : orderItems) {
    		ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
    		itemQuantityResponse.setPrice(orderItemEntity.getPrice()); 
    		itemQuantityResponse.setQuantity(orderItemEntity.getQuantity()); 
    		
    		ItemQuantityResponseItem  itemQuantityResponseItem = new ItemQuantityResponseItem();
    		itemQuantityResponseItem.setId(orderItemEntity.getItem().getUuid()); 
    		itemQuantityResponseItem.setItemName(orderItemEntity.getItem().getItemName()); 
    		itemQuantityResponseItem.setItemPrice(orderItemEntity.getItem().getPrice()); 
    		itemQuantityResponseItem.setType(TypeEnum.valueOf(orderItemEntity.getItem().getType())); 
    		
    		itemQuantityResponse.setItem(itemQuantityResponseItem); 
    		
    		itemQuantityList.add(itemQuantityResponse);
    	}
    	
    	return itemQuantityList;
    }
    
    
    //Need to complete this
    private OrderListPayment getOrderListPayment(OrderEntity orderEntity) {
    	
    	OrderListPayment orderListPayment = new OrderListPayment();
    	//orderListPayment.setId(orderEntity.get);
    	
    	return orderListPayment;
    }
    
    
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
    		SaveOrderRequest saveOrderRequest) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException, RestaurantNotFoundException, CouponNotFoundException, ItemNotFoundException {  
 
        String[] splitText = authorization.split("Basic "); 
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        
        if (decodedTextArray.length == 2) {
            CustomerAuthTokenEntity customerAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);
            
            if(customerAuthToken == null) {
            	throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in"); 
            }
            if(customerAuthToken.getLogoutAt() != null) {
            	throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
            } 
            if(customerAuthToken.getExpiresAt() != null) {
            	throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint."); 
            }
            
			String addressId = saveOrderRequest.getAddressId(); 
			BigDecimal bill = saveOrderRequest.getBill(); 
			UUID couponId = saveOrderRequest.getCouponId(); 
			BigDecimal discount = saveOrderRequest.getDiscount(); 
			List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities(); 
			UUID paymentId = saveOrderRequest.getPaymentId(); 
			UUID restaurantId = saveOrderRequest.getRestaurantId();
			
			//Building OrderEntity Object
			OrderEntity orderEntity = new OrderEntity();
			
			AddressEntity addressByUuid = addressService.getAddressByUuid(addressId); 
			if(addressByUuid == null) {
				throw new AddressNotFoundException("ANF-003","No address by this id");
			}
			
			orderEntity.setAddress(addressByUuid); 
			
			
			orderEntity.setBill(bill.intValue()); 
			
			CouponEntity couponByUuid = orderService.getCouponByUuid(couponId.toString()); 
			if(couponByUuid == null) {
				throw new CouponNotFoundException("CPF-002","No coupon by this id");
			}
			orderEntity.setCoupon(couponByUuid); 
			orderEntity.setCustomer(customerAuthToken.getCustomer()); 
			orderEntity.setDiscount(discount.intValue()); 
			orderEntity.setOrderedDate(new Date());
			
			RestaurantEntity restaurantByUUID = restaurantService.restaurantByUUID(restaurantId.toString()); 
			if(restaurantByUUID == null) {
				throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
			}
			orderEntity.setRestaurant(restaurantByUUID); 
            
			//Set Payment Object in OrderEntity as well 
//			if(paymentByUUID == null) {
//				throw new PaymentMethodNotFoundException("PNF-002","No payment method found by this id");
//			}
			
			orderEntity.setUuid(UUID.randomUUID().toString());  
			
			List<OrderItemEntity> orderItemEntityList = new ArrayList<OrderItemEntity>();
		    
			for(ItemQuantity itq : itemQuantities) {
		    
		    	OrderItemEntity ordItemEntity = new OrderItemEntity();
		    	ItemEntity itemByUuid = orderService.getItemByUuid(itq.getItemId()); 
		    	if(itemByUuid == null) {
		    		throw new ItemNotFoundException("INF-003","No item by this id exist");
		    	}
		    	ordItemEntity.setItem(itemByUuid);
		    	ordItemEntity.setOrderEntity(orderEntity);
		    	ordItemEntity.setPrice(itq.getPrice()); 
		    	ordItemEntity.setQuantity(itq.getQuantity()); 
		    	
		    	orderItemEntityList.add(ordItemEntity);
		    
		    }
			
		    orderEntity.setOrderItems(orderItemEntityList);
			OrderEntity savedOrder = orderService.saveOrder(orderEntity); 
			
			SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
			saveOrderResponse.setStatus("ORDER SUCCESSFULLY PLACED"); 
			saveOrderResponse.setId(savedOrder.getUuid()); 
			
			return new ResponseEntity<SaveOrderResponse>(saveOrderResponse,HttpStatus.OK);
			
        }
        
    	return null; 
    	
    }
    
}
