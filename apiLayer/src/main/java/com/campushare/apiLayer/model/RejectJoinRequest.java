package com.campushare.apiLayer.model;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class RejectJoinRequest {
    private String rideId;
    private String rideTitle;
    private String passengerId;
}
