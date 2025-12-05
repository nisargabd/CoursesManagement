package com.sanketika.utils;

import lombok.Data;

@Data
public class ApiEnvelope<T> {

    private String id;
    private String ver;
    private String ts;
    private String responseCode;
    private Params params = new Params();
    private ApiResult<T> result;

}
