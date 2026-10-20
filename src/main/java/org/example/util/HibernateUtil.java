package org.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Variante native Hibernate du TP. Les DAO utilisent la variante JPA (JpaUtil). */
public final class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Throwable erreur) {
                System.err.println("Initial SessionFactory creation failed: " + erreur);
                throw new ExceptionInInitializerError(erreur);
            }
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
