package com.campushare.apiLayer.model;

import lombok.Data;

@Data
public class JoinRequest {
    private String driverID;
    private String passengerID;
    private String to;
    private String from;
}
