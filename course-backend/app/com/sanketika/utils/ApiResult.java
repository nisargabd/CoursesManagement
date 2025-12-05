package com.sanketika.utils;

import lombok.Data;

@Data
public class ApiResult<T> {
    private String message;
    private T data;
}
