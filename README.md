<p align="center">
<img src="https://github.com/TrigoPI/ProjetJava/raw/main/Conception/Mockups/ChatGUI.png">
</p>

# Clavarchat 

## Mais qu'est ce donc? 🤨

**ClavarChat** est un chat 100% décentralisé qui fonctionne sur un réseau local et inclut les fonctionnalités suivantes :
- **Une phase de découverte** des utilisateur sur le réseau
- Possibilité de plusieurs phases de découverte simultanée sur le réseau (ne fonctionne pas à tous  les coups)
- Utilisation d'une **base de données décentralisée**
- Possibilité de **changer son pseudo** et son image de profil à tout moment
- Garder un **historique des conversations** (limité à seulement les 10 derniers  messages pour des raisons de performance)
- Affiche en **temps réel** si un utilisateur  est connecté
- Affiche si un utilisateur est en train **d'écrire dans le chat**

## utilisation ⌨️

Le projet et compilable avec Maven en utilisant **Java 11** et le **.jar** et disponible dans [Implementation/build](https://github.com/TrigoPI/ProjetJava/tree/main/Implementation/build) (executable à partir de Java 11)

Avant de lancer le projet il faut **changer l'ID** utilisateur, qui se trouve dans le fichier [Implementation/src/main/resources/ClavarChat/Resources/CONFIG/Conf.json](https://github.com/TrigoPI/ProjetJava/blob/main/Implementation/src/main/resources/ClavarChat/Resources/CONFIG/Conf.json) **(🚨 L'ID doit être unique pour chaque utilisateur)**

Pour lancer le projet avec **Maven** : 

```sh
#lancer le projet 
mvn javafx:run
```

Pour compiler le projet en **.jar**

```sh
#compiler le projet 
mvn assembly:assembly
```
Le .jar se trouve dans **target** sous le nom de **ClavarChat-1.0-SNAPSHOT-jar-with-dependencies.jar** il faut par la suite copier le dossier [Implementation/src/main/resources/ClavarChat](https://github.com/TrigoPI/ProjetJava/tree/main/Implementation/src/main/resources/ClavarChat/Resources) dans le **dossier ou se trouve le .jar**.

### Améliorations possibles 🔧

Plusieurs améliorations sont possibles : 
- Récupurer au fur et à mesure l'historique des messages dans une conversation pour eviter les problèmes de performance
- Finaliser la page de modification de mot de passe
- Pouvoir détecter si un utilisateur ne répond pas au broadcast de découverte
- Création automatique de la base de données et du ficher de configuration

### Bugs possibles 🪳

- L'image de profile ne s'affiche pas :
	- Changer l'image de profil devrait régler le problème
- Les messages ne s'envoient plus (problème lié aux sockets) :
	- Le [SocketWatchdog](https://github.com/TrigoPI/ProjetJava/blob/main/Implementation/src/main/java/ClavarChat/Controllers/Runnables/Network/SocketWatchdog/SocketWatchdog.java) se charge de fermer la socket au bout de 10 en cas de problème
- Les utilisateurs ne s'affichent pas (problème lié à la base de données): 
	- Si le projet est lancer depuis Maven, il suffit de clean le projet avec Maven ```mvn clean:clean```
	- Si le projet est lancer depuis le .jar, il faut recopier le dossier [Implementation/src/main/resources/ClavarChat](https://github.com/TrigoPI/ProjetJava/tree/main/Implementation/src/main/resources/ClavarChat/Resources) ou se trouve le .jar
