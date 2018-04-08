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
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

import ch.unihub.dom.Post;
import ch.unihub.dom.Like;
import ch.unihub.dom.Dislike;
import ch.unihub.dom.Tag;

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
	public Optional<Post> getPost(Long id)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> cq = qb.createQuery(Post.class);

		Root<Post> root = cq.from(Post.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);

		TypedQuery<Post> query = entityManager.createQuery(cq);
        List<Post> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<Like> getLike(Long id)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Like> cq = qb.createQuery(Like.class);

		Root<Like> root = cq.from(Like.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);

		TypedQuery<Like> query = entityManager.createQuery(cq);
		List<Like> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<Dislike> getDislike(Long id)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Dislike> cq = qb.createQuery(Dislike.class);

		Root<Dislike> root = cq.from(Dislike.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);

		TypedQuery<Dislike> query = entityManager.createQuery(cq);
		List<Dislike> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<Tag> getTag(Long id)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> cq = qb.createQuery(Tag.class);

		Root<Tag> root = cq.from(Tag.class);
		Predicate idCond = qb.equal(root.get("id"), id);
		cq.where(idCond);

		TypedQuery<Tag> query = entityManager.createQuery(cq);
		List<Tag> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public void addPost(@NotNull Post newPost) {
		newPost.setId(null);
		entityManager.persist(newPost);
	}

	@Override
	public void addLike(@NotNull Like newLike) {
		newLike.setId(null);
		entityManager.persist(newLike);
	}

	@Override
	public void addDislike(@NotNull Dislike newDislike) {
		newDislike.setId(null);
		entityManager.persist(newDislike);
	}

	@Override
	public void addTag(@NotNull Tag newTag) {
		newTag.setId(null);
		entityManager.persist(newTag);
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

	@Override
	public Long getUserIdPost(Long id)
	{
		Optional<Post> thePost = getPost(id);
		return thePost.isPresent() ? thePost.get().getUserId() : 0;
    }

	@Override
	public Long getParentIdPost(Long id)
	{
		Optional<Post> thePost = getPost(id);
		return thePost.isPresent() ? thePost.get().getParentId() : 0;
	}

	@Override
	public Long getReplyToIdPost(Long id)
	{
		Optional<Post> thePost = getPost(id);
		return thePost.isPresent() ? thePost.get().getReplyToId() : 0;
	}

	@Override
	public Long getNbUpvotes(Long idPost)
	{
		//get likes
		long nbUpvotes=0;
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Like> cq = qb.createQuery(Like.class);

		Root<Like> root = cq.from(Like.class);
		Predicate idCond = qb.equal(root.get("postId"), idPost);
		cq.where(idCond);

		TypedQuery<Like> query = entityManager.createQuery(cq);
		nbUpvotes = query.getResultList().size();

		//get dislikes/
		CriteriaQuery<Dislike> cq2 = qb.createQuery(Dislike.class);
		Root<Dislike> root2 = cq2.from(Dislike.class);
		Predicate idCond2 = qb.equal(root2.get("postId"), idPost);
		cq2.where(idCond2);

		TypedQuery<Dislike> query2 = entityManager.createQuery(cq2);
		nbUpvotes -= query2.getResultList().size();
		return nbUpvotes;
	}

    @Override
    public Optional<Post> updatePost(Post updatedPost) {
        Optional<Post> post = getPost(updatedPost.getId());
        if (!post.isPresent()) return Optional.empty();
        post.get().copyFields(updatedPost);
        return post;
    }
}
