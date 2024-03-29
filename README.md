# <u>L3 Miage - SPRING - TP2 - EXO 1</u>

* Pour pouvoir démarrer tous les TPs simplement, assurez-vous d'avoir tous les prérequis :
    * voir les [prérequis](prerequis.md)

# Objectif de l'exercice :

Dans cet exercice, vous allez apprendre à :

* Tester votre serveur.

# Le modèle applicatif

* Dans cet exercice, nous allons être tout le long dans le paradigme `create`
* Nous allons reprendre la situation du TP3 de JPA.
  ![situation exo 1](doc/img_2.png)
* Le code est déjà existant.



# Les tests unitaires

1. À partir de maintenant, nous travaillons dans le directory `test` du module `server`
2. Dans le dossier `server/src/test/`, créer le dossier `ressources` (intellij vous le proposera directement), et faire un copié-collé du fichier `application.yml`. Cela permet d'avoir une configuration de spring dans les contextes de tests.
3. Dans ce nouveau `application.yml`, ajouter la propriété `spring.jpa.show-sql : true` afin de pouvoir voir toutes les requêtes faites par JPA (voir la magie de la création des tables, ou les requêtes faites par les repositories).
    ```yml
    # sur quel port le serveur démarre
    server:
      port: 8080
    
    # Les informations de connexion de la bd
    spring:
      datasource:
        username: postgres
        password: postgres
        url: jdbc:postgresql://localhost:5432/
        driver-class-name: "org.postgresql.Driver"
      jpa:
        hibernate:
          ddl-auto: create-drop
          # Permet de voir le sql dans les logs du serveur
        show-sql: true
        ```
3. Créer un package <b style="color:red">identique</b> à celui de la classe qui correspond à l'application (dans notre cas `Tp1Exo1SpringApplication.java`) afin que les tests puissent scanner directement l'executable pour lancer les tests. Ici, vous devez créer le package `fr.uga.l3miage.spring.tp2.exo1`

## Tester les repositories

* Lors des TPa de JPA, nous avons créé des repositories, qu'il n'est pas particulièrement utile de tester, car les fonctions de base proposées l'ont déjà été. Cependant, lorsque nous créons des requêtes personalisées, il est important de les tester !
* Dans cet exemple, nous allons tester la requête `findAllByMailContaining` contenue dans le repository `UserRepository`

1. Créer le package `fr.uga.l3miage.spring.tp2.exo1.repositories`
2. La convention de nommage des tests en java est le nom de la classe que l'on teste, avec le suffixe `Test`. Nous allons donc créer la class `UserRepositoryTest`
    ```java
    class UserRepositoryTest {}
    ```
3. Ici, nous voulons tester avec un springBoot de test, pour cela, nous allons utiliser l'annotation `@SpringBootTest()` afin de nous permettre de faire de l'injection par exemple.
    1. Cette annotation a plusieurs paramètres importants. Le premier est `webEnvironment`, qui correspond à la manière de démarrer SpringBoot. Il y a 4 options possibles :

       | `webEnvironment` | type de démarrage                                                                                           |
               |-------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
       | MOCK             | l'environnement web est simulé, il ne peut donc pas recevoir d'appel extérieur                              |
       | RANDOM_PORT      | l'env est sur port random et lance réellement le serveur, donc on peux faire appel à lui depuis l'extérieur |
       | DEFINED_PORT     | idem que `RANDOM_PORT`, sauf que l'on peut définir le port                                                  |
       | NONE             | aucun environment web n'est utile                                                                            |
    2. Dans notre cas nous ne voulons pas faire d'appel depuis l'extérieur, mais nous avons besoin de l'injection de dépendance pour le repository, donc nous allons utiliser `MOCK`
    3. Cette annotation nous permet de récrire des paramètres présents dans le fichier `application.yml` pour le context de test. Dans notre cas, nous ne voulons pas nous embêter à avoir une base de données `postgres` sur un docker, surtout pour les tests. Nous allons donc utiliser les bases de données H2, qui est une base de données embarquée directement par un système d'écriture dans un fichier. Dans notre cas, nous allons changer la propriété `spring.jpa.database-platform` par `org.hibernate.dialect.H2Dialect`
    4. Avec ces 2 informations, nous avons ceci
         ```java
         @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
         class UserRepositoryTest {}
         ```
