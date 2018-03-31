package ch.unihub.business.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ch.unihub.dom.Post;

@Stateless
public class PostServiceImpl implements PostService {
	// The serial-id
	private static final long serialVersionUID = 1386508985359072399L;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Post> getAll() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> c = qb.createQuery(Post.class);

		Root<Post> variableRoot = c.from(Post.class);
		c.select(variableRoot);
		c.orderBy(qb.asc(variableRoot.get("id")));
		TypedQuery<Post> query = entityManager.createQuery(c);
		return query.getResultList();
	}
	
	@Override
	public int getNbPosts() {
		return getAll().size();
	}
	
}
