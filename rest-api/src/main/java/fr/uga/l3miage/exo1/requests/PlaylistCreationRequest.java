package fr.uga.l3miage.exo1.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PlaylistCreationRequest {
    private final String name;

    private final  String description;

    private final Set<String> songsIds;
}
