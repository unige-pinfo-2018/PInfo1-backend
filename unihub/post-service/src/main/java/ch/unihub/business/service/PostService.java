package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import ch.unihub.dom.Post;

@Local
public interface PostService extends Serializable {
	List<Post> getAll();
	int getNbPosts();
}
