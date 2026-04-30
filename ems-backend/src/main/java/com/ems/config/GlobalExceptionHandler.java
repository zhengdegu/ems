package com.ems.config;

import com.ems.aspect.PermissionAspect;
import com.ems.dto.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PermissionAspect.PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> handlePermissionDenied(PermissionAspect.PermissionDeniedException e) {
        return R.fail(403, e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return R.fail("文件大小超出限制");
    }

    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntime(RuntimeException e) {
        return R.fail(e.getMessage());
    }
}
