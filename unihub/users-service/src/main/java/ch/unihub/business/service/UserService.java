package ch.unihub.business.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;

import ch.unihub.dom.User;

@Local
public interface UserService extends Serializable {
	List<User> getAll();
	
	int getNbUsers();
}
