# BookHub - Backend

API REST d'une application de gestion de bibliothèque. Permet la gestion des livres, emprunts, réservations, notes et utilisateurs.

## Stack technique

- **Java** avec **Spring Boot 4.0.5**
- **Spring Security** + **JWT** (authentification stateless)
- **Spring Data JPA** + **Hibernate**
- **SQL Server** (base de données)
- **MapStruct** (mapping entités → DTOs)
- **Lombok** (réduction du boilerplate)
- **SpringDoc OpenAPI** (documentation Swagger)
- **Gradle** (build)

---

## Prérequis

- Java 21+
- SQL Server avec une base de données `bookhub_db`
- Gradle (ou utiliser le wrapper `./gradlew`)

---

## Configuration

Le fichier `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=bookhub_db
spring.datasource.username=sa
spring.datasource.password=Pa$$w0rd
spring.jpa.hibernate.ddl-auto=update

jwt.secret=VGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JCb29rSHViMjAyNiE=
jwt.expiration=86400000
```

Le schéma est généré automatiquement au démarrage (`ddl-auto=update`).

---

## Lancer le projet

```bash
./gradlew bootRun
```

L'API est disponible sur `http://localhost:8080`.

---

## Documentation Swagger

Une fois le serveur lancé :

```
http://localhost:8080/swagger-ui/
```

Les endpoints protégés nécessitent un token JWT. Dans Swagger, cliquer sur **Authorize** et saisir `Bearer <token>`.

---

## Authentification

L'API utilise **JWT**. Le token est valable **24h**.

| Endpoint | Méthode | Description |
| --- | --- | --- |
| `/api/auth/register` | POST | Créer un compte |
| `/api/auth/login` | POST | Se connecter, retourne le token JWT |

Le token doit être envoyé dans le header de chaque requête protégée :

```
Authorization: Bearer <token>
```

---

## Rôles

| Rôle | Description |
| --- | --- |
| `USER` | Rôle par défaut. Peut emprunter, réserver, noter. |
| `LIBRARIAN` | Peut gérer les livres, auteurs, catégories et accéder aux statistiques. |
| `ADMIN` | Accès complet, gestion des utilisateurs et rôles. |

---

## Endpoints

### Utilisateurs — `/api/users`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/users/me` | GET | Authentifié | Voir son profil |
| `/api/users/me` | PUT | Authentifié | Modifier son profil |
| `/api/users/me/password` | PUT | Authentifié | Changer son mot de passe |
| `/api/users/me` | DELETE | Authentifié | Supprimer son compte |
| `/api/users` | GET | ADMIN | Lister tous les utilisateurs |
| `/api/users/{id}` | GET | ADMIN | Voir un utilisateur |
| `/api/users/{id}/role` | PUT | ADMIN | Modifier le rôle d'un utilisateur |

> La suppression de compte est bloquée si l'utilisateur a des emprunts ou réservations en cours.

---

### Livres — `/api/books`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/books` | GET | Public | Lister tous les livres (paginé) |
| `/api/books/{id}` | GET | Public | Détail d'un livre |
| `/api/books/search` | GET | Public | Recherche (titre, isbn, date, dispo, catégorie, auteur) |
| `/api/books` | POST | LIBRARIAN / ADMIN | Ajouter un livre |
| `/api/books` | PUT | LIBRARIAN / ADMIN | Modifier un livre |
| `/api/books/{id}` | DELETE | ADMIN | Supprimer un livre |

> La suppression est bloquée si le livre est actuellement emprunté.

---

### Emprunts — `/api/loans`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/loans` | POST | Authentifié | Emprunter un livre |
| `/api/loans/{id}/return` | PUT | Propriétaire / LIBRARIAN / ADMIN | Rendre un livre |
| `/api/loans/my` | GET | Authentifié | Mes emprunts (actifs + historique) |
| `/api/loans` | GET | LIBRARIAN / ADMIN | Tous les emprunts |
| `/api/loans/stats` | GET | LIBRARIAN / ADMIN | Statistiques du tableau de bord |
| `/api/loans/top10` | GET | LIBRARIAN / ADMIN | Top 10 livres les plus empruntés |
| `/api/loans/overdue` | GET | LIBRARIAN / ADMIN | Emprunts en retard |

**Règles métier :**
- Maximum **3 emprunts actifs** par utilisateur
- Durée par défaut : **14 jours**
- Impossible d'emprunter si un emprunt précédent est en retard
- Au retour d'un livre, le **premier utilisateur en file d'attente** est notifié automatiquement

---

### Réservations — `/api/reservations`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/reservations` | POST | Authentifié | Réserver un livre (indisponible uniquement) |
| `/api/reservations/my` | GET | Authentifié | Mes réservations avec rang dans la file |
| `/api/reservations/{id}` | DELETE | Propriétaire / ADMIN | Annuler une réservation |

