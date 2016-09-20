package com.janeullah.healthinspectionrecords.org.model;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Author: jane
 * Date:  9/19/2016
 */
public class ResultSetRestaurantController {
    CountDownLatch signal;

    public CountDownLatch getSignal() {
        return signal;
    }

    public void setSignal(CountDownLatch signal) {
        this.signal = signal;
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    List<Restaurant> restaurantList;

    ResultSetRestaurantController(CountDownLatch signal, List<Restaurant> restaurants){
        this.signal = signal;
        this.restaurantList = restaurants;
    }
}
