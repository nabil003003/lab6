# Lab 6 — Gestion commerciale avec Hibernate

![Capture 1 : création des tables](docs/images/capture-1.jpg)

Hibernate crée les tables de la base de données, notamment les catégories, les clients, les commandes et leur association avec les produits.

![Capture 2 : contraintes et relations](docs/images/capture-2.jpg)

Hibernate ajoute la contrainte d'unicité sur le nom des catégories et les clés étrangères entre les tables.

![Capture 3 : insertion des données](docs/images/capture-3.jpg)

L'application enregistre deux catégories avec leurs identifiants générés, puis insère les produits associés.

![Capture 4 : recherche et pagination](docs/images/capture-4.jpg)

La console affiche les produits recherchés, la requête de pagination triée par identifiant et l'insertion d'un produit supplémentaire.

![Capture 5 : test JUnit réussi](docs/images/capture-5.jpg)

Le test JUnit `testCreateProduitAvecCategorie` réussit et valide la création d'un produit associé à une catégorie.

![Capture 6 : catégories dans phpMyAdmin](docs/images/capture-6.jpg)

phpMyAdmin affiche les tables de la base `gestion_commerciale` et les catégories « Électronique » et « Informatique » enregistrées avec les identifiants 1 et 2.
