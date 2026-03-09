package com.ems.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> ok() {
        return new R<>(200, "success", null);
    }

    public static <T> R<T> fail(String message) {
        return new R<>(400, message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }
}
