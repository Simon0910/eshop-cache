package com.roncoo.eshop.cache.listener;

import com.roncoo.eshop.cache.rebuild.RebuildCacheThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RebuildInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        new Thread(new RebuildCacheThread()).start();
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }

}
