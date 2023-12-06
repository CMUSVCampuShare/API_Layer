package com.campushare.apiLayer.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GetAccessTokenAndCreateOrder {
    private String authorizationCode;
    private String driverId;
    private String rideId;
    private String passengerId;
}
