package ch.unihub.business.service;

import ch.unihub.dom.Dislike;
import ch.unihub.dom.Like;
import ch.unihub.dom.Post;
import ch.unihub.dom.Tag;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	public Optional<Like> getLike(Long value,String columnId)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Like> cq = qb.createQuery(Like.class);

		Root<Like> root = cq.from(Like.class);
		Predicate idCond = qb.equal(root.get(columnId), value);
		cq.where(idCond);

		TypedQuery<Like> query = entityManager.createQuery(cq);
		List<Like> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<Dislike> getDislike(Long value,String columnId)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Dislike> cq = qb.createQuery(Dislike.class);

		Root<Dislike> root = cq.from(Dislike.class);
		Predicate idCond = qb.equal(root.get(columnId), value);
		cq.where(idCond);

		TypedQuery<Dislike> query = entityManager.createQuery(cq);
		List<Dislike> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public Optional<Tag> getTag(String value,String columnId)
	{
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tag> cq = qb.createQuery(Tag.class);

		Root<Tag> root = cq.from(Tag.class);
		Predicate idCond;

		if (columnId.equals("name")) {
			idCond = qb.equal(root.get(columnId), value); }
		else {
			idCond = qb.equal(root.get(columnId), Long.valueOf(value).longValue()); }

		cq.where(idCond);

		TypedQuery<Tag> query = entityManager.createQuery(cq);
		List<Tag> results = query.getResultList();
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	@Override
	public void addPost(@NotNull Post newPost) {
		newPost.setId(null);
		Date date= new Date();
		//getTime() returns current time in milliseconds
		long time = date.getTime();
		//Passed the milliseconds to constructor of Timestamp class
		Timestamp ts = new Timestamp(time);
		newPost.setDatePost(ts);
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

	@Override
	public Long getUserIdPost(Long id)
	{
		Optional<Post> thePost = getPost(id);
		if (thePost.isPresent()){
			return thePost.get().getUserId();
		} else {
			return Long.parseLong(Integer.toString(0));
		}
    }

	@Override
	public Long getParentIdPost(Long id)
	{
		Optional<Post> thePost = getPost(id);
		if (thePost.isPresent()){
			return thePost.get().getParentId();
		} else {
			return Long.parseLong(Integer.toString(0));
		}
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
		CriteriaBuilder qb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Like> cq = qb.createQuery(Like.class);

		Root<Like> root = cq.from(Like.class);
		Predicate idCond = qb.equal(root.get("postId"), idPost);
		cq.where(idCond);

		TypedQuery<Like> query = entityManager.createQuery(cq);
		long nbUpvotes = query.getResultList().size();

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
	public Date getDate(Long id)
	{
		Optional<Post> thePost = getPost(id);
		Date date= new Date();
		//getTime() returns current time in milliseconds
		long time = date.getTime();
		//Passed the milliseconds to constructor of Timestamp class
		Timestamp ts = new Timestamp(time);
		return thePost.isPresent() ? thePost.get().getDatePost() : ts;
	}

	@Override
	public String getContent(Long id)
	{
		Optional<Post> thePost = getPost(id);
		if (thePost.isPresent())
		{
			return thePost.get().getContent();
		}
		else
		{
			return "";
		}
	}

	@Override
	public List<Long> getListIdTags(Long idPost) {

	    List<Long> listIdTags= new ArrayList<Long>();
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tag> cq = qb.createQuery(Tag.class);

        Root<Tag> root = cq.from(Tag.class);
        Predicate idCond = qb.equal(root.get("postId"), idPost);
        cq.where(idCond);

        TypedQuery<Tag> query = entityManager.createQuery(cq);
        List<Tag> listTags = query.getResultList();

        for (Tag element : listTags)
        {
            listIdTags.add(element.getId());
        }
        return listIdTags;
    }

    @Override
    public Optional<Post> updatePost(Post updatedPost) {
        Optional<Post> post = getPost(updatedPost.getId());
        if (!post.isPresent()) return Optional.empty();
        post.get().copyFields(updatedPost);
        return post;
    }



    //              !!!!!       first do this sql query like in squirrel :          !!!!!
    //          ALTER TABLE `POSTS` ADD FULLTEXT(`CONTENT`);
    //example : http://localhost:18080/post-service/rest/posts/searchPost?q=my+question+for+python&n=10&t=info&t=informatique
    // even if you dont have a question :
    //example : http://localhost:18080/post-service/rest/posts/searchPost?n=4&t=info&t=tag2
    @Override
    public List searchPost(String QuestionUser, int nbPost,List<String> listTags) {

        String listTagsSql= "INNER JOIN TAGS ON TAGS.NAME IN (";
	    for (int i=0;i<listTags.size();i++)
        {
            listTagsSql=listTagsSql.concat("'");
            listTagsSql=listTagsSql.concat(listTags.get(i));
            listTagsSql=listTagsSql.concat("',");
        }
        listTagsSql=listTagsSql.substring(0, listTagsSql.length() - 1);
        listTagsSql=listTagsSql.concat(") AND TAGS.POSTID = POSTS.ID ");
        if (listTags.size() == 0)
        {
            listTagsSql=" ";
        }

        String MatchQuestionSql;
        String MatchQuestionSql2;
        if (QuestionUser == null)
        {
            MatchQuestionSql =" FROM POSTS ";
            MatchQuestionSql2 ="ORDER BY POSTS.ID DESC LIMIT "+nbPost+";\n";
        }
        else {
            MatchQuestionSql= ", MATCH(CONTENT) AGAINST ('"+QuestionUser+"' IN NATURAL LANGUAGE MODE) AS score FROM POSTS ";
            MatchQuestionSql2 = "AND MATCH(CONTENT) AGAINST ('"+QuestionUser+"' IN NATURAL LANGUAGE MODE) > 0 ORDER BY score DESC LIMIT "+nbPost+";\n";

        }

        Query q = entityManager.createNativeQuery(
                "SELECT DISTINCT POSTS.ID" +
                   MatchQuestionSql+
                   listTagsSql+
                   "WHERE PARENTID IS NULL AND REPLYTOID IS NULL "+
                   MatchQuestionSql2);

        return q.getResultList();
    }

    @Override
    public List getPostsByIds(List<Long> listId)
    {
        String listIdSQL=" ";
        for (int i=0;i<listId.size();i++)
        {
            listIdSQL=listIdSQL.concat(String.valueOf(listId.get(i)));
            listIdSQL=listIdSQL.concat(",");
        }
        listIdSQL=listIdSQL.substring(0, listIdSQL.length() - 1);
        Query q = entityManager.createNativeQuery(
                "SELECT * FROM POSTS WHERE ID IN ("+listIdSQL+") ORDER BY FIELD(id,"+listIdSQL+");");
        return q.getResultList();

    }

}
