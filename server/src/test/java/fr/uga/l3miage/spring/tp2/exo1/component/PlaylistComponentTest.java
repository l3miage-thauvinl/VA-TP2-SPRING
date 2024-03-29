package fr.uga.l3miage.spring.tp2.exo1.component;

import fr.uga.l3miage.spring.tp2.exo1.components.PlaylistComponent;
import fr.uga.l3miage.spring.tp2.exo1.exceptions.technical.NotFoundPlaylistEntityException;
import fr.uga.l3miage.spring.tp2.exo1.models.PlaylistEntity;
import fr.uga.l3miage.spring.tp2.exo1.repositories.PlaylistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class PlaylistComponentTest {
    @Autowired
    private PlaylistComponent playlistComponent;
    @MockBean
    private PlaylistRepository playlistRepository;
    @Test
    void getPlaylistReturnEntity(){
        //Given
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .songEntities(Set.of())
                .description("test")
                .build();
        when(playlistRepository.findById(anyString())).thenReturn(Optional.of(playlistEntity));

        // when - then
        assertDoesNotThrow(()->playlistComponent.getPlaylist("test"));
    }
    @Test
    void getPlaylistDontReturnEntity(){
        //Given
        when(playlistRepository.findById(anyString())).thenReturn(Optional.empty());

        //then - when
        assertThrows(NotFoundPlaylistEntityException.class,()->playlistComponent.getPlaylist("test"));
    }
}