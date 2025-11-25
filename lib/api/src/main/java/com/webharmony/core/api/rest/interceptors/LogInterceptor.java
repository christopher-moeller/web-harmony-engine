package com.webharmony.core.api.rest.interceptors;

import com.webharmony.core.configuration.utils.EnvironmentConstants;
import com.webharmony.core.context.ContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

@Component
@Slf4j
public class LogInterceptor extends AbstractHandlerInterceptorAdapter {

    @Value( "${" + EnvironmentConstants.ENV_LOG_LOG_API_CALLS + "}" )
    private boolean logApiCalls;


    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        if(!logApiCalls) {
            return super.preHandle(request, response, handler);
        }

        final String actorName = ContextHolder.getContext().getCurrentActor().getUniqueName();
        final String servletPath = request.getServletPath();
        final String handlerPath = handler.toString();
        final String method = request.getMethod();

        if(handler instanceof HandlerMethod handlerMethod) {
            final Method javaMethod = handlerMethod.getMethod();
            log.info("API Request [{}] {} from {} to handler {}", method, servletPath, actorName, getJavaMethodInfoWithLineNumber(javaMethod));
        } else {
            log.info("API Request [{}] {} from {} to handler {}", method, servletPath, actorName, handlerPath);
        }

        return super.preHandle(request, response, handler);
    }

    @SneakyThrows
    private String getJavaMethodInfoWithLineNumber(Method javaMethod) {
        final Class<?> declaringClass = javaMethod.getDeclaringClass();
        final ClassPool pool = ClassPool.getDefault();
        final CtClass cc = pool.get(declaringClass.getName());
        final CtMethod method = cc.getDeclaredMethod(javaMethod.getName());
        int lineNumber = method.getMethodInfo().getLineNumber(0);
        return String.format("%s.%s(%s.java:%s)", declaringClass.getName(), javaMethod.getName(), declaringClass.getSimpleName(), lineNumber);
    }

}
