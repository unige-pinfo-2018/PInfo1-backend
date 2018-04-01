package ch.unihub.business.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import ch.unihub.dom.User;

@Stateless
public class UserServiceImpl implements UserService {
	// The serial-id
	private static final long serialVersionUID = 1386508985359072399L;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<User> getAll() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> c = qb.createQuery(User.class);

		Root<User> variableRoot = c.from(User.class);
		c.select(variableRoot);
		c.orderBy(qb.asc(variableRoot.get("id")));
		TypedQuery<User> query = entityManager.createQuery(c);
		return query.getResultList();
	}

	@Override
	public User getUser(String username) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = qb.createQuery(User.class);

		Root<User> root = cq.from(User.class);
		Predicate usernameCond = qb.equal(root.get("username"), username);
		cq.where(usernameCond);
		TypedQuery<User> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

	@Override
	public User getUser(Long id) {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = qb.createQuery(User.class);

		Root<User> root = cq.from(User.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);
		TypedQuery<User> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}

	@Override
	public void addUser(@NotNull User user) {
		user.setId(null);
		entityManager.persist(user);
	}

	@Override
	public void deleteUser(Long id) {
		// TODO
	}

	@Override
	public void deleteUser(String username) {
		// TODO
	}

	@Override
	public void updateUser(User updatedUser) {
		// TODO
	}

	@Override
	public int getNbUsers() {
		return getAll().size();
	}
}
