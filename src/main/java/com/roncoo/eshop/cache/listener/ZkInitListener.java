package com.roncoo.eshop.cache.listener;

import com.roncoo.eshop.cache.zk.ZooKeeperSession;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ZkInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ZooKeeperSession.init();
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
