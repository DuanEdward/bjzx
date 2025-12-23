package com.hebei.cs.common;

/**
 * 统一响应结果
 *
 * @author Hebei CS
 * @since 2024-01-10
 */
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message) {
        this();
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    /**
     * 成功
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功");
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功（带消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 设置数据
     */
    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败");
    }

    /**
     * 失败（带消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message);
    }

    /**
     * 失败（带状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权");
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问");
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound() {
        return new Result<>(404, "资源不存在");
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }

    // Getters and Setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}