package fr.uga.l3miage.spring.tp2.exo1.repositories;

import fr.uga.l3miage.exo1.enums.GenreMusical;
import fr.uga.l3miage.spring.tp2.exo1.models.ArtistEntity;
import fr.uga.l3miage.spring.tp2.exo1.models.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
public class ArtistRepositoryTest {
     @Autowired
     private ArtistRepository artistRepository;
    @Test
    void countByGenreMusicalEqualsReturnEntity() {
        //given

        ArtistEntity artistEntity1 = ArtistEntity
                .builder()
                .name("paul")
                .biography("bio")
                .genreMusical(GenreMusical.RAP)
                .build();

        //when

        artistRepository.save(artistEntity1);
        int artistEntity = artistRepository.countByGenreMusicalEquals(GenreMusical.RAP);

        //then
        assertThat(artistEntity).isEqualTo(1);
    }
    @Test
    void countByGenreMusicalEqualsDontReturnEntity() {
        //given

        ArtistEntity artistEntity1 = ArtistEntity
                .builder()
                .name("paul")
                .biography("bio")
                .genreMusical(GenreMusical.RAP)
                .build();

        //when

        artistRepository.save(artistEntity1);
        int artistEntity = artistRepository.countByGenreMusicalEquals(GenreMusical.CLASSIC);

        //then
        assertThat(artistEntity).isEqualTo(0);
    }
    @Test
    void countByGenreMusicalEqualsGenreMusicalEmpty() {
        //given

        ArtistEntity artistEntity1 = ArtistEntity
                .builder()
                .name("paul")
                .biography("bio")
                .build();

        //when

        artistRepository.save(artistEntity1);
        int artistEntity = artistRepository.countByGenreMusicalEquals(GenreMusical.CLASSIC);

        //then
        assertThat(artistEntity).isEqualTo(0);
    }
}
