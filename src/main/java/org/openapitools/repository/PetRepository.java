package org.openapitools.repository;

import org.openapitools.model.Pet;
import org.openapitools.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PetRepository extends HashMapRepository<Pet, Long> {

    private Long sequenceId = 1L;

    @Override
    <S extends Pet> Long getEntityId(S pet) {
        return pet.getId();
    }

    @Override
    public <S extends Pet> S save(S pet) {
        if (pet.getId() != null && pet.getId() > sequenceId) {
            sequenceId = pet.getId();
        }
        pet.setId(sequenceId);
        sequenceId += 1;
        return super.save(pet);
    }

    public List<Pet> findPetsByStatus(List<String> status) {
        return entities.values().stream()
                .filter(entity -> entity.getStatus() != null)
                .filter(entity -> status.contains(entity.getStatus().toString()))
                .collect(Collectors.toList());
    }

    public List<Pet> findPetsByTags(List<String> tags) {
        return entities.values().stream()
                .filter(entity -> entity.getTags() != null)
                .filter(entity -> entity.getTags().stream()
                        .map(Tag::getName)
                        .anyMatch(tags::contains)
                )
                .collect(Collectors.toList());
    }
}
