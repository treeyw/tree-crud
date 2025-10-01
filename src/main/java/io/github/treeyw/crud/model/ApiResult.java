package io.github.treeyw.crud.model;


public record ApiResult<T>(
        int code,          // 0=OK
        String msg,    // "OK" 或错误信息
        T data,
        long timestamp   // System.currentTimeMillis()
) {
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "OK", data, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> error(int code, String msg) {
        return new ApiResult<>(code, msg, null, System.currentTimeMillis());
    }
}