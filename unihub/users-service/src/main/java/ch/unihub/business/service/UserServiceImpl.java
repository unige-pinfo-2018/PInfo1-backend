package ch.unihub.business.service;

import ch.unihub.dom.Hash;
import ch.unihub.dom.User;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * @author Arthur Deschamps
 */
@Stateless
public class UserServiceImpl implements UserService {
	// The serial-id
	private static final long serialVersionUID = 1386508985359072399L;
	// Number of hash iterations to apply to the password
	private static final int HASH_ITERATIONS = 500000;

	private RandomNumberGenerator rng = new SecureRandomNumberGenerator();
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
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);

		Root<User> root = cq.from(User.class);
		Predicate usernameCond = cb.equal(root.get("username"), username);
		cq.where(usernameCond);
		TypedQuery<User> query = entityManager.createQuery(cq);
		List<User> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<User> getUser(Long id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);

		Root<User> root = cq.from(User.class);
		Predicate idCond = cb.equal(root.get("id"), id);
		cq.where(idCond);
		TypedQuery<User> query = entityManager.createQuery(cq);
		List<User> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public void addUser(@NotNull User user, @NotNull String password) {
		// Id will be created automatically
		user.setId(null);
		// Computes the salt
		Object salt = rng.nextBytes();
		// Computes the hash
		Sha256Hash hash = new Sha256Hash(password, salt, HASH_ITERATIONS);
		final String hashStr = hash.toBase64();
		final String saltStr = hash.getSalt().toBase64();
		final Hash hashObj = new Hash();
		hashObj.setHash(hashStr);
		hashObj.setSalt(saltStr);
		hashObj.setUser(user);
		// Lets J2EE generate the id
		hashObj.setId(null);

		logger.info(hashObj.getSalt());
		logger.info(hashObj.getHash());
		// Saves both object in database
		entityManager.persist(user);
		entityManager.persist(hashObj);
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
		Optional<User> user = getUser(updatedUser.getId());
		if (!user.isPresent()) return Optional.empty();
		user.get().copyFields(updatedUser);
		return user;
	}

	@Override
	public int getNbUsers() {
		return getAll().size();
	}
}
