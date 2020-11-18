package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;

/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * Method that gets a specific entity in the Repository
     * @param id - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    E findOne(ID id);

    /**
     * Method that gets all the entities in the Repository
     * @return Iterable<E>, representing all the entities
     */
    Iterable<E> findAll();

    /**
     * Method that adds a new entity to the Repository
     * @param entity E, representing the entity to be added
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     */
    E save(E entity);

    /**
     * Method that removes the entity with the specified id from the Repository
     * @param id ID, representing the the ID of the Entity to be deleted,
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    E delete(ID id);

    /**
     * Method that updates an entity in the Repository
     * @param entity E, representing the new entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    E update(E entity);
}

