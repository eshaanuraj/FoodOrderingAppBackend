package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "state",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries({

        @NamedQuery(name = "getStateByUuid", query = "SELECT s from StateEntity s where s.stateUuid = :uuid"),
        @NamedQuery(name = "getAllStates", query = "select u from StateEntity u")

})
public class StateEntity implements Serializable {

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
    private String stateUuid;


    @Column(name = "state_name")
    @Size(max = 30)
    private String stateName;

    public StateEntity(String stateUuid, String stateName) {
        this.stateUuid = stateUuid;
        this.stateName = stateName;
        return;
    }

    public StateEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStateUuid() {
        return stateUuid;
    }

    public void setStateUuid(String stateUuid) {
        this.stateUuid = stateUuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
