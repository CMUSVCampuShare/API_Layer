package com.campushare.apiLayer.model;

import com.campushare.apiLayer.utils.Status;
import com.campushare.apiLayer.utils.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationPost {
    private String postId;
    private String userId;
    private String title;
    private String from;
    private String to;
    private String details;
    private Type type;
    private Integer noOfSeats;
    private Status status;
    private Date timestamp;
}
