package com.wb2code.microbox.annotation.entity;

import lombok.Data;

/**
 * @author lwp
 * @date 2023-08-19
 **/
@Data
public class Result<T> {
    private boolean isSuccess;
    private String error;
    private T data;

    public static <T> Result<T> success(T data) {
        final Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(String error) {
        final Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }
}
