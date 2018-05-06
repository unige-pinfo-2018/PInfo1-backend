package ch.unihub.business.service;


import ch.unihub.dom.AccountConfirmation;
import ch.unihub.dom.ResetPasswordRequest;
import ch.unihub.dom.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author PAUL
 */
@Stateless
public class UserServiceImpl implements UserService {
	// The serial-id
	private static final long serialVersionUID = 1386509985359072399L;
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<User> getAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> c = cb.createQuery(User.class);

		Root<User> variableRoot = c.from(User.class);
		c.select(variableRoot);
		c.orderBy(cb.asc(variableRoot.get("id")));
		TypedQuery<User> query = entityManager.createQuery(c);
		return query.getResultList();
	}

	@Override
	public Optional<User> getUser(String username) {
		return getUser("username", username);
	}

	@Override
	public Optional<User> getUser(Long id) {
		return getUser("id", id);
	}

	@Override
	public Optional<User> getUserByEmail(String email) {
		return getUser("email", email);
	}

	@Override
	public void createUser(@NotNull User user) {
		// Id will be created automatically
		user.setId(null);
		// Saves the user in DB
		entityManager.persist(user);
	}

	@Override
	public void deleteUser(Long id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<User> delete = cb.createCriteriaDelete(User.class);

		Root<User> root = delete.from(User.class);
		delete.where(cb.equal(root.get("id"), id));
		if (entityManager.createQuery(delete).executeUpdate() < 1)
			throw new NotFoundException("No user with id " + id.toString() + " found in database");
	}

	@Override
	public void deleteUser(String username) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<User> delete = cb.createCriteriaDelete(User.class);

		Root<User> root = delete.from(User.class);
		delete.where(cb.equal(root.get("username"), username));
		if (entityManager.createQuery(delete).executeUpdate() < 1)
			throw new NotFoundException("No user with username \"" + username + "\" found in database");
	}

	@Override
	public Optional<User> updateUser(User updatedUser) {
		Optional<User> userOpt = getUser(updatedUser.getId());
		userOpt.ifPresent(user -> {
			user.copyFields(updatedUser);
			entityManager.persist(user);
		});
		return userOpt;
	}

	@Override
	public void updatePassword(User user, String password) {
		user.setPassword(password);
		entityManager.merge(user);
	}

	@Override
	public Optional<String> createAccountConfirmation(User user) {
		if (user.getEmail() != null && !user.isConfirmed()) {
			final AccountConfirmation accountConfirmation = new AccountConfirmation();
			accountConfirmation.setId(null);
			// Random 36-characters string.
			final String confirmationId = UUID.randomUUID().toString();
			accountConfirmation.setConfirmationId(confirmationId);
			accountConfirmation.setUserEmail(user.getEmail());
			entityManager.persist(accountConfirmation);
			return Optional.of(confirmationId);
		}
		return Optional.empty();
	}

	@Override
	public void deleteAccountConfirmations(String userEmail) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<AccountConfirmation> delete = cb.createCriteriaDelete(AccountConfirmation.class);

		Root<AccountConfirmation> root = delete.from(AccountConfirmation.class);
		delete.where(cb.equal(root.get("userEmail"), userEmail));
		// No need to check for results, there could no confirmation for the given email
		entityManager.createQuery(delete).executeUpdate();
	}

	@Override
	public List<AccountConfirmation> findAccountConfirmations(String userEmail) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AccountConfirmation> query = cb.createQuery(AccountConfirmation.class);

		Root<AccountConfirmation> root = query.from(AccountConfirmation.class);
		query.where(cb.equal(root.get("userEmail"), userEmail));
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public String createPasswordResetRequest(String userEmail) {
		ResetPasswordRequest request = new ResetPasswordRequest();
		request.setUserEmail(userEmail);
		final String requestId = UUID.randomUUID().toString();
		request.setRequestId(requestId);
		entityManager.persist(request);
		return requestId;
	}

	@Override
	public List<ResetPasswordRequest> findResetPasswordRequests(String userEmail) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ResetPasswordRequest> query = cb.createQuery(ResetPasswordRequest.class);

		Root<ResetPasswordRequest> root = query.from(ResetPasswordRequest.class);
		query.where(cb.equal(root.get("userEmail"), userEmail));
		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public void deletePasswordRequests(String userEmail) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaDelete<ResetPasswordRequest> delete = cb.createCriteriaDelete(ResetPasswordRequest.class);

		Root<ResetPasswordRequest> root = delete.from(ResetPasswordRequest.class);
		delete.where(cb.equal(root.get("userEmail"), userEmail));
		// No need to check for results, there could be no request for that email.
		entityManager.createQuery(delete).executeUpdate();
	}

	@Override
	public int getNbUsers() {
		return getAll().size();
	}

	private <T> Optional<User> getUser(String predicateField, T predicatedValue) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		cq.where(cb.equal(root.get(predicateField), predicatedValue));
		TypedQuery<User> query = entityManager.createQuery(cq);
		List<User> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}
}
