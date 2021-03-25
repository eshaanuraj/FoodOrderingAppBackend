package com.upgrad.FoodOrderingApp.service.entity;

//This Class represents the RestaurantItem table in the DB

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_item")
@NamedQueries({

})
public class RestaurantItemEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "item_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @NotNull
//    private ItemEntity item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private RestaurantEntity restaurant;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }


}
