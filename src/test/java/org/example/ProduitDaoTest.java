package org.example;

import org.example.dao.GenericDAO;
import org.example.dao.ProduitDAO;
import org.example.entities.*;
import org.example.service.CrudService;
import org.example.service.CommandeService;
import org.example.service.RapportVentesService;
import org.example.util.JpaUtil;
import org.junit.*;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import static org.junit.Assert.*;

public class ProduitDaoTest {
    private static CrudService<Categorie, Long> categorieService;
    private static CrudService<Produit, Long> produitService;

    @BeforeClass
    public static void init() {
        categorieService = new CrudService<>(Categorie.class);
        produitService = new CrudService<>(Produit.class);
    }

    @AfterClass
    public static void fermer() { JpaUtil.shutdown(); }

    @Test
    public void testCreateProduitAvecCategorie() {
        Categorie categorie = new Categorie("Papeterie");
        categorieService.create(categorie);
        Produit stylo = new Produit("Stylo", new BigDecimal("1.20"), categorie);
        produitService.create(stylo);
        assertNotNull(stylo.getId());
        // JOIN FETCH : la categorie reste consultable apres fermeture de l'EntityManager.
        Produit produitCharge = new ProduitDAO().findAvecCategorie(stylo.getId());
        assertEquals("Papeterie", produitCharge.getCategorie().getNom());
        produitCharge.setLibelle("Stylo bleu");
        produitService.update(produitCharge);
        assertEquals("Stylo bleu", produitService.getById(stylo.getId()).getLibelle());
        produitService.delete(produitCharge);
        assertNull(produitService.getById(stylo.getId()));
    }

    @Test
    public void testRequetesEtPagination() {
        Categorie categorie = new Categorie("Recherche");
        categorieService.create(categorie);
        Produit clavier = new Produit("Clavier test", new BigDecimal("49.99"), categorie);
        Produit souris = new Produit("Souris test", new BigDecimal("19.99"), categorie);
        produitService.create(clavier);
        produitService.create(souris);
        ProduitDAO produitDAO = new ProduitDAO();
        assertEquals(2, produitDAO.findByCategorie(categorie.getId()).size());
        assertEquals(1, produitDAO.searchByLibelle("CLAVIER TEST").size());
        assertEquals(1, produitDAO.search("test", new BigDecimal("40"), new BigDecimal("50")).size());
        assertEquals(0, produitDAO.search("' OR 1=1 --", null, null).size());
        assertFalse(produitDAO.findAllNamed().isEmpty());
        assertEquals(1, produitDAO.findPage(1, 1).size());
        assertNotEquals(produitDAO.findPage(1, 1).get(0).getId(), produitDAO.findPage(2, 1).get(0).getId());
    }

    @Test
    public void testCommandeCleComposeeEtRapport() {
        Categorie categorie = new Categorie("Ventes");
        categorieService.create(categorie);
        Categorie sansVente = new Categorie("Sans vente");
        categorieService.create(sansVente);
        Produit premierProduit = new Produit("Produit A", new BigDecimal("10.50"), categorie);
        Produit secondProduit = new Produit("Produit B", new BigDecimal("2.00"), categorie);
        produitService.create(premierProduit);
        produitService.create(secondProduit);
        YearMonth mois = YearMonth.now().minusMonths(1);
        Commande premiereCommande = new Commande();
        premiereCommande.setDateCommande(mois.atDay(1));
        premiereCommande.addProduit(premierProduit, 3);
        premiereCommande.addProduit(secondProduit, 5);
        new CommandeService().create(premiereCommande);
        CommandeProduit ligne = premiereCommande.getLignes().get(0);
        assertEquals(premiereCommande.getId(), ligne.getId().getCommandeId());
        assertEquals(premierProduit.getId(), ligne.getId().getProduitId());
        assertNotNull(new GenericDAO<CommandeProduit, CommandeProduitPK>(CommandeProduit.class).find(ligne.getId()));
        Commande derniereCommande = new Commande();
        derniereCommande.setDateCommande(mois.atEndOfMonth());
        derniereCommande.addProduit(secondProduit, 1);
        new CommandeService().create(derniereCommande);
        Commande commandeHorsPeriode = new Commande();
        commandeHorsPeriode.setDateCommande(mois.plusMonths(1).atDay(1));
        commandeHorsPeriode.addProduit(premierProduit, 100);
        new CommandeService().create(commandeHorsPeriode);
        Map<String, BigDecimal> rapport = new RapportVentesService().rapportMoisPrecedent();
        assertEquals(0, new BigDecimal("43.50").compareTo(rapport.get("Ventes")));
        assertEquals(0, BigDecimal.ZERO.compareTo(rapport.get("Sans vente")));
    }

    @Test
    public void testHeritageJoined() {
        Client client = new Client();
        client.setNom("Client test");
        client.setDateInscription(LocalDate.now());
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom("Fournisseur test");
        fournisseur.setAdresse("Casablanca");
        new CrudService<Client, Long>(Client.class).create(client);
        new CrudService<Fournisseur, Long>(Fournisseur.class).create(fournisseur);
        GenericDAO<User, Long> utilisateurDAO = new GenericDAO<>(User.class);
        assertTrue(utilisateurDAO.find(client.getId()) instanceof Client);
        assertTrue(utilisateurDAO.find(fournisseur.getId()) instanceof Fournisseur);
    }

    @Test
    public void testRelationBidirectionnelleEtOrphanRemoval() {
        Categorie categorie = new Categorie("Orphelins");
        Produit produit = new Produit("Produit temporaire", BigDecimal.ONE, categorie);
        categorie.addProduit(produit);
        categorieService.create(categorie);
        assertNotNull(produit.getId());
        EntityManager gestionnaireEntites = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            gestionnaireEntites.getTransaction().begin();
            Categorie categorieGeree = gestionnaireEntites.find(Categorie.class, categorie.getId());
            categorieGeree.removeProduit(categorieGeree.getProduits().get(0));
            gestionnaireEntites.getTransaction().commit();
        } finally {
            if (gestionnaireEntites.getTransaction().isActive()) gestionnaireEntites.getTransaction().rollback();
            gestionnaireEntites.close();
        }
        assertNull(produitService.getById(produit.getId()));
    }

    @Test
    public void testRollbackEtReutilisationDuDao() {
        Categorie categorie = new Categorie("Unique");
        categorieService.create(categorie);
        assertThrows(RuntimeException.class, () -> categorieService.create(new Categorie("Unique")));
        assertEquals("Unique", categorieService.getById(categorie.getId()).getNom());
        assertThrows(IllegalArgumentException.class, () -> produitService.create(null));
        assertThrows(IllegalArgumentException.class, () -> new ProduitDAO().findPage(0, 10));
    }
}
