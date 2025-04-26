package com.developer.login.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedbackDto {
    private String title;
    private String content;
    private Boolean isPremium;
}
