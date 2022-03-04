package com.userbot.test.aspect;

import com.userbot.test.controller.CountryController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RestAspect {

    private final static Logger logger = LoggerFactory.getLogger(RestAspect.class);

    @Before(value = "execution(* com.userbot.test.controller.CountryController.*(..))")
    public void beforeAdviceCountry(JoinPoint joinPoint) {
        logger.info("Call Country method: " + joinPoint.getSignature());
    }

    @Before(value = "execution(* com.userbot.test.controller.RegionController.*(..))")
    public void beforeAdviceRegion(JoinPoint joinPoint) {
        logger.info("Call Region method: " + joinPoint.getSignature());
    }

    @Before(value = "execution(* com.userbot.test.controller.CityController.*(..))")
    public void beforeAdviceCity(JoinPoint joinPoint) {
        logger.info("Call City method: " + joinPoint.getSignature());
    }

}
