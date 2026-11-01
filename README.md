# Description d'Image

Application web d'audiodescription pour personnes aveugles ou malvoyantes. Sélectionnez une image, posez une question optionnelle, obtenez une réponse en français. Le parcours complet (sélection, description, **synthèse vocale**) fonctionne sur un poste où aucun modèle n'est installé.

## Prérequis

- **JDK 17 ou supérieur**
- Maven Wrapper (fourni)
- Navigateur récent

## Lancement

Cloner ou télécharger le projet, puis se placer à la racine (dossier contenant `pom.xml`).

### Avec la fausse implémentation (sans modèle)

**Fonctionne de bout en bout (page + synthèse vocale) sans modèle installé.**

Par défaut (sans profil) :

```sh
./mvnw spring-boot:run
```

Ou avec profil explicite :

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=faux
```

Ouvrir [http://localhost:8080](http://localhost:8080).

### Avec Ollama (analyse réelle)

1. Installer [Ollama](https://ollama.ai) et démarrer le serveur :
   ```sh
   ollama serve
   ```
2. Télécharger le modèle :
   ```sh
   ollama pull gemma4:e2b
   ```
3. Lancer l'application :
   ```sh
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=ollama
   ```
   Ou via propriété :
   ```sh
   ./mvnw spring-boot:run -Dspring.profiles.active=ollama
   ```

## Modèle attendu

- **Modèle** : `gemma4:e2b` (modèle de vision multilingue)
- **Serveur** : Ollama local sur `http://localhost:11434`
- **Serveur** : Ollama local sur `http://localhost:11434`
