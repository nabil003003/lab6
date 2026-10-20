package org.example.service;

import org.example.entities.Categorie;
import org.example.entities.CommandeProduit;
import org.example.entities.Produit;
import org.example.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class RapportVentesService {
    public Map<String, BigDecimal> rapportMoisPrecedent() {
        return rapportMensuel(YearMonth.now().minusMonths(1));
    }

    public Map<String, BigDecimal> rapportMensuel(YearMonth mois) {
        EntityManager gestionnaireEntites = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            CriteriaBuilder constructeur = gestionnaireEntites.getCriteriaBuilder();
            // Inclure les categories sans vente avec un total de zero.
            CriteriaQuery<String> requeteCategories = constructeur.createQuery(String.class);
            Root<Categorie> categorie = requeteCategories.from(Categorie.class);
            requeteCategories.select(categorie.get("nom")).orderBy(constructeur.asc(categorie.get("nom")));
            Map<String, BigDecimal> totaux = new LinkedHashMap<>();
            for (String nomCategorie : gestionnaireEntites.createQuery(requeteCategories).getResultList()) {
                totaux.put(nomCategorie, BigDecimal.ZERO);
            }
            CriteriaQuery<Tuple> requete = constructeur.createTupleQuery();
            Root<CommandeProduit> ligne = requete.from(CommandeProduit.class);
            Join<CommandeProduit, Produit> produit = ligne.join("produit");
            Path<String> nomCategorie = produit.get("categorie").get("nom");
            Path<LocalDate> dateCommande = ligne.get("commande").get("dateCommande");
            Expression<BigDecimal> montant = constructeur.prod(produit.<BigDecimal>get("prix"),
                    ligne.get("quantite").as(BigDecimal.class));
            requete.multiselect(nomCategorie.alias("categorie"), constructeur.sum(montant).alias("total"))
                    .where(constructeur.greaterThanOrEqualTo(dateCommande, mois.atDay(1)),
                            constructeur.lessThan(dateCommande, mois.plusMonths(1).atDay(1)))
                    .groupBy(nomCategorie);
            for (Tuple resultat : gestionnaireEntites.createQuery(requete).getResultList()) {
                BigDecimal total = resultat.get("total", BigDecimal.class);
                totaux.put(resultat.get("categorie", String.class), total == null ? BigDecimal.ZERO : total);
            }
            return totaux;
        } finally { gestionnaireEntites.close(); }
    }
}
