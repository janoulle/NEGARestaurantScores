package com.janeullah.healthinspectionrecords.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

public class LoggingAspectTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private LoggingAspect loggingAspect = new LoggingAspect();

    @Test
    public void testLogMethodExecutionTime() throws Throwable {
        MockClass mockClass = new MockClass();
        when(proceedingJoinPoint.getTarget()).thenReturn(mockClass);

        MethodSignature methodSignature = new MethodSignature() {
            @Override
            public Class getReturnType() {
                return null;
            }

            @Override
            public Method getMethod() {
                try {
                    return mockClass.getClass().getMethod("mockMethod");
                } catch (Exception e){
                    return null;
                }
            }

            @Override
            public Class[] getParameterTypes() {
                return new Class[0];
            }

            @Override
            public String[] getParameterNames() {
                return new String[0];
            }

            @Override
            public Class[] getExceptionTypes() {
                return new Class[0];
            }

            @Override
            public String toShortString() {
                return null;
            }

            @Override
            public String toLongString() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public int getModifiers() {
                return 0;
            }

            @Override
            public Class getDeclaringType() {
                return null;
            }

            @Override
            public String getDeclaringTypeName() {
                return null;
            }
        };
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);

        loggingAspect.logMethodExecutionTime(proceedingJoinPoint);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    private class MockClass {
        private final Logger log = LoggerFactory.getLogger(MockClass.class);

        public void mockMethod() {

        }
    }

}
