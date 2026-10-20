package org.example.service;

import org.example.entities.Commande;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/** Les produits lus par le CRUD sont detached : les rattacher pour @MapsId. */
public class CommandeService extends CrudService<Commande, Long> {
    public CommandeService() { super(Commande.class); }

    @Override
    public void create(Commande commande) {
        if (commande == null) throw new IllegalArgumentException("L'entité ne doit pas être nulle");
        EntityManager gestionnaireEntites = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = gestionnaireEntites.getTransaction();
        try {
            transaction.begin();
            for (CommandeProduit ligne : commande.getLignes()) {
                Produit produitGere = gestionnaireEntites.getReference(Produit.class, ligne.getProduit().getId());
                ligne.setProduit(produitGere);
            }
            gestionnaireEntites.persist(commande);
            transaction.commit();
        } catch (RuntimeException erreur) {
            if (transaction.isActive()) transaction.rollback();
            throw erreur;
        } finally {
            gestionnaireEntites.close();
        }
    }
}
