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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "item",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries(
  {
    @NamedQuery(name = "getItemByUuid",query = "SELECT it FROM ItemEntity it where it.uuid = :uuid ")
  }
)
public class ItemEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "uuid")
	private UUID uuid;
	
	@Column(name = "item_name")
	private String itemName;
	
	@Column(name = "price")
	private Integer price;
	
	@Column(name = "type")
	private String type;

	public ItemEntity() {
	   super();	
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
