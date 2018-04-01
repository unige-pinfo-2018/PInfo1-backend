package ch.unihub.dom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USERS")
public class User implements Serializable {

	private static final long serialVersionUID = -6146935825517747043L;
	
	/** The unique id in a technical sense. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	/** The user username */
	@NotNull
	@Size(min = 2, max = 35)
	@Column(name = "USERNAME")
	private String username;

	/** The user password */
	@NotNull
	@Size(min = 2, max = 35)
	@Column(name = "PASSWD")
	private String password;

	@NotNull
	@Column(name = "EMAIL")
	private String email;

	/**
	 * Updates this fields with the given {@code User} object.
	 * @param user A user object to copy the fields from.
	 */
	public void copyFields(final User user) {
		if (user.getUsername() != null)
			this.setUsername(user.getUsername());
		if (user.getPassword() != null)
			this.setPassword(user.getPassword());
		if (user.getEmail() != null)
			this.setEmail(user.getEmail());
	}

	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}

	public final String getUsername() {
		return username;
	}

	public final void setUsername(String username) {
		this.username = username;
	}

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
