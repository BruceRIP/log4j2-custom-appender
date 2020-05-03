package com.log4j2.custom.appender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    public static Logger log = LogManager.getLogger(AppTest.class);

    @BeforeAll
    public static void init() {
        System.setProperty("log4j2.contextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        ThreadContext.put("identity", String.valueOf(System.currentTimeMillis()));
    }

    @Test
    public void suite() {
        Assertions.assertNotNull(System.getProperty("log4j2.contextSelector"));
        long random = System.currentTimeMillis();
        System.out.println("Execute before or after? INFO - START");
        log.info("This is a INFO message [{}]", random);
        System.out.println("Execute before or after? INFO - END");
        System.out.println("========================================");

        System.out.println("Execute before or after? DEBUG - START");
        log.debug("Alright! I need to put a DEBUG message here [{}]", random);
        System.out.println("Execute before or after? DEBUG - END");
        System.out.println("========================================");

        System.out.println("Execute before or after? WARN - START");
        log.warn("Woo! this is a WARN message [{}]", random);
        System.out.println("Execute before or after? WARN - END");
        System.out.println("========================================");

        System.out.println("Execute before or after? ERROR - START");
        log.error("Holy crap this is an ERROR message [{}]", random);
        System.out.println("Execute before or after? ERROR - END");
        System.out.println("========================================");

        System.out.println("Execute before or after? TRACE - START");
        log.trace("Ok! I need to put TRACE message [{}]", random);
        System.out.println("Execute before or after? TRACE - END");
    }

}
