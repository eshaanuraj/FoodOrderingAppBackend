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
        @NamedQuery(name = "getAllStates", query = "select u from StateEntity u"),
        @NamedQuery(name = "getStateById", query = "SELECT s from StateEntity s where s.id = :id")
})
public class StateEntity implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 


	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String Uuid;


    @Column(name = "state_name")
    @Size(max = 30)
    private String stateName;

    public StateEntity(String Uuid, String stateName) {
        this.Uuid = Uuid;
        this.stateName = stateName;
        return;
    }

    public StateEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStateUuid(String stateUuid) {
        this.Uuid = stateUuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getUuid() {
        return Uuid;
    }
    
}
