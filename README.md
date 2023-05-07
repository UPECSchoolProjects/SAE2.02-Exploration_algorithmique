# SAE2.02-Exploration_algorithmique
 
![Tests Github](https://github.com/UUUUUwUUUUU/SAE2.02-Exploration_algorithmique/actions/workflows/JAVA_CI.yaml/badge.svg)
![Coverage](./.github/badges/jacoco.svg)

## 1. Préface
Objectif : Dans quelle mesure les textes d’un même auteur, ou d’une même époque, obéissent-ils à un
déterminisme ?
La problématique est, ici, d’analyser plusieurs textes du même auteur, ou de la même décennie, et de
montrer les points communs et les différences dans le vocabulaire utilisé (qui s’expliquent peut-être
en fonction du genre, de l’auteur, du sujet, etc.)
Il est conseillé de choisir 3 ou 4 textes longs (romans, etc.) ou une cinquantaine de textes courts
(poèmes, articles de presse, par exemple).

À cette fin, il faudra donc représenter :
- Un nuage de mots par texte analysé ;
- Un nuage de mots commun, permettant de visualiser les mots communs aux textes.

## 2. Informations

Pour ce projet nous avons décidé d'utiliser le language `java`. Nous utilisons `maven` pour la gestion des dépendances (`pdfbox`, `log4j` et `ApacheCLI`) et de la compilation.

Si jamais vous voyez plein d'erreurs en ouvrant le projet. Cela signifie que votre maven (ou votre IDE) est mal configuré.

Pour la gestion de version, nous avons utilisé `Git` avec `Github`. A chaque push, Github test le code pour s'assurer qu'aucune erreur n'a été introduite dans le code. Si les test ne se sont pas dérouler comme prévu une croix rouge s'affichera à coté du commit.

## 3. Déploiement 
Utiliser la commande `mvn install` dans le dossier `sae202`. Un fichier .jar sera généré dans le dossier `sae202/target`.

`mvn javadoc:javadoc` permet de mettre à jour la documentation.

Pour l'exécution : `java -jar sae202-1.0-SNAPSHOT.jar`, puis suivre l'aide du CLI.

Pour l'exécution du serveur node.js : `node index.js` dans le dossier `Serveur`

## 4. Dossiers
- le dossier `HTML` contient tout le code du site internet (https://sae202.nwo.ovh/)
- le dossier `javadocs` contient la documentation de l'application.
- le dossier `sae202` contient le code source du convertisseurs, de l'analyse et de l'algorithme de racinisation écrit par Maxime Lotto. Il contient aussi un dossier `test` avec tout les tests unitaire de l'application.
- Le dossier `Serveur` contient un serveur node.js avec la bibliothèque `express` qui permet de faire le lien entre les textes analysés et le site.

