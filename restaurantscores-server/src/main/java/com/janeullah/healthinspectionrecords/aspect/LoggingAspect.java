package com.janeullah.healthinspectionrecords.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;

/**
 * https://blog.jayway.com/2015/09/08/defining-pointcuts-by-annotations/
 * http://www.baeldung.com/spring-aop-annotation
 * https://docs.spring.io/spring/docs/3.0.0.M3/reference/html/ch08s06.html
 * https://docs.spring.io/spring/docs/4.3.15.RELEASE/spring-framework-reference/html/aop.html
 * https://stackoverflow.com/questions/8224465/use-of-proxies-in-spring-aop
 */
@SuppressWarnings("squid:S1186")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Aspect
@Component
public class LoggingAspect {

    // Kinded designator
    @Pointcut("execution(* com.janeullah.healthinspectionrecords.services..*.*(..))")
    private void serviceCalls() { }

    @Pointcut("execution(* com.janeullah.healthinspectionrecords.external..*.*(..))")
    private void externalServicesCalls() { }

    @Pointcut("execution(* com.janeullah.healthinspectionrecords.controller..*.*(..))")
    private void restControllerCalls() { }

    @Pointcut("execution(* com.janeullah.healthinspectionrecords.events..*.*(..))")
    private void internalEvents() { }

    // Scoping designator
    @Pointcut("within(com.janeullah.healthinspectionrecords..*)")
    private void inHealthInspectionRecordsPackage() { }

    // Contextual designator
    @Pointcut("@annotation(com.janeullah.healthinspectionrecords.annotation.LogMethodExecutionTime)")
    private void withLogMethodExecutionTimeAnnotation() {
        /*
         * no implementation needed
         */
    }

    /**
     * Uses reflection to access the injected 'log' field on the target. This means the target object must have the
     * Slf4J lombok annotation or a log field defined.
     *
     * @param proceedingJoinPoint allows us to control before & after
     * @return the result of the target object's method execution
     * @throws Throwable passing up the error
     */
    @Around("inHealthInspectionRecordsPackage() && withLogMethodExecutionTimeAnnotation() && (serviceCalls() || externalServicesCalls() || internalEvents() || restControllerCalls())")
    public Object logMethodExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = proceedingJoinPoint.proceed();
        stopWatch.stop();

        Object target = proceedingJoinPoint.getTarget();
        Field classLoggerField = target.getClass().getDeclaredField("log");
        classLoggerField.setAccessible(true);

        Logger log = (Logger) classLoggerField.get(target);

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        log.info("timeElapsed={} methodName={}", stopWatch.getTotalTimeMillis(), methodSignature.getMethod().getName());
        return result;
    }
}
