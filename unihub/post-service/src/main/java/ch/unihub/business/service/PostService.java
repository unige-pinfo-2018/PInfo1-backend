package ch.unihub.business.service;

import ch.unihub.dom.Dislike;
import ch.unihub.dom.Like;
import ch.unihub.dom.Post;
import ch.unihub.dom.Tag;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Local
public interface PostService extends Serializable {

	//get all post
	List<Post> getAll();

	//id of all tags with this idPost
	List<Long> getListIdTags(Long idPost);

	//get the number of post
	int getNbPosts();

	//get a post with the id
	Optional<Post> getPost(final Long id);

    //get a like with the id
    Optional<Like> getLike(final Long value, String columnId);

    //get a dislike with the id
    Optional<Dislike> getDislike(final Long value, String columnId);

    //get a dislike with the id
    Optional<Tag> getTag(final String value, String columnId);

	//get a userId of post with the id
	Long getUserIdPost(final Long id);

	//get a ParentId of post with the id
	Long getParentIdPost(final Long id);

	//get a ReplyToId of post with the id
	Long getReplyToIdPost(final Long id);

    //get a ReplyToId of post with the id
    Long getNbUpvotes(final Long idPost);

	//get date of post with the id
	Date getDate(final Long idPost);

	//get text of post with the id
	String getContent(final Long idPost);
	//add post 
	Long addPost(@NotNull final Post post);

	//Long addPostAndTag(Long userId, String content,String name,Long parentId);

    //add like
    void addLike(@NotNull final Like like);

    //add dislike
    void addDislike(@NotNull final Dislike dislike);

    //add tag
    void addTag(@NotNull final Tag tag);

    void addTags(Long postId, List<String> lisName);

	//update post
	Optional<Post> updatePost(final Post updatedPost);

	List searchPost(String questionUser, int nbPost, List<String> listTags);

	List getPostsByIds(List<Long> listId);

    List<Post> getPostsOfUser(Long idUser);

    //getCommentsByID : retourne tous les comments pour un id de post donné (donc un select * where parentID = id)
    List getCommentsByQuestionID(List<Long> parentId);

    List getPostsAndCommentsByTags(String questionUser, int nbPost, List<String> listTags);

	List getPostsAndCommentsByIds(List<Long> listIds);

	void addSQLForSearch();

	List getSeveralPosts();

	Map getLikeDislikeOfPostsFromUser(Long postId,Long userId);


}
