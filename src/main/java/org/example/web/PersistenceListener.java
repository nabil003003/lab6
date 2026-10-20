package org.example.web;

import org.example.util.HibernateUtil;
import org.example.util.JpaUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PersistenceListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent evenement) {
        JpaUtil.getEntityManagerFactory();
    }
    @Override
    public void contextDestroyed(ServletContextEvent evenement) {
        try { JpaUtil.shutdown(); }
        finally { HibernateUtil.shutdown(); }
    }
}
