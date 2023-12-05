package com.campushare.apiLayer.model;

import lombok.Data;

@Data
public class FoodRequest {
    private String driverID;
    private String passengerID;
    private String postId;
}