4. De plus, puisque nous utilisons une base données particulière, nous devons ajouter quelques configurations. SpringBoot met à notre disposition une annotation qui nous permet de configurer automatiquement la base, en utilisant l'annotation `@AutoConfigureTestDatabase`
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
    class UserRepositoryTest {}
    ```
5. Pour nos tests nous allons avoir besoin du repository, il nous faut donc l'injecter. Les tests n'ayant pas de constructeurs, nous allons faire de l'injection par attribut via l'annotation `@Autowired`
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
    class UserRepositoryTest {
        @Autowired
        private UserRepository userRepository;
    }
    ```
6. Ensuite, nous pouvons créer notre test `testRequestFindAllByMailContaining`  en utilisant l'annotation `@Test`
    ```java
    @Test
    void testRequestFindAllByMailContaining(){}
    ```
7. Nous allons appliquer le `Given - When - Then`
   ### Given
    1. Nous allons créer 2 utilisateurs différents, 1 avec un `@gmail` dans son adresse mail, et l'autre non, puis nous allons les `save`
         ```java
         @Test
         void testRequestFindAllByMailContaining(){
             //given
             UserEntity userEntity = UserEntity
                     .builder()
                     .mail("test@gmail.com")
                     .name("google 1")
                     .build();
     
             UserEntity userEntity2 = UserEntity
                     .builder()
                     .mail("test@test.com")
                     .name("non google")
                     .build();
     
             userRepository.save(userEntity);
             userRepository.save(userEntity2);
         }
         ```
   ### When
    1. Ensuite, nous allons exécuter la requête
        ```java
        @Test
        void testRequestFindAllByMailContaining(){
            //given
            UserEntity userEntity = UserEntity
                    .builder()
                    .mail("test@gmail.com")
                    .name("google 1")
                    .build();
    
            UserEntity userEntity2 = UserEntity
                    .builder()
                    .mail("test@test.com")
                    .name("non google")
                    .build();
    
            userRepository.save(userEntity);
            userRepository.save(userEntity2);
       
            // when
            Set<UserEntity> userEntitiesResponses = userRepository.findAllByMailContaining("gmail");
        }
        ```

   ### Then
    1. Enfin nous allons vérifier les résultats (utilisation des asserts) : dans notre cas, nous devons avoir un set de taille 1, et l'entité qui est dans cette recherche doit avoir comme email `test@gmail.com`
        ```java
        @Test
       void testRequestFindAllByMailContaining(){
       //given
       UserEntity userEntity = UserEntity
       .builder()
       .mail("test@gmail.com")
       .name("google 1")
       .build();
    
            UserEntity userEntity2 = UserEntity
                    .builder()
                    .mail("test@test.com")
                    .name("non google")
                    .build();
    
            userRepository.save(userEntity);
            userRepository.save(userEntity2);
    
            //when
    
            Set<UserEntity> userEntitiesResponses = userRepository.findAllByMailContaining("gmail");
    
            //then
            assertThat(userEntitiesResponses).hasSize(1);
            assertThat(userEntitiesResponses.stream().findFirst().get().getMail()).isEqualTo("test@gmail.com");
       }
        ```
8. Enfin nous pouvons lancer le test avec le bouton qui se trouve à côté du nom de la fonction.
9. Imaginons que le test ne passe pas, vous pourriez aller voir dans les logs du test et voir le sql qui s'est exécuté.

* Bravo, nous avons testé un cas sur une requête, maintenant, il faudra tester tous les cas possibles !

