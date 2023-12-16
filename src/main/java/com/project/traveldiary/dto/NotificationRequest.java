package com.project.traveldiary.dto;

import com.project.traveldiary.entity.User;
import com.project.traveldiary.type.AlarmType;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private User receiver;
    private AlarmType alarmType;
    private Map<String, String> params;
    private String path;
}
