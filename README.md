## Table des matières

- [À propos](#à-propos)
- [Installation](#installation)
  - [Dépendances requises](#dépendances-requises)
- [Commandes](#commandes)
- [Crédits](#crédits)
- [Aide](#aide)
  - [Questions fréquentes](#questions-fréquentes)
- [Indications pour les développeurs](#indications-pour-les-développeurs)
  - [Modification de compatibilité](#modification-de-compatibilité)
  - [Ajouter des rôles](#ajouter-des-rôles)
  - [Publier un rôle](#publier-un-rôle)
 	 - [Quelques classes utiles](#quelques-classes-utiles)

## License

Utilisation du code autorisé, les modifications et utilisations du code doivent être publique.
Ce projet ne peut pas être utilisé de manière commerciale (Serveur minecraft monétisé) sans avoir obtenu l'autorisation préalable.

## A propos

Le mode Loup-Garou est un mode inspiré du jeu de société [Les Loups-Garous de Thiercelieux](https://fr.wikipedia.org/wiki/Les_Loups-garous_de_Thiercelieux) reprenant son fonctionnement ainsi que sa manière d'être joué, à la seule différence qu'aucun maître du jeu n'est requis, le déroulement de chaque partie étant entièrement automatisé.

**A noter :**

- Il existe des nouveaux rôles
- Utilisable sur n'importe quelle map sous condition d'ajouter les positions grâces aux commandes

## Installation

### Dépendances requises

- [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

## Commandes

`/lg menu` : Ouvre le menu de gestion des rôles
`/lg roles` : Retourne la liste des rôles dans la partie  
`/lg roles set <ID> <MONTANT>` : Définit le nombre de joueurs pour un certain rôle  
`/lg addSpawn` : Ajoute un emplacement de joueur
`/lg removeSpawn` : Supprime un emplacement de joueur
`/lg clearAllSpawn` : Supprime tout les emplacements de joueur
`/lg showSpawns` : Affiche tout les emplacements de joueur
`/lg start <PSEUDO>` : Lance la partie  
`/lg end <PSEUDO>` : Arrête une partie  
`/lg spec <PSEUDO>` : Ajoute un spectateur à la partie
`/lg reloadConfig` : Recharge la configuration   
`/lg hideVote` : Cache les messages de votes durant la partie
`/lg hideVoteExtra` : Cache les nombres de votes durant la partie
`/lg hideRole` : Cache la composition des rôles de la partie dans le scoreboard

## Crédits

- Base sur le projet de Shytoos_ et Leomelki
- Mapping : [Cosii](https://www.youtube.com/channel/UCwyOcA41QSk590fl9L0ys8A)

## Aide

Merci de créer des [issues](https://github.com/Ekinoxx0/LoupGarou/issues) en cas de bug.

### Questions fréquentes

- Que faire en cas de problème d'affichage (votes bloqués au dessus des têtes, etc...) ?  

Cela arrive après avoir `reload` au cours d'une partie, tous les joueurs qui ont ce problème doivent se déconnecter et se reconnecter.

- J'ai mal placés mes spawns ou je veux utiliser une nouvelle map, comment faire ?  

Il suffit d'utiliser la command `/lg removespawn` ou `/lg clearspawns`

- Puis-je mettre plusieurs fois le même rôle dans une seule partie ?

Cela est possible pour les rôles `Loup-Garou`, `Villageois` et `Chasseur`.
D'autres rôles peuvent aussi marcher mais n'ont pas été testés avec plusieurs joueurs ayant ce rôle dans une seule partie.
Merci de signaler si les rôles ne fonctionne pas avec plus d'un joueur.

## Indications pour les développeurs

Vous devez utiliser `Lombok` et `Maven` pour modifier ce projet.

### Modification de compatibilité

La version de Leomelki n'est pas strictement compatible avec cette version suite aux modifications suivantes aux events :

 - `LGNightStart` -> `LGDayEndEvent`
 - `LGDayEndEvent` -> `LGNightStartEvent`
 - `LGVoteEvent` -> `LGPeopleVoteStartEvent`
 - `LGMayorVoteEvent` -> `LGMayorVoteStartEvent`

### Ajouter des rôles

Ce plugin de Loup-Garou est organisé autour d'un système d'évènements, disponibles dans le package `dev.loupgarou.events`.  
Pour vous aider à créer des rôles, copiez des rôles ayant déjà été créés pour ainsi les modifier.

⚠️ Ce projet a été créé de façon à ce que les rôles soient - presque - totalement indépendants du reste du code (LGGame, LGPlayer...).  
Merci de garder ça en tête lors du développement de nouveaux rôles : utilisez un maximum les évènements et, s'il en manque, créez les !

#### Quelques classes utiles
`LGGame` : Contient le coeur du jeu, à modifier le minimum possible !  
`LGPlayer` : Classe utilisée pour interragir avec les joueurs et stocker leurs données, à modifier le minimum possible !  
`LGVote` : Système gérant les votes  
`RoleSort`: Classement de l'apparition des rôles durant la nuit.  
