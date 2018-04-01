package ch.unihub.business.service;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import ch.unihub.dom.User;

@Stateless
public class UserServiceImpl implements UserService {
	// The serial-id
	private static final long serialVersionUID = 1386508985359072399L;

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
	public void addUser(@NotNull User user) {
		user.setId(null);
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
