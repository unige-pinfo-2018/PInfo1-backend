package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.Optional;

import ch.unihub.dom.Dislike;
import ch.unihub.dom.Like;
import ch.unihub.dom.Post;
import ch.unihub.dom.Tag;

@Local
public interface PostService extends Serializable {

	//get all post
	List<Post> getAll();

	//get the number of post
	int getNbPosts();

	//get a post with the id
	Optional<Post> getPost(final Long id);

    //get a like with the id
    Optional<Like> getLike(final Long id);

    //get a dislike with the id
    Optional<Dislike> getDislike(final Long id);

    //get a dislike with the id
    Optional<Tag> getTag(final Long id);

	//get a userId of post with the id
	Long getUserIdPost(final Long id);

	//get a ParentId of post with the id
	Long getParentIdPost(final Long id);

	//get a ReplyToId of post with the id
	Long getReplyToIdPost(final Long id);

    //get a ReplyToId of post with the id
    Long getNbUpvotes(final Long idPost);

	//add post 
	void addPost(@NotNull final Post post);

    //add like
    void addLike(@NotNull final Like like);

    //add dislike
    void addDislike(@NotNull final Dislike dislike);

    //add tag
    void addTag(@NotNull final Tag tag);

	//get the next id
	public Long getNextPostId();

	//update post
	Optional<Post> updatePost(final Post updatedPost);



}
