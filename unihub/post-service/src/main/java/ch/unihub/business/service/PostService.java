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
	
	//add post 
	void addPost(@NotNull final Post post);

	//get the next id
	public Long getNextPostId();

	//update post
	Optional<Post> updatePost(final Post updatedPost);



}
