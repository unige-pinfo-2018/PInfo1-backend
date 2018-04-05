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
	
	@Override
	public Post getPost(Long id) 
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> cq = qb.createQuery(Post.class);

		Root<Post> root = cq.from(Post.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);
		TypedQuery<Post> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}
	
	@Override
	public void addPost(@NotNull Post newPost) {
		newPost.setId(null);
		newPost.setId(getNextPostId());
		entityManager.persist(newPost);
	}
	
	
	public Long getNextPostId() {
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> c = qb.createQuery(Long.class);
		Root<Post> from = c.from(Post.class);
		c.select(qb.max(from.get("id")));		
		TypedQuery<Long> query = entityManager.createQuery(c);
		System.out.println(query);
		
		Long nb = query.getSingleResult(); 
		long firstId=0;
		if (nb == null) {
			return firstId;
		} else {
			return nb + 1;	
		}
		
	}
}
