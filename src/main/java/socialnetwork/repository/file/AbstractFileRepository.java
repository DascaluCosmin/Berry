package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    protected Repository<Long, User> userRepository;

    /**
     * Constructor that creates a new AbstractFileRepository
     * @param fileName String, representing the name of the file where the data is loaded from / stored to
     * @param validator Validator<E>, representing the validator of the AbstractFileRepository
     */
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    public AbstractFileRepository(String fileName, Validator<E> validator, Repository<Long, User> repository) {
        super(validator);
        this.fileName = fileName;
        this.userRepository = repository;
        loadData();
    }

    /**
     * Method that loads the data from the file
     */
    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line -> {
                List<String> attributes = Arrays.asList(line.split(";")); // Split the attributes by ";"
                E e = extractEntity(attributes); // Create the Entity based on the attributes
                super.save(e); // Add the loaded Entity in the Repository
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reloads the data from the file
     */
    private void reload() {
        Iterable<E> currentEntities = super.findAll();
        try {
            PrintWriter writer =  new PrintWriter(fileName);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("File to reload doesn't exist!");
        }
        currentEntities.forEach(this::writeToFile);
    }

    /**
     *  (Template method design pattern)
     * Method that extracts an entity of type E having a specified list of attributes
     * @param attributes List<String>, representing the attributes of the Entity to be extracted
     * @return E, based on the given attributes
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * Method that gets the serialization of an entity
     * @param entity Entity, representing the entity whose serialization is being determined
     * @return String, representing the serialization of the entity
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Method that adds a new entity to the AbstractFileRepository
     * @param entity E, representing the entity to be added
     *         entity must be not null
     * @return null, if the given entity is saved
     *         non-null entity, otherwise (it already exists)
     */
    @Override
    public E save(E entity){
        E e = super.save(entity);
        if (e==null) {
            writeToFile(entity);
        }
        return e;
    }

    /**
     * Method that deletes an entity from the AbstractFileRepository
     * @param id ID, representing the ID of the Entity to be deleted,
     *        id must be not null
     * @return E, representing the removed entity or null if the entity doesn't exist
     */
    @Override
    public E delete(ID id) {
        E e = super.delete(id);
        if (e != null) {
            this.reload();
        }
        return e;
    }

    /**
     * Method that updates an entity in the AbstractFileRepository
     * @param entity E, representing the new entity,
     *          entity must not be null
     * @return null, if the entity was updated
     *         non-null entity, otherwise (doesn't exist)
     */
    @Override
    public E update(E entity) {
        E e = super.update(entity);
//        if (e == null) {
//            writeToFile(entity);
//        }
        return e;
    }

    /**
     * Method that writes the entity (data) to the file
     * @param entity E, representing the entity to be written to the file
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

