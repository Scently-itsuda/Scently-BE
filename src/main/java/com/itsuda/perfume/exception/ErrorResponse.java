package com.itsuda.perfume.exception;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONValue;

public abstract class ErrorResponse {
    protected final void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        Map<String, Object> map = new HashMap<>();
        map.put("result", errorCode.getCode());
        map.put("data", Collections.emptyList());
        map.put("error", errorCode.getHttpStatus().toString());
        map.put("message", errorCode.getMessage());

        response.getWriter().print(JSONValue.toJSONString(map));
    }
}