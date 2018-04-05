package ch.unihub.business.service;

import ch.unihub.dom.User;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Local
public interface UserService extends Serializable {

    /**
     * @return All the existing users.
     */
	List<User> getAll();

    /**
     * @param username The username of the user to return.
     * @return The {@code User} corresponding to the given username, or an empty optional.
     */
	Optional<User> getUser(final String username);

    /**
     * @param id The user's id.
     * @return The {@code User} corresponding to the given id, or an empty optional.
     */
	Optional<User> getUser(final Long id);

    /**
     * Adds a user to the database.
     * @param user A {@code User} object.
     */
	void addUser(@NotNull final User user) throws URISyntaxException;

    /**
     * Deletes a user from the database, using its id.
     * @param id The id of the user to delete.
     */
	void deleteUser(final Long id);

    /**
     * Deletes a user from the database, using its username.
     * @param username The username of the user to delete.
     */
	void deleteUser(final String username);

    /**
     * Updates a user in the database, using its id to find it. The update can only contain a part of the fields of
     * the {@code User} object, for instance only the username. In that case, the other null fields will keep their
     * initial values.
     *
     * @param updatedUser An updated {@code User} object, or an empty optional if the user wasn't found.
     */
	Optional<User> updateUser(final User updatedUser);

    /**
     * @return The total number of users in the database.
     */
	int getNbUsers();
}