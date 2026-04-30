package com.ems.aspect;

import com.ems.annotation.OpLog;
import com.ems.entity.User;
import com.ems.mapper.UserMapper;
import com.ems.service.OperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private UserMapper userMapper;

    @Around("@annotation(opLog)")
    public Object recordLog(ProceedingJoinPoint joinPoint, OpLog opLog) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            String username = "unknown";
            Long userId = null;

            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                username = auth.getPrincipal().toString();
                User user = userMapper.selectOne(
                        new LambdaQueryWrapper<User>().eq(User::getUsername, username));
                if (user != null) {
                    userId = user.getId();
                }
            }

            // 尝试从方法参数中提取目标ID
            String target = extractTarget(joinPoint);

            // 获取IP
            String ip = getClientIp();

            operationLogService.record(userId, username, opLog.module(), opLog.action(), target, null, ip);
        } catch (Exception ignored) {
            // 日志记录失败不影响业务
        }

        return result;
    }

    private String extractTarget(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                if ("id".equals(paramNames[i]) && args[i] != null) {
                    return args[i].toString();
                }
            }
        }
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                // 多级代理取第一个
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception ignored) {
        }
        return "unknown";
    }
}
