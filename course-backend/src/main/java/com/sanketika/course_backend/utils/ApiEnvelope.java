package com.sanketika.course_backend.utils;

import lombok.Data;

// import java.time.LocalDateTime;

@Data
public class ApiEnvelope<T> {

    private String id;
    private String ver;
    private String ts;
    private String responseCode;
    private Params params = new Params();
    private ApiResult<T> result;

}
