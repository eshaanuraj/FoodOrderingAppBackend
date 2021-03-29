package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "orders",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
  {
    		@NamedQuery(name = "getOrdersByCustomerId",query = "SELECT o FROM OrderEntity o where o.customer = :customer ORDER BY o.orderedDate DESC"),
    		@NamedQuery(name = "getOrdersByCustomerUUId",query = "SELECT o FROM OrderEntity o where o.customer = :customer"),
		  	@NamedQuery(name = "getOrdersByRestaurant", query = "select o from OrderEntity o where o.restaurant=:restaurant order by o.orderedDate desc"),
  }
)
public class OrderEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;
    
    @Column(name="bill")
    private Integer bill;
    
    @ManyToOne
    @JoinColumn(name="coupon_id")
    private CouponEntity coupon;
    
    @Column(name="discount")
    private Integer discount;
    
    @Column(name="date")
    private Date orderedDate;
    
    @ManyToOne
    @JoinColumn(name="payment_id")
    private PaymentEntity payment;
    
    @ManyToOne
    @JoinColumn(name="customer_id")
    private CustomerEntity customer;
    
    @ManyToOne
    @JoinColumn(name="address_id")
    private AddressEntity address;
    
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private RestaurantEntity restaurant;

    
    @OneToMany(mappedBy="orderEntity")
    @Cascade(CascadeType.ALL)
    private List<OrderItemEntity> orderItems;
    
    
	public OrderEntity() {
    	super();
    }
    
    
    public OrderEntity( String uuid, Integer bill, CouponEntity coupon, Integer discount,
			Date orderedDate, PaymentEntity payment, CustomerEntity customer, AddressEntity address,
			RestaurantEntity restaurant) {
		super();
		this.uuid = uuid;
		this.bill = bill;
		this.coupon = coupon;
		this.discount = discount;
		this.orderedDate = orderedDate;
		this.payment = payment;
		this.customer = customer;
		this.address = address;
		this.restaurant = restaurant;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getBill() {
		return bill;
	}

	public void setBill(Integer bill) {
		this.bill = bill;
	}

	public CouponEntity getCoupon() {
		return coupon;
	}

	public void setCoupon(CouponEntity coupon) {
		this.coupon = coupon;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public Date getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public RestaurantEntity getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(RestaurantEntity restaurant) {
		this.restaurant = restaurant;
	}

	public List<OrderItemEntity> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemEntity> orderItems) {
		this.orderItems = orderItems;
	}

	public PaymentEntity getPayment() {
		return payment;
	}

	public void setPayment(PaymentEntity payment) {
		this.payment = payment;
	}
    
	

}