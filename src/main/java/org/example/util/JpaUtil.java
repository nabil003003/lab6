package org.example.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/** Fabrique JPA de defaultPU, partagee par les DAO et services. */
public final class JpaUtil {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory(System.getProperty("gestion.persistence.unit", "defaultPU"));

    private JpaUtil() { }

    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    public static void shutdown() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
