package com.yrwcy.carpool.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class CacheDeleteDelay {

    @Autowired
    RedisUtils redisUtils;


    @Pointcut("@annotation(com.yrwcy.carpool.util.DeleteDelay)")
    public void pointCut(){

    }


    @Around("pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint){
        System.out.println("----------- 环绕通知 -----------");
        System.out.println("环绕通知的目标方法名：" + proceedingJoinPoint.getSignature().getName());

        Signature signature1 = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature1;
        Method targetMethod = methodSignature.getMethod();
        DeleteDelay annotation = targetMethod.getAnnotation(DeleteDelay.class);//反射得到自定义注解的方法对象

        String name = annotation.name();//获取自定义注解的方法对象的参数即name
        System.out.println("name:" + name);
        Object[] args = proceedingJoinPoint.getArgs();
        String key = name + args[0].toString();
        System.out.println("key:"+key);
        boolean remove = redisUtils.remove(key);

        //执行加入双删注解的改动数据库的业务 即controller中的方法业务
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        //开一个线程 延迟1秒
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("10");
                    boolean remove = redisUtils.remove(key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return proceed;//返回业务代码的值

    }

}