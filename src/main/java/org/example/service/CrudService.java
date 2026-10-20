package org.example.service;

import org.example.dao.GenericDAO;
import java.io.Serializable;
import java.util.List;

public class CrudService<T, ID extends Serializable> {
    private final GenericDAO<T, ID> dao;

    public CrudService(Class<T> classeEntite) { dao = new GenericDAO<>(classeEntite); }
    public T getById(ID identifiant) { return dao.find(identifiant); }
    public List<T> getAll() { return dao.findAll(); }
    public void create(T entite) { verifierEntite(entite); dao.create(entite); }
    public T update(T entite) { verifierEntite(entite); return dao.update(entite); }
    public void delete(T entite) { verifierEntite(entite); dao.delete(entite); }

    private void verifierEntite(T entite) {
        if (entite == null) throw new IllegalArgumentException("L'entité ne doit pas être nulle");
    }
}
