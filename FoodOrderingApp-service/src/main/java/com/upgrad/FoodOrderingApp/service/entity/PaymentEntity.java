package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "payment")
@NamedQueries(
        {
                @NamedQuery(name = "getAllPaymentMethods", query = "select p from PaymentEntity p "),
                @NamedQuery(name="getPaymentbyUuid" , query="select p from PaymentEntity p where p.uuid = :uuid"),
        }
)

public class PaymentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name="payment_name")
    @NotNull
    @Size(max = 255)
    private String paymentName;
    
    

    public PaymentEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PaymentEntity(String uuid, String paymentName) { 
		super();
		this.uuid = uuid;
		this.paymentName = paymentName;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}

