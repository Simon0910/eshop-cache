package com.roncoo.eshop.cache.listener;

import com.roncoo.eshop.cache.kafka.KafkaConsumer;
import com.roncoo.eshop.cache.spring.SpringContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 系统初始化的监听器
 *
 * @author Administrator
 */
//@WebListener
public class KafkaInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        new Thread(new KafkaConsumer("cache-message")).start();
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }

}
