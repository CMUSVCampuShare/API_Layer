package com.campushare.apiLayer.model;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AuthorizeOrder {
    private String rideId;
    private String passengerId;
}
