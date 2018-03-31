package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;

import ch.unihub.dom.User;

@Local
public interface UserService extends Serializable {

    /**
     * @return All the existing users.
     */
	List<User> getAll();

    /**
     * @param username The username of the user to return.
     * @return The {@code User} corresponding to the given username.
     */
	User getUser(final String username);

    /**
     * @param id The user's id.
     * @return The {@code User} corresponding to the given id.
     */
	User getUser(final Long id);

    /**
     * Adds a user to the database.
     * @param user A {@code User} object.
     */
	void addUser(@NotNull final User user);

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
     * Updates a user in the database, using its id to find it.
     * @param updatedUser An updated {@code User} object.
     */
	void updateUser(final User updatedUser);

    /**
     * @return The total number of users in the database.
     */
	int getNbUsers();
}