![nice](https://tenor.com/fr/view/elmer-sheep-thumbs-up-like-approved-gif-7569635.gif)

### A votre tour

* Tester le repository `ArtistRpository`

## Tester les components

* Pour les components, nous pouvons tester plusieurs choses
    * Savoir s'il fait tous les appels lors d'une fonction
    * Savoir s'il remonte bien des exceptions

* En résumé s'il a le comportement attendu

* Prenons comme exemple la fonction `getPlaylist` dans le component `PlaylistComponent`.
    * On veut tester que si la base de données ne renvoie rien, alors une exception est levée
    * On veut tester le comportement normal de la fonction

1. Créer le package `fr.uga.l3miage.spring.tp2.exo1.component`
2. Créer la classe `playlistComponentTest`
3. Ajouter l'annotation `@SpringBootTest()` en mode `Mock`, et rien de plus, vous verrez par la suite pourquoi
4. Ajouter l'annotation `@AutoConfigureTestDatabase`
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    class PlayListComponentTest {}
    ```
5. Ensuite déclarons les objets utiles à nos tests, ici le components, car c'est lui que nous testons.
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    class PlayListComponentTest {
        @Autowired
        private PlaylistComponent playlistComponent;
    }
    ```
6. Dans les tests, nous essayons d'isoler au plus possible les différentes briques utilisées. Ici nous allons donc nous isoler de la base de données en simulant le comportement du repository via un `mock`. Nous allons donc déclarer un `PlaylistRepository` avec l'annotation `@MockBean`(et non pas `@Mock`, car nous récupérons déjà le repository par injection de dépendance). Voilà pourquoi nous ne changeons pas le dialect de la base de données dans l'annotation `@SpringBootTest`, car nous nous isolons de la base. Cependant nous sommes obligés d'utiliser l'annotation `@AutoConfigureTestDatabase`, car le repository utilise certaines configurations pour se créer.
   ```java
   @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    class PlayListComponentTest {
        @Autowired
        private PlaylistComponent playlistComponent;
        @MockBean
        private PlaylistRepository playlistRepository;
    }
   ```
7. Ensuite créons notre test `getPlaylistNotFound`
    ```java
    @Test
    void getPlaylistNotFound(){}
    ```
8. Nous allons appliquer le `Given - When - Then`
   ### Given
    1. Dans notre cas, le given est de donner le comportement du repository lors de son appel, puisque c'est un mock (une coquille vide, qui ne sait rien faire sans instructions de notre part)
    2. On va ensuite utiliser la fonction `when` qui va nous permettre de spécifier à quel moment le mock renverra quelque chose. Dans notre cas, c'est lorsque l'on appellera la fonction `findById()` avec un string quelconque, et cet appel devra renvoyer une liste vide.
          ```java
           @Test
           void getPlaylistNotFound(){
              //Given
              when(playlistRepository.findById(anyString())).thenReturn(Optional.empty());
          }
          ```
    3. Ainsi, avons défini le comportement suivant : lorsque `playlistRepository.findById` est appelé avec n'importe quelle chaine de caractères, alors il nous renvoie un set vide

   ### When - Then
    1.  Dans notre cas, les 2 étapes vont se faire en même temps, car nous voulons voir si une exception remonte et nous allons utiliser la fonction `assertThrows`
        ```java
        @Test
        void getPlaylistNotFound(){
            //Given
            when(playlistRepository.findById(anyString())).thenReturn(Optional.empty());

            //then - when
            assertThrows(NotFoundPlaylistEntityException.class,()->playlistComponent.getPlaylist("test"));
        }
        ```
9. Ensuite, on peut tester le fait que la fonction ne remonte pas d'erreur (un comportement normal) de cette manière :
    ```java
   @Test
    void getPlaylistFound(){
        //Given
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .songEntities(Set.of())
                .description("test")
                .build();
        when(playlistRepository.findById(anyString())).thenReturn(Optional.of(playlistEntity));

        // when - then
        assertDoesNotThrow(()->playlistComponent.getPlaylist("test"));
    }
    ```

* Pas mal, vous avez testé votre premier composant

![pas_mal](https://tenor.com/fr/view/not-bad-ryan-reynolds-gif-23839048.gif)

### A votre tour

* Tester les components `SongComponent`


## Tester les services

* C'est un peu comme les components, cependant un rang plus haut.
* Prenons comme exemple le service `PlaylistService`

1. Créer le package `fr.uga.l3miage.spring.tp2.exo1.services`
2. Créer le fichier `PlaylistServiceTest`
3. Partons du même principe pour l'isolation. Déclarer l'annotation `@SpringBootTest` en mode `MOCK` , et l'annotation `@AutoConfigureTestDatabase`
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    class PlaylistServiceTest {}
    ```
4. Déclarer le service `PlaylistService`
    ```java
    @AutoConfigureTestDatabase
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
    class PlaylistServiceTest {
        @Autowired
        private PlaylistService playlistService;
    }
    ```
5. Les services ont souvent beaucoup de dépendances. Puisque nous voulons isoler le service testé de ses dépendants, ces dernières doivent toutes devenir des `mock` ou encore des `spy`. Ici, nous avons besoin de `PlaylistComponent`, `SongComponent`, `PlaylistMapper`
6. Déclarons donc les attributs `PlaylistComponent`et `SongComponent` en `@MockBean` afin de définir leur comportement
    ```java
        @AutoConfigureTestDatabase
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
        class PlaylistServiceTest {
        @Autowired
        private PlaylistService playlistService;

        @MockBean
        private PlaylistComponent playlistComponent;

        @MockBean
        private SongComponent songComponent;
    }
    ```
7. Ensuite pour le mapper, nous allons en faire un `Spy` (le comportement de base n'est pas alteré tant qu'il n'est pas redéfini, et cela nous permet de suivre le nombre d'appels fait sur le spy) avec l'annotation `@SpyBean`
    ```java
        @AutoConfigureTestDatabase
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
        class PlaylistServiceTest {
        @Autowired
        private PlaylistService playlistService;

        @MockBean
        private PlaylistComponent playlistComponent;

        @MockBean
        private SongComponent songComponent;
   
        @SpyBean
        private PlaylistMapper playlistMapper;
    }
    ```
8. Créer le test `createPlaylist`
    ```java
    @Test
    void createPlaylist(){}
    ```
9. Nous allons appliquer le `Given - When - Then`
   ### Given
    1. Dans notre cas il nous faut créer une request `PlaylistCreationRequest`, transformer ce DTO en entité (voilà pourquoi on utilise un `@SpyBean` sur playlistMapper), simuler le comportement des `mock`, et créer la réponse attendue
       ```java
       @Test
       void createPlaylist(){
           //given
           PlaylistCreationRequest playlistCreationRequest = PlaylistCreationRequest
           .builder()
           .name("test")
           .description("Une description de test")
           .songsIds(Set.of())
           .build();
   
           PlaylistEntity playlistEntity = playlistMapper.toEntity(playlistCreationRequest);
           playlistEntity.setSongEntities(Set.of());
           when(songComponent.getSetSongEntity(same(Set.of()))).thenReturn(Set.of());
           when(playlistComponent.createPlaylistEntity(playlistEntity)).thenReturn(playlistEntity);

           PlaylistResponseDTO responseExpected = playlistMapper.toResponse(playlistEntity);
       }
       ```
   ### When
    1. Appeler le service avec la request du given
        ```java
            @Test
       void createPlaylist(){
         //given
         PlaylistCreationRequest playlistCreationRequest = PlaylistCreationRequest
         .builder()
         .name("test")
         .description("Une description de test")
         .songsIds(Set.of())
         .build();

         PlaylistEntity playlistEntity = playlistMapper.toEntity(playlistCreationRequest);
         playlistEntity.setSongEntities(Set.of());
         when(songComponent.getSetSongEntity(same(Set.of()))).thenReturn(Set.of());
         when(playlistComponent.createPlaylistEntity(playlistEntity)).thenReturn(playlistEntity);

         PlaylistResponseDTO responseExpected = playlistMapper.toResponse(playlistEntity);

         //when
         PlaylistResponseDTO response = playlistService.createPlaylist(playlistCreationRequest);
       }
        ```
   ### Then
    1. Comparer le resultat obtenu avec la reponse attendue, et regarder si sur les `mock` et `spy` le bon nombre d'appels à été fait via la fonction `verify` de mockito
        ```java
           @Test
           void createPlaylist(){
                //given
                PlaylistCreationRequest playlistCreationRequest = PlaylistCreationRequest
                .builder()
                .name("test")
                .description("Une description de test")
                .songsIds(Set.of())
                .build();

                PlaylistEntity playlistEntity = playlistMapper.toEntity(playlistCreationRequest);
                playlistEntity.setSongEntities(Set.of());
                when(songComponent.getSetSongEntity(same(Set.of()))).thenReturn(Set.of());
                when(playlistComponent.createPlaylistEntity(playlistEntity)).thenReturn(playlistEntity);

                PlaylistResponseDTO responseExpected = playlistMapper.toResponse(playlistEntity);

                //when
                PlaylistResponseDTO response = playlistService.createPlaylist(playlistCreationRequest);
          
                // then
                assertThat(response).usingRecursiveComparison().isEqualTo(responseExpected);
                verify(playlistMapper,times(2)).toEntity(playlistCreationRequest);
                verify(playlistMapper,times(2)).toResponse(same(playlistEntity));
                verify(playlistComponent,times(1)).createPlaylistEntity(any(PlaylistEntity.class));
                verify(songComponent,times(1)).getSetSongEntity(Set.of());
        }
       ```

* Nice, vous savez maintenant tester un service

![nice](https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExeTlycjc3OWw0ZDl5Mm9sMDFhOW1wM2V3djZrcDRzNXRvenI1ZmsydiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/yJFeycRK2DB4c/giphy.gif)


### A votre tour

* Terminer de tester `PlaylistService`


## Tester les controllers

* En test unitaire, il n'y a pas d'intérêt à tester les controller, car ils n'ont aucune logique et ne font qu'appeler un service.

# Les tests d'intégrations

* Les tests d'intégrations permettent de tester toutes les briques ensembles pour voir si tout fonctionne bien.

## Tester les controllers

* Dans le cadre des tests d'intégration, il est intéressant de tester les controllers car ils ont les points d'entrée de notre serveur.
* Il faut tester dans un controller tous <b style="color:red">les codes d'erreurs définis dans la documentation !</b>

1. Créer le package `fr.uga.l3miage.spring.tp2.exo1.Controllers`
2. Créer la classe `PlaylistControllerTest`
    ```java
    class PlaylistControllerTest {}
    ```
3. Ajouter les annotations `@SpringBootTest`, mais cette fois-ci dans le mode `RANDOM_PORT`, et la propriété pour la base H2, donc aussi `@AutoConfigureTestDatabase` (et oui, en intégration, nous devons tout brancher, et nous faisons des appels extérieurs ^^)
4. Ajouter également une nouvelle annotation `@AutoConfigureWebTestClient`, qui va nous permettre de configurer automatiquement un client (un code capable de parler à une API en java) pour les appels sur le serveur
    ```java
    @AutoConfigureTestDatabase
    @AutoConfigureWebTestClient
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
    class PlaylistControllerTest {}
    ```
5. Déclarer comme attribut une `TestRestTemplate`, qui va nous permettre de faire les échanges entre notre test et le serveur.
    ```java
    @AutoConfigureTestDatabase
    @AutoConfigureWebTestClient
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
    class PlaylistControllerTest {
        @Autowired
        private TestRestTemplate testRestTemplate;
    }
    ```
6. Mettre en `@SpyBean` les éléments que vous voulez suivre, dans notre cas, nous allons regarder les components `SongComponent` et `PlaylistComponent`
7. Nous allons aussi déclarer le repository `PlaylistRepository`, car lorsqu'une classe de test s'exécute, pour des raisons de performances, SpringBoot est démarré au premier test et éteint au dernier test. Cela signifie que si des données sont en base, elles ne sont pas supprimées entre 2 tests de la même classe. C'est pour cela qu'on va aussi définir une méthode qui va s'exécuter après chaque test d'une classe grâce à l'annotation `@AfterEach`
    ```java
    @AutoConfigureTestDatabase
    @AutoConfigureWebTestClient
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
    class PlaylistControllerTest {
        @Autowired
        private TestRestTemplate testRestTemplate;
    
        @Autowired
        private PlaylistRepository playlistRepository;
    
        @SpyBean
        private PlaylistComponent playlistComponent;
    
        @SpyBean
        private SongComponent songComponent;
    
        @AfterEach
        public void clear() {
            playlistRepository.deleteAll();
        }   
    }
    ```
8. Créer le test `canCreatePlaylistWithoutSong` :
    ```java
    @Test
    void canCreatePlaylistWithoutSong() {}
    ```
9. Nous allons appliquer le `Given - When - Then`
   ### Given
    1. Ici, nous allons construire notre requête afin de pouvoir faire l'appel au serveur.
    2. On va donc créer un `HttpHeaders` qui nous permet d'insérer les informations nécessaires dans le headers
    3. Ensuite, nous allons créer notre `PlaylistCreationRequest`
        ```java
        @Test
        void canCreatePlaylistWithoutSong() {
            //given
            final HttpHeaders headers = new HttpHeaders();
    
            final PlaylistCreationRequest request = PlaylistCreationRequest
                    .builder()
                    .name("Test playlist")
                    .description("une playlist de test")
                    .songsIds(Set.of())
                    .build();
        }
        ```
   ### When
    1. Nous allons appeler notre endpoint grâce à la fonction `exchange` de la `testRestTemplate` : cette fonction prend le lien relatif (pour nous ici `/api/playlist/create`), la méthode http utilisée, une `HttpEntity` qui prend le `body` et `headers` de la requête, et enfin le `.class` du type de retour.
        ```java
        @Test
        void canCreatePlaylistWithoutSong() {
            //given
            final HttpHeaders headers = new HttpHeaders();
    
            final PlaylistCreationRequest request = PlaylistCreationRequest
                    .builder()
                    .name("Test playlist")
                    .description("une playlist de test")
                    .songsIds(Set.of())
                    .build();
       
            // when
            ResponseEntity<PlaylistResponseDTO> response = testRestTemplate.exchange("/api/playlist/create", HttpMethod.POST, new HttpEntity<>(request, headers), PlaylistResponseDTO.class);
       }
        ```
   ### Then
    1. Enfin, on vérifie que le status HTTP est celui qu'on attendait, dans notre cas `201` created
    2. On regarde si en base il y a une nouvelle entité `playlistEntity`
    3. On regarde si les élements que l'on voulait suivre ont été appelés le bon nombre de fois
        ```java
        @Test
        void canCreatePlaylistWithoutSong() {
            //given
            final HttpHeaders headers = new HttpHeaders();
    
            final PlaylistCreationRequest request = PlaylistCreationRequest
                    .builder()
                    .name("Test playlist")
                    .description("une playlist de test")
                    .songsIds(Set.of())
                    .build();
        
       
            // when
            ResponseEntity<PlaylistResponseDTO> response = testRestTemplate.exchange("/api/playlist/create", HttpMethod.POST, new HttpEntity<>(request, headers), PlaylistResponseDTO.class);
       
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(playlistRepository.count()).isEqualTo(1);
            verify(playlistComponent, times(1)).createPlaylistEntity(any(PlaylistEntity.class));
            verify(songComponent, times(1)).getSetSongEntity(Set.of());
         }
        ```
10. Prenons un autre exemple, nous voulons tester la récuperation d'une playlist qui n'existe pas, donc le code `404` not found. Je vous laisse comprendre le code par vous-même
    ```java
    @Test
        void getNotFoundPlaylist() {
            //Given
            final HttpHeaders headers = new HttpHeaders();
    
            final Map<String, Object> urlParams = new HashMap<>();
            urlParams.put("idPlaylist", "ma playlist qui n'existe pas");
    
            NotFoundErrorResponse notFoundErrorResponseExpected = NotFoundErrorResponse
                    .builder()
                    .uri("/api/playlist/ma%20playlist%20qui%20n%27existe%20pas")
                    .errorMessage("La playlist [ma playlist qui n'existe pas] n'a pas été trouvé")
                    .build();
    
            //when
            ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/playlist/{idPlaylist}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);
    
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).usingRecursiveComparison()
                    .isEqualTo(notFoundErrorResponseExpected);
        }
    ```
    1. Nous ajoutons une `final Map<String, Object> urlParams` qui nous permet de gérer tous les arguments utiles pour créer notre lien


* Bravo si vous êtes arrivés ici et que vous avez tous compris, bravo à vous, je suis fier de vous

![](https://tenor.com/fr/view/mission-impossible-we-got-this-you-got-this-aja-oh-yeah-gif-9140872.gif)


### A votre tour

* Terminer les tests du controller `PlaylistController`


![good luck](https://tenor.com/fr/view/johnny-depp-pirates-of-the-caribbean-good-luck-gif-26735885.gif)
