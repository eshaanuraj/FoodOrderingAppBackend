package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table (name = "address",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries({

        @NamedQuery(name = "getAddressByUuid",query = "SELECT a from AddressEntity a where a.uuid = :uuid"),
})
public class AddressEntity implements Serializable {


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

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flatBuilNo;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    @Size(max = 30)
    private String pincode;

    @Column(name = "active")
    private Integer active = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private StateEntity state;

    public AddressEntity(){
    	
    }

    public AddressEntity(String uuid, String flatBuilNo, String locality, String city, String pincode, StateEntity stateEntity) {
        this.uuid = uuid;
        this.flatBuilNo =flatBuilNo;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state = stateEntity;
        return;
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

    public String getFlatBuilNo() {
        return flatBuilNo;
    }

    public void setFlatBuilNo(String flatBuilNo) {
        this.flatBuilNo = flatBuilNo;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

}
