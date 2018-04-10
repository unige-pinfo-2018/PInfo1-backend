package ch.unihub.dom;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Arthur Deschamps
 */
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

	// Still required to be able to send the password in a JSON from the client.
	// Not saved in db.
	@NotNull
	@Size(min = 2, max = 1000)
	@Column(name = "PASSWD")
	private String password;

	@NotNull
	@Column(name = "EMAIL")
	private String email;

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Role role;

	/**
	 * Updates this fields with the given {@code User} object.
	 * @param user A user object to copy the fields from.
	 */
	public void copyFields(final User user) {
		if (user.getUsername() != null)
			this.setUsername(user.getUsername());
		if (user.getEmail() != null)
			this.setEmail(user.getEmail());
		if (user.getRole() != null)
			this.setRole(user.getRole());
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
