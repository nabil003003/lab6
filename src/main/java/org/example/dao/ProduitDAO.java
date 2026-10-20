package org.example.dao;

import org.example.entities.Produit;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProduitDAO extends GenericDAO<Produit, Long> {
    public ProduitDAO() { super(Produit.class); }

    public List<Produit> findByCategorie(Long categorieId) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.createNamedQuery("Produit.findByCategorie", Produit.class)
                    .setParameter("categorieId", categorieId).getResultList();
        } finally { gestionnaireEntites.close(); }
    }

    public List<Produit> searchByLibelle(String terme) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.createNamedQuery("Produit.searchByLibelle", Produit.class)
                    .setParameter("terme", terme).getResultList();
        } finally { gestionnaireEntites.close(); }
    }

    public List<Produit> findAllNamed() {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.createNamedQuery("Produit.findAll", Produit.class).getResultList();
        } finally { gestionnaireEntites.close(); }
    }

    /** Charge la categorie avant de fermer le contexte de persistance. */
    public Produit findAvecCategorie(Long identifiant) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.createQuery(
                    "SELECT produit FROM Produit produit JOIN FETCH produit.categorie WHERE produit.id = :identifiant",
                    Produit.class).setParameter("identifiant", identifiant)
                    .getResultStream().findFirst().orElse(null);
        } finally { gestionnaireEntites.close(); }
    }

    public List<Produit> search(String terme, BigDecimal prixMinimum, BigDecimal prixMaximum) {
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            CriteriaBuilder constructeur = gestionnaireEntites.getCriteriaBuilder();
            CriteriaQuery<Produit> requete = constructeur.createQuery(Produit.class);
            Root<Produit> produit = requete.from(Produit.class);
            List<Predicate> conditions = new ArrayList<>();
            if (terme != null && !terme.isEmpty()) {
                conditions.add(constructeur.like(constructeur.lower(produit.get("libelle")),
                        "%" + terme.toLowerCase(Locale.ROOT) + "%"));
            }
            if (prixMinimum != null) conditions.add(constructeur.ge(produit.get("prix"), prixMinimum));
            if (prixMaximum != null) conditions.add(constructeur.le(produit.get("prix"), prixMaximum));
            requete.select(produit).where(conditions.toArray(new Predicate[0]));
            return gestionnaireEntites.createQuery(requete).getResultList();
        } finally { gestionnaireEntites.close(); }
    }

    public List<Produit> findPage(int numeroPage, int taillePage) {
        if (numeroPage < 1 || taillePage < 1) {
            throw new IllegalArgumentException("Le numéro et la taille de page doivent être positifs");
        }
        int decalage = Math.multiplyExact(numeroPage - 1, taillePage);
        EntityManager gestionnaireEntites = fabriqueEntites.createEntityManager();
        try {
            return gestionnaireEntites.createQuery("SELECT produit FROM Produit produit ORDER BY produit.id", Produit.class)
                    .setFirstResult(decalage).setMaxResults(taillePage).getResultList();
        } finally { gestionnaireEntites.close(); }
    }
}
