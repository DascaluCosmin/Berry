package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    protected Map<ID,E> entities;

    /**
     * Constructor that creates a new InMemoryRepository
     * @param validator ValidatorService<E>, representing the validator of the in memory Repository
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**
     * Method that gets a specific entity in the in memory Repository
     * @param id ID, reprsenting the id of the entity to be returned,
     *           id must not be null
     * @return E, representing the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**
     * Method that gets all the entities in the in memory Repository
     * @return Iterable<E>, representing all the entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * Method that adds a new entity to the in memory Repository
     * @param entity E, representing the entity to be added
     *         entity must be not null
     * @return null - if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * Method that removes the entity with the specified id from the in memory Repository
     * @param id ID, representing the ID of the Entity to be deleted,
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    @Override
    public E delete(ID id) {
        return entities.remove(id);
    }

    /**
     * Method that updates an entity in the in memory Repository
     * @param entity E, representing the new entity,
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);
        entities.put(entity.getId(),entity);
        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;
    }
}
