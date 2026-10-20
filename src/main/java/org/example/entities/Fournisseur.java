package org.example.entities;

import javax.persistence.*;

@Entity
@Table(name = "fournisseurs")
public class Fournisseur extends User {
    @Column(name = "adresse")
    private String adresse;
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}
