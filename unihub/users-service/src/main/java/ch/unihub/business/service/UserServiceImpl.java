package ch.unihub.business.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
	public int getNbUsers() {
		return getAll().size();
	}
}
