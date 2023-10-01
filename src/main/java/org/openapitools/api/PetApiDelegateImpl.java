package org.openapitools.api;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.openapitools.model.Category;
import org.openapitools.model.ModelApiResponse;
import org.openapitools.model.Pet;
import org.openapitools.model.Tag;
import org.openapitools.repository.PetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class PetApiDelegateImpl implements PetApiDelegate {

    private final PetRepository petRepository;

    private final NativeWebRequest request;

    public PetApiDelegateImpl(PetRepository petRepository, NativeWebRequest request) {
        this.petRepository = petRepository;
        this.request = request;
    }

    @PostConstruct
    private void initPets() {
        Category dogs = new Category().id(1L).name("Dogs");
        Category cats = new Category().id(2L).name("Cats");
        Category rabbits = new Category().id(3L).name("Rabbits");
        Category lions = new Category().id(4L).name("Lions");

        petRepository.save(createPet(1, cats, "Cat 1", new String[] {
                "url1", "url2" }, new String[] { "tag1", "tag2" }, Pet.StatusEnum.AVAILABLE));
        petRepository.save(createPet(2, cats, "Cat 2", new String[] {
                "url1", "url2" }, new String[] { "tag2", "tag3" }, Pet.StatusEnum.AVAILABLE));
        petRepository.save(createPet(3, cats, "Cat 3", new String[] {
                "url1", "url2" }, new String[] { "tag3", "tag4" }, Pet.StatusEnum.PENDING));

        petRepository.save(createPet(4, dogs, "Dog 1", new String[] {
                "url1", "url2" }, new String[] { "tag1", "tag2" }, Pet.StatusEnum.AVAILABLE));
        petRepository.save(createPet(5, dogs, "Dog 2", new String[] {
                "url1", "url2" }, new String[] { "tag2", "tag3" }, Pet.StatusEnum.SOLD));
        petRepository.save(createPet(6, dogs, "Dog 3", new String[] {
                "url1", "url2" }, new String[] { "tag3", "tag4" }, Pet.StatusEnum.PENDING));

        petRepository.save(createPet(7, lions, "Lion 1", new String[] {
                "url1", "url2" }, new String[] { "tag1", "tag2" }, Pet.StatusEnum.AVAILABLE));
        petRepository.save(createPet(8, lions, "Lion 2", new String[] {
                "url1", "url2" }, new String[] { "tag2", "tag3" }, Pet.StatusEnum.AVAILABLE));
        petRepository.save(createPet(9, lions, "Lion 3", new String[] {
                "url1", "url2" }, new String[] { "tag3", "tag4" }, Pet.StatusEnum.AVAILABLE));

        petRepository.save(createPet(10, rabbits, "Rabbit 1", new String[] {
                "url1", "url2" }, new String[] { "tag3", "tag4" }, Pet.StatusEnum.AVAILABLE));
    }

    @Override
    public ResponseEntity<Void> addPet(Pet pet) {
        petRepository.save(pet);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deletePet(Long petId, String apiKey) {
        petRepository.deleteById(petId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsByStatus(List<String> statusList) {
        List<Pet.StatusEnum> statusEnums = statusList.stream()
                .map(s -> Optional.ofNullable(Pet.StatusEnum.fromValue(s))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + s))
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(petRepository.findPetsByStatus(statusEnums));
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsByTags(List<String> tags) {
        return ResponseEntity.ok(petRepository.findPetsByTags(tags));
    }

    @Override
    public ResponseEntity<Pet> getPetById(Long petId) {
        ApiUtil.checkApiKey(request);
        return petRepository.findById(petId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<Void> updatePet(Pet pet) {
        return addPet(pet);
    }

    @Override
    public ResponseEntity<Void> updatePetWithForm(Long petId, String name, String status) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!StringUtils.isEmpty(name))
            pet.name(name);
        if(!StringUtils.isEmpty(name))
            pet.setStatus(Pet.StatusEnum.fromValue(status));
        return addPet(pet);
    }

    @Override
    public ResponseEntity<ModelApiResponse> uploadFile(Long petId, String additionalMetadata, MultipartFile file) {
        try {
            String uploadedFileLocation = "./" + file.getName();
            System.out.println("uploading to " + uploadedFileLocation);
            IOUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFileLocation));
            String msg = String.format("additionalMetadata: %s\nFile uploaded to %s, %d bytes", additionalMetadata, uploadedFileLocation, (new File(uploadedFileLocation)).length());
            ModelApiResponse output = new ModelApiResponse().code(200).message(msg);
            return ResponseEntity.ok(output);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't upload file", e);
        }
    }

    private static Pet createPet(long id, Category category, String name, String[] urls,
                                 String[] tags, Pet.StatusEnum status) {
        Pet pet = new Pet()
                .id(id)
                .category(category)
                .name(name)
                .status(status);

        if (null != urls) {
            pet.setPhotoUrls(Arrays.asList(urls));
        }

        final AtomicLong i = new AtomicLong(0);
        if (null != tags) {
            Arrays.stream(tags)
                    .map(tag -> new Tag().name(tag).id(i.incrementAndGet()))
                    .forEach(pet::addTagsItem);
        }
        return pet;
    }

}
