package com.upgrad.FoodOrderingApp.service.entity;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "restaurant")
@NamedQueries(
        {
                @NamedQuery(name = "getAllRestaurants", query = "select r from RestaurantEntity r order by r.customerRating desc"),
                @NamedQuery(name = "getRestaurantByName", query = "select r from RestaurantEntity  r where lower(r.restaurantName) like :restaurantName order by r.restaurantName"),
                @NamedQuery(name = "getRestaurantByUUId",query = "select r from RestaurantEntity r where lower(r.uuid) = :uuid"),
                @NamedQuery(name = "getRestaurantsByRating",query = "SELECT r FROM RestaurantEntity r ORDER BY r.customerRating DESC")
        }
)

public class RestaurantEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "restaurant_name")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "photo_url")
    @NotNull
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "customer_rating")
    private double customerRating;

    @Column(name = "number_of_customers_rated")
    @NotNull
    private Integer numCustomersRated;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer avgPriceForTwo;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "restaurant_category",
            joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName="id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName="id", nullable = false)
    )
    private Set<CategoryEntity> categoryEntities = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "restaurant_item",
            joinColumns = @JoinColumn(name = "restaurant_id", referencedColumnName="id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "item_id", referencedColumnName="id", nullable = false)
    )

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

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getNumCustomersRated() {
        return numCustomersRated;
    }

    public void setNumCustomersRated(Integer numCustomersRated) {
        this.numCustomersRated = numCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public Set<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    public void setCategoryEntities(Set<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public Integer getAvgPriceForTwo() {
        return avgPriceForTwo;
    }
    public void setAvgPriceForTwo(Integer avgPriceForTwo) {
        this.avgPriceForTwo = avgPriceForTwo;
    }

}
