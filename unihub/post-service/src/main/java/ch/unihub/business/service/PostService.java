package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import ch.unihub.dom.Post;

@Local
public interface PostService extends Serializable {
	
	List<Post> getAll();
	
	int getNbPosts();
	
	Post getPost(Long id);
	
	//add post 
	void addPost(@NotNull final Post post);
	
	public Long getNextPostId();
}
