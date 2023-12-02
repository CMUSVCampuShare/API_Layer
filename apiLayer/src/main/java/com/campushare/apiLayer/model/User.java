package com.campushare.apiLayer.model;

import com.campushare.apiLayer.utils.Role;

/* import com.campushare.userservice.utils.Role;
import com.campushare.userservice.utils.Schedule;
import com.campushare.userservice.utils.Address; */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userId;
    private String username;
    private String password;
    private Role role;

 /*    private String email;
 private String entryTime;
private String exitTime;
 private String address; 
 private String account;
 private Integer noOfSeats;
 private String licenseNo; */

}