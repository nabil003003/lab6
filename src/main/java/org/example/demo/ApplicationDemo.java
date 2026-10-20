package org.example.demo;

import org.example.dao.GenericDAO;
import org.example.dao.ProduitDAO;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.CommandeService;
import org.example.util.JpaUtil;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Exemples du TP, a executer une seule fois dans une base de demonstration vide. */
public class ApplicationDemo {
    public static void main(String[] arguments) {
        try {
            GenericDAO<Categorie, Long> categorieDAO = new GenericDAO<>(Categorie.class);
            Categorie categorieElectronique = new Categorie("Électronique");
            categorieDAO.create(categorieElectronique);
            System.out.println("Catégorie créée avec l'ID : " + categorieElectronique.getId());

            CrudService<Categorie, Long> categorieService = new CrudService<>(Categorie.class);
            Categorie categorieInformatique = new Categorie("Informatique");
            categorieService.create(categorieInformatique);
            System.out.println("Nouvelle catégorie ID = " + categorieInformatique.getId());

            CrudService<Produit, Long> produitService = new CrudService<>(Produit.class);
            Produit clavier = new Produit("Clavier", new BigDecimal("49.99"), categorieInformatique);
            Produit souris = new Produit("Souris", new BigDecimal("19.99"), categorieInformatique);
            produitService.create(clavier);
            produitService.create(souris);
            CommandeService commandeService = new CommandeService();
            Produit premierProduit = produitService.getById(clavier.getId());
            Produit secondProduit = produitService.getById(souris.getId());
            Commande commande = new Commande();
            commande.setDateCommande(LocalDate.now());
            commande.addProduit(premierProduit, 3);
            commande.addProduit(secondProduit, 5);
            commandeService.create(commande);
            System.out.println("Commande créée avec ID = " + commande.getId());

            ProduitDAO produitDAO = new ProduitDAO();
            System.out.println(produitDAO.findAllNamed());
            System.out.println(produitDAO.findByCategorie(categorieInformatique.getId()));
            System.out.println(produitDAO.searchByLibelle("clav"));
            System.out.println(produitDAO.search("clav", BigDecimal.TEN, new BigDecimal("100")));
            System.out.println(produitDAO.findPage(1, 10));
            exempleTransaction(categorieInformatique.getId());
        } finally {
            JpaUtil.shutdown();
        }
    }

    public static void exempleTransaction(Long categorieId) {
        EntityManager gestionnaireEntites = JpaUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = gestionnaireEntites.getTransaction();
        try {
            transaction.begin();
            Categorie categorie = gestionnaireEntites.find(Categorie.class, categorieId);
            Produit produit = new Produit("Clavier", new BigDecimal("49.99"), categorie);
            gestionnaireEntites.persist(produit);
            if (produit.getPrix().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Prix négatif");
            }
            transaction.commit();
        } catch (RuntimeException erreur) {
            if (transaction.isActive()) transaction.rollback();
            System.err.println("Transaction annulée : " + erreur.getMessage());
        } finally {
            gestionnaireEntites.close();
        }
    }
}
