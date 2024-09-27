package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private CustomerService cuustomerService;

	@Autowired
	private RestaurantService restaurantService;

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(
			@RequestHeader("authorization") final String authorization, @PathVariable("coupon_name") String couponName)
			throws AuthenticationFailedException, AuthorizationFailedException, AddressNotFoundException,
			CouponNotFoundException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		cuustomerService.getCustomer(accessToken);

		CouponEntity couponByName = orderService.getCouponByCouponName(couponName);

		CouponDetailsResponse response = new CouponDetailsResponse();
		response.setId(couponByName.getUuid());
		response.setCouponName(couponByName.getCouponName());
		response.setPercent(couponByName.getPercent());
		return new ResponseEntity<CouponDetailsResponse>(response, HttpStatus.OK);

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<CustomerOrderResponse> getPastOrdersOfUser(
			@RequestHeader("authorization") final String authorization)
			throws AuthenticationFailedException, AuthorizationFailedException, AddressNotFoundException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		CustomerEntity customer = cuustomerService.getCustomer(accessToken);

		List<OrderEntity> allOrdersForCustomer = orderService.getOrdersByCustomers(customer);

		List<OrderList> completeOrderList = new ArrayList<OrderList>();
        if(!CollectionUtils.isEmpty(allOrdersForCustomer)) {
			for (OrderEntity orderEntity : allOrdersForCustomer) {
				OrderList orderList = new OrderList();
	
				orderList.setAddress(getOrderListAddress(orderEntity));
				orderList.setBill(BigDecimal.valueOf(orderEntity.getBill()));
				orderList.setCoupon(getOrderListCoupon(orderEntity));
				orderList.setCustomer(getOrderListCustomer(orderEntity));
				orderList.setDate(orderEntity.getOrderedDate().toString());
				orderList.setDiscount(BigDecimal.valueOf(orderEntity.getDiscount()));
				orderList.setId(UUID.fromString(orderEntity.getUuid()));
				if(getOrderListItemQuantities(orderEntity) != null) {
				   orderList.setItemQuantities(getOrderListItemQuantities(orderEntity));
				}
				orderList.setPayment(getOrderListPayment(orderEntity));
	
				completeOrderList.add(orderList);
	
			}
        }

        CustomerOrderResponse response = new CustomerOrderResponse();
        response.setOrders(completeOrderList); 
		return new ResponseEntity<CustomerOrderResponse>(response, HttpStatus.OK);


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
		addrState.setId(UUID.fromString(address.getState().getUuid()));
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
       
		if(CollectionUtils.isEmpty(orderItems)) {
			return null;
		}
		
		for (OrderItemEntity orderItemEntity : orderItems) {
			ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
			itemQuantityResponse.setPrice(orderItemEntity.getPrice());
			itemQuantityResponse.setQuantity(orderItemEntity.getQuantity());

			ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
			itemQuantityResponseItem.setId(UUID.fromString(orderItemEntity.getItem().getUuid()));
			itemQuantityResponseItem.setItemName(orderItemEntity.getItem().getItemName());
			itemQuantityResponseItem.setItemPrice(orderItemEntity.getItem().getPrice());
			itemQuantityResponseItem
					.setType(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItem().getType().toString()));

			itemQuantityResponse.setItem(itemQuantityResponseItem);

			itemQuantityList.add(itemQuantityResponse);
		}

		return itemQuantityList;
	}

	private OrderListPayment getOrderListPayment(OrderEntity orderEntity) {

		PaymentEntity paymentEntity = orderEntity.getPayment();

		OrderListPayment orderListPayment = new OrderListPayment();
		orderListPayment.setId(UUID.fromString(paymentEntity.getUuid()));
		orderListPayment.setPaymentName(paymentEntity.getPaymentName());

		return orderListPayment;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,
			@RequestBody(required=false) final SaveOrderRequest saveOrderRequest) throws AuthenticationFailedException,
			AuthorizationFailedException, AddressNotFoundException, RestaurantNotFoundException,
			CouponNotFoundException, ItemNotFoundException, PaymentMethodNotFoundException {

		String[] splitText = authorization.split(" ");
		String accessToken = new String(splitText[1]);

		CustomerEntity customerEntity = cuustomerService.getCustomer(accessToken); 

		String addressId = saveOrderRequest.getAddressId();
		BigDecimal bill = saveOrderRequest.getBill();
		UUID couponId = saveOrderRequest.getCouponId();
		BigDecimal discount = saveOrderRequest.getDiscount();
		List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
		UUID paymentId = saveOrderRequest.getPaymentId();
		UUID restaurantId = saveOrderRequest.getRestaurantId();

		// Building OrderEntity Object
		OrderEntity orderEntity = new OrderEntity();


		orderEntity.setBill(bill.intValue());

		CouponEntity couponByUuid = orderService.getCouponByCouponId(couponId.toString());
		orderEntity.setCoupon(couponByUuid);
		orderEntity.setCustomer(customerEntity);
		orderEntity.setDiscount(discount.intValue());
		orderEntity.setOrderedDate(new Date());

		PaymentEntity paymentByUUID = paymentService.getPaymentByUUID(paymentId.toString());
		orderEntity.setPayment(paymentByUUID);

		AddressEntity addressByUuid = addressService.getAddressByUUID(addressId,customerEntity);
		
		orderEntity.setAddress(addressByUuid);
		
		RestaurantEntity restaurantByUUID = restaurantService.restaurantByUUID(restaurantId.toString());
		
		orderEntity.setRestaurant(restaurantByUUID);
		

		orderEntity.setUuid(UUID.randomUUID().toString());

		List<OrderItemEntity> orderItemEntityList = new ArrayList<OrderItemEntity>();

		for (ItemQuantity itq : itemQuantities) {

			OrderItemEntity ordItemEntity = new OrderItemEntity();

			ItemEntity itemByUuid = orderService.getItemByUuid(itq.getItemId());
			
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

		return new ResponseEntity<SaveOrderResponse>(saveOrderResponse, HttpStatus.CREATED);

	}

}
