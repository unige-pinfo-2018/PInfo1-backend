package ch.unihub.business.service;

import ch.unihub.dom.AccountConfirmation;
import ch.unihub.dom.ResetPasswordRequest;
import ch.unihub.dom.User;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * @author Arthur Deschamps
 */
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
     * Finds a user by its email.
     * @param email The user's email.
     * @return The {@code User} corresponding to the given email, or an empty optional if not found.
     */
	Optional<User> getUserByEmail(final String email);

    /**
     * Persists a user in database.
     * @param user A {@code User} object.
     */
	void createUser(@NotNull final User user) throws URISyntaxException;

    /**
     * Persists an account confirmation in database.
     * @param user A {@code User} object to create a confirmation for.
     * @return The confirmation ID, if the email is not null and the user is not yet confirmed.
     */
	Optional<String> createAccountConfirmation(@NotNull final User user);

    /**
     * Deletes all account confirmations for the database.
     * @param userEmail The email linked in the confirmation.
     */
	void deleteAccountConfirmations(final String userEmail);

    /**
     * Finds all the account confirmations with the given email.
     * @param userEmail The email contained in the confirmation.
     */
	List<AccountConfirmation> findAccountConfirmations(final String userEmail);

    /**
     * Saves the password reset request in DB.
     * @param userEmail The user's email.
     * @return The request id.
     */
	String createPasswordResetRequest(final String userEmail);

    /**
     * Finds all the password reset requests from the given email.
     * @param userEmail The user's email.
     */
	List<ResetPasswordRequest> findResetPasswordRequests(final String userEmail);

    /**
     * Deletes all the password reset requests from the given email.
     * @param userEmail The user's email.
     */
	void deletePasswordRequests(final String userEmail);

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
     * Updates a user's password.
     * @param user A user object.
     * @param password A password in clear.
     */
	void updatePassword(final User user, final String password);

    /**
     * @return The total number of users in the database.
     */
	int getNbUsers();
}