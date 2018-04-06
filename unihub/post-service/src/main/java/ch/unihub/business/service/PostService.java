package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.Optional;

import ch.unihub.dom.Post;

@Local
public interface PostService extends Serializable {

	//get all post
	List<Post> getAll();

	//get the number of post
	int getNbPosts();

	//get a post with the id
	Optional<Post> getPost(final Long id);

	//get a userId of post with the id
	Long getUserIdPost(final Long id);

	//get a ParentId of post with the id
	Long getParentIdPost(final Long id);

	//get a ReplyToId of post with the id
	Long getReplyToIdPost(final Long id);

	//add post 
	void addPost(@NotNull final Post post);

	//get the next id
	public Long getNextPostId();

	//update post
	Optional<Post> updatePost(final Post updatedPost);



}
