package org.example.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommandeProduitPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "commande_id")
    private Long commandeId;
    @Column(name = "produit_id")
    private Long produitId;

    public CommandeProduitPK() { }
    public Long getCommandeId() { return commandeId; }
    public void setCommandeId(Long commandeId) { this.commandeId = commandeId; }
    public Long getProduitId() { return produitId; }
    public void setProduitId(Long produitId) { this.produitId = produitId; }
    @Override
    public boolean equals(Object objet) {
        if (this == objet) return true;
        if (!(objet instanceof CommandeProduitPK)) return false;
        CommandeProduitPK autreCle = (CommandeProduitPK) objet;
        return Objects.equals(commandeId, autreCle.commandeId) && Objects.equals(produitId, autreCle.produitId);
    }
    @Override
    public int hashCode() { return Objects.hash(commandeId, produitId); }
}
