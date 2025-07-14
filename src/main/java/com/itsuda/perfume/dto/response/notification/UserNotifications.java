package com.itsuda.perfume.dto.response.notification;

import com.itsuda.perfume.dto.response.PageInfoDto;

import java.util.List;

public record UserNotifications(
        List<UserNotification> dataList,
        PageInfoDto pageInfo
) {
}