**Règles métier :**
- Maximum **5 réservations actives** par utilisateur
- On ne peut réserver qu'un livre **indisponible**
- La file est gérée par ordre chronologique (date de réservation)

---

### Notes — `/api/ratings`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/ratings/book/{bookId}` | GET | Public | Notes d'un livre |
| `/api/ratings/book/{bookId}` | POST | Authentifié | Ajouter une note |
| `/api/ratings/{id}` | PUT | Authentifié | Modifier sa note |
| `/api/ratings/{id}` | DELETE | LIBRARIAN / ADMIN | Supprimer une note |

> Une seule note par utilisateur par livre.

---

### Auteurs — `/api/authors`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/authors` | GET | Public | Lister tous les auteurs |
| `/api/authors/{id}` | GET | Public | Détail d'un auteur |
| `/api/authors` | POST | LIBRARIAN / ADMIN | Créer un auteur |
| `/api/authors/{id}` | PUT | LIBRARIAN / ADMIN | Modifier un auteur |
| `/api/authors/{id}` | DELETE | ADMIN | Supprimer un auteur |

---

### Catégories — `/api/categories`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/categories` | GET | Public | Lister toutes les catégories |
| `/api/categories/{id}` | GET | Public | Détail d'une catégorie |
| `/api/categories` | POST | LIBRARIAN / ADMIN | Créer une catégorie |
| `/api/categories/{id}` | PUT | LIBRARIAN / ADMIN | Modifier une catégorie |
| `/api/categories/{id}` | DELETE | ADMIN | Supprimer une catégorie |

---

### Notifications — `/api/notifications`

| Endpoint | Méthode | Rôle requis | Description |
| --- | --- | --- | --- |
| `/api/notifications` | GET | Authentifié | Mes notifications (triées par date, plus récentes en premier) |
| `/api/notifications/read` | PUT | Authentifié | Marquer toutes les notifications comme lues |

**Fonctionnement :** quand un livre est rendu, le premier utilisateur en file d'attente reçoit automatiquement une notification indiquant que le livre est disponible à l'emprunt.

Exemple de réponse :
```json
[
    {
        "id": 1,
        "message": "Le livre \"Harry Potter\" est maintenant disponible à l'emprunt !",
        "isRead": false,
        "createdAt": "2026-04-15T10:30:00"
    }
]
```

---

## Modèle de données

| Entité | Description |
| --- | --- |
| `User` | Compte utilisateur (email unique, rôle, mot de passe BCrypt) |
| `Book` | Livre (titre, ISBN, auteurs, catégories, disponibilité) |
| `Author` | Auteur (prénom, nom — unique par combinaison) |
| `Category` | Catégorie de livre |
| `Loan` | Emprunt (dates, statut retourné) |
| `Reservation` | Réservation en file d'attente |
| `Rating` | Note d'un utilisateur sur un livre (score + commentaire) |
| `Notification` | Notification in-app pour l'utilisateur |

---

## Structure du projet

```
src/main/java/fr/eni/bookhubbacked/
├── config/           # SecurityConfig, OpenApiConfig
├── controller/       # Contrôleurs REST
├── entity/
│   ├── bo/           # Entités JPA
│   ├── bo/dto/       # DTOs de réponse/requête
│   ├── dto/          # DTOs spécifiques (BookDto, Search...)
│   └── enums/        # RoleEnum
├── exception/        # GlobalExceptionHandler
├── mapper/           # Interfaces MapStruct
├── repository/       # Repositories Spring Data JPA
├── security/         # JwtService, JwtAuthenticationFilter
└── service/          # Logique métier
```

---

## Frontend

Le frontend (Angular) tourne sur `http://localhost:4200`. Le CORS est configuré pour autoriser cette origine.

## Rapport technique

### Problème rencontré
Lors de la récupération des réservations d’un utilisateur, le rang dans la file d’attente était toujours égal à `1`, malgré l’utilisation de la fonction SQL `ROW_NUMBER()`.

### Analyse
La requête utilisait un filtre `WHERE user_id = :userId` avant le calcul du rang.

En SQL, le filtrage est appliqué avant les fonctions de fenêtrage.  
Ainsi, le `ROW_NUMBER()` était calculé uniquement sur les réservations de l’utilisateur, et non sur l’ensemble des réservations d’un livre.

### Solution apportée
Le calcul du rang a été déplacé dans une sous-requête afin de prendre en compte toutes les réservations.

Le filtrage par utilisateur est ensuite appliqué après, permettant d’obtenir le rang réel dans la file d’attente.

### Conclusion
Ce problème a permis de mieux comprendre l’ordre d’exécution des requêtes SQL et l’impact du `WHERE` sur les fonctions comme `ROW_NUMBER()`.
