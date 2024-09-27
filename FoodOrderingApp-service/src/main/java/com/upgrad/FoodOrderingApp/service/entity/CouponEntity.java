package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "coupon", schema = "public")
@NamedQueries({
    @NamedQuery(name="getCouponDetailsByName",query="select u from CouponEntity u where u.couponName = :coupon_name"),
    @NamedQuery(name="getCouponEntityByUuid",query="select u from CouponEntity u where u.uuid = :uuid")
})
public class CouponEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "uuid")
	private UUID uuid;

	@Column(name="coupon_name")
	private String couponName;
	
	@Column(name="percent")
	private Integer percent;
	
	
	public CouponEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CouponEntity(UUID uuid, String couponName, Integer percent) {
		super();
		this.uuid = uuid;
		this.couponName = couponName;
		this.percent = percent;
	}

	public CouponEntity(String uuid, String couponName, Integer percent) {
		super();
		this.uuid = UUID.fromString(uuid); 
		this.couponName = couponName;
		this.percent = percent;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Integer getPercent() {
		return percent;
	}

	public void setPercent(Integer percent) {
		this.percent = percent;
	}
	
	
	
	
}
