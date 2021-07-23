# Ecran liste 

- [X] Le bouton "Nouvelle commune" dirige l'utilisateur vers l'écran de création de commune
- [X] Le tableau présente les résultats paginés de toutes les communes ou celles correspondant à la recherche effectuée
- [X] Il est possible de trier par code insee, code postal, nom, latitude et longitude, de manière ascendante, descendante. Ajuster l'affichage de chacune des colonnes en fonction des critères de tri
- [X] Chaque ligne commune possède un lien Détail permettant de consulter les détails d'une commune
- [X] Il est possible de sélectionner parmi une liste (5, 10, 20, 50, 100) la taille des pages. Lorsque l'on sélectionne une taille, la page est rechargée. Il est important que la valeur sélectionnée dans la liste déroulante soit celle spécifiée dans l'url (attribut selected=true dans l'option)
- [X] Même travail avec la sélection de la page (utiliser #numbers.sequence pour générer la liste côté Thymeleaf)
- [X] Les boutons précédent et suivant doivent être opérationnels et conserver les critères de tri et de recherche. Précédent est désactivé en première page. Suivant est désactivé en dernière page.
- [X] On affiche la position des résultats "Affichage des communes X à Y sur un total de Z". Attention au total qui est soit égal au nombre total de communes un base, ou égal ou nombre de résultats de la recherche
- [X] Bonus : Ajouter un bouton Supprimer à côté du bouton Détail sur chaque ligne

# Ecran détail
- [X] Le titre est soit "Détail de la communes CODEINSEE, NOM" si c'est une modification ou "Création d'une nouvelle commune" si c'est une création
- [X] On affiche dans les champs du formulaires les valeurs de la commune éditée (vide si création)
- [X] On affiche la carte avec un marqueur à la latitude/longitude de la commune. Il faut spécifier également la "boite" (LATITUDE et LONGITUDE à remplacer avec les valeurs de la commune) : https://www.openstreetmap.org/export/embed.html?bbox=${LONGITUDE-0.10}%2C${LATITUDE-0.5}%2C${LONGITUDE+0.10}%2C${LATITUDE+0.5}&amp;layer=mapnik&amp;marker=${LATITUDE}%2C${LONGITUDE}.
- [X] La carte n'est pas visible lors d'une création
- [X] On affiche les communes dans un périmètre de X km autour de la commune. On peut modifier le périmètre dans l'input, cliquer sur Rechercher pour afficher de nouveaux les détails de la commune avec le périmètre modifié
- [X] La section "périmètre" n'est pas visible lors d'une création
- [X] Le bouton Enregistrer fait un POST sur /communes dans le cas d'une création, sur /communes/CODEINSEE dans le cas d'une modification
- [X] Le bouton Supprimer fait un GET sur /communes/CODEINSEE/delete est n'est visible qu'en mode modification.
- [X] Faire en sorte que lors de la modification ou de la création d'une commune, on soit redirigé vers la page de détail de la communes (pour éviter le problème de F5)
- [X] Faire en sorte que lors de la suppression d'une commune, on soit redirigé vers la liste des communes
- [X] Bonus : Si dans la barre de recherche, on renseigne un Code INSEE (5 chiffres), on redirige l'utilisateur directement vers la page de détail de la commune

# Fragments
- [X] Créer un fragment pour la balise `head` et pour la `navbar`. Appeler ces fragments dans les différentes pages pour supprimer la duplication.
- [X] Créer un fragment pour les colonnes de la liste
- [ ] Bonus : Créer un fragment pour l'architecture de la page
- [X] Bonus : Créer un fragment pour les champs de formulaire
- [X] Bonus : Créer un fragment pour les éléments de pagination
- [ ] Bonus : Créer un fragment pour le tableau

# Gestion des erreurs
- [X] Créer un template `error.html` permettant d'afficher de manière générique les erreurs de votre application
- [X] Créer le `GlobalExceptionHandler` permettant la gestion globale des exceptions levées dans vos contrôleurs
- [X] Pour le GET /communes/CODEINSEE, lever une `EntityNotFoundException` lorsque le code INSEE n'existe pas en base et la gérer dans votre `GlobalExceptionHandler` avec une 404
- [X] Idem pour le GET /communes/CODEINSEE/delete
- [ ] Dans l'affichage de la liste des communes, contrôler la valeur des paramètres `page`, `size`, `sortProperty` et `sortDirection`, lever une `IllegalArgumentException` lorqu'un des paramètres est incorrect et la gérer dans votre `GlobalExceptionHandler` avec une 400
- [ ] Bonus : Gérer d'autres erreurs à travers votre application

# Flash attributes
- [X] Afficher un message de succès lors de la sauvegarde d'une commune
- [X] Afficher un message de succès suite à la suppression d'une commune
- [X] Afficher un message de succès suite à la création d'une commune
- [X] Afficher un message d'erreur si on souhaite afficher les villes dans un périmètre supérieur à 20 km (cela n'utilise pas forcément les flash attributes)

# Validation
- [X] Mettre en place la validation avec les règles adéquates sur les champs code Insee (obligatoire, 5 chiffres et le deuxième caractère peut être A ou B), code postal (obligatoire, 5 chiffres), nom (obligatoire, taille max 50, regex `^[A-Za-z-' ]+[0-9]{0,2}$`, latitude et longitude (facultatives, mais lorsqu'elles sont renseignées, elles doivent être valides)
- [X] Faire en sorte que les fomulaires affichent les messages d'erreurs pour chaque champs en cas d'erreur de validation (classe CSS is-valid ou is-invalid, + div de la classe invalid-feedback pour l'affichage des erreurs)
- [ ] Bonus : Gérer correctement le formulaire de création (pas de coche verte ou de warning rouge à la création mais en cas d'erreur de création on réaffiche bien le formulaire avec les warnings pour les champs à problèmes).

# Spring Security
- [X] Créer une entité User : 
  - id (clé primaire)
  - userName 
  - email
  - password
  - name
  - lastName
  - active (booléen)
  - Constructeurs, toString.... + règles de validation auxquelles vous pensez (sauf pour le mot de passe)
- [X] Créer une entité Role : 
  - id (clé primaire)
  - role (String)
- [X] Mettre en place un ManyToMany entre User et Role
- [X] Mettre en place le paramétrage de sécurité avec authentification par formulaire
- [X] Mettre en place la page de connexion
- [X] Restreindre l'accès aux pages suivantes : 
  - Liste des communes et recherche accessible à tous
  - Détails des communes accessible uniquement aux utilisateurs connectés
  - Création, Modification et suppression d'une commune accessible uniquement aux utilisateurs ADMIN
- [X] Affiner l'affichage au niveau du template pour : 
  - Ne pas afficher le bouton Créer une nouvelle commune pour les utilisateurs non admin
  - Ne pas afficher le bouton Supprimer et le bouton Enregistrer la commune pour les utilisateurs non admin 
  - Afficher un lien Se connecter dans le menu lorsqu'on n'est pas connecté, un bouton Se déconnecter lorsqu'on est connecté
- [X] Gérer les messages de connexion réussie et de connexion échouée