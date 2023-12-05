package com.campushare.apiLayer.model;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ApproveJoinRequest {
    private String rideId;
    private String passengerId;
}
