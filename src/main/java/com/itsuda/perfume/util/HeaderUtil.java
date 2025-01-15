package com.itsuda.perfume.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class HeaderUtil {
    public static Optional<String> refineHeader(HttpServletRequest request, String header, String prefix) {
        String unpreparedToken = request.getHeader(header);

        if (!StringUtils.hasText(unpreparedToken) || !unpreparedToken.startsWith(prefix)) {
            return Optional.empty();
        }

        return Optional.of(unpreparedToken.substring(prefix.length()));
    }
}
