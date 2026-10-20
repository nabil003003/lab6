package org.example.dao;

import org.example.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

public class GenericDAO<T, ID extends Serializable> {
    private final Class<T> classeEntite;
    protected final EntityManagerFactory fabriqueEntites;

    public GenericDAO(Class<T> classeEntite) {
        this.classeEntite = classeEntite;
        this.fabriqueEntites = JpaUtil.getEntityManagerFactory();
    }

    public T find(ID identifiant) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.find(classeEntite, identifiant);
        } finally {
            gestionnaireEntites.close();
        }
    }

    public List<T> findAll() {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            String requete = "SELECT entite FROM " + classeEntite.getSimpleName() + " entite";
            return gestionnaireEntites.createQuery(requete, classeEntite).getResultList();
        } finally {
            gestionnaireEntites.close();
        }
    }

    public void create(T entite) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        EntityTransaction transaction = gestionnaireEntites.getTransaction();
        try {
            transaction.begin();
            gestionnaireEntites.persist(entite);
            transaction.commit();
        } catch (RuntimeException erreur) {
            if (transaction.isActive()) transaction.rollback();
            throw erreur;
        } finally {
            gestionnaireEntites.close();
        }
    }

    public T update(T entite) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        EntityTransaction transaction = gestionnaireEntites.getTransaction();
        try {
            transaction.begin();
            T entiteFusionnee = gestionnaireEntites.merge(entite);
            transaction.commit();
            return entiteFusionnee;
        } catch (RuntimeException erreur) {
            if (transaction.isActive()) transaction.rollback();
            throw erreur;
        } finally {
            gestionnaireEntites.close();
        }
    }

    public void delete(T entite) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        EntityTransaction transaction = gestionnaireEntites.getTransaction();
        try {
            transaction.begin();
            T entiteAttachee = gestionnaireEntites.contains(entite)
                    ? entite : gestionnaireEntites.merge(entite);
            gestionnaireEntites.remove(entiteAttachee);
            transaction.commit();
        } catch (RuntimeException erreur) {
            if (transaction.isActive()) transaction.rollback();
            throw erreur;
        } finally {
            gestionnaireEntites.close();
        }
    }
}
