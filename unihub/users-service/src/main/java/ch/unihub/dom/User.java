package ch.unihub.dom;

import ch.unihub.utils.PasswordEncrypter;

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

	public User() {
	}

	public User(String username, String password, String email, Role role, Curriculum curriculum, Boolean confirmed) {
		this.username = username;
		this.setPassword(password);
		this.email = email;
		this.role = role;
		this.curriculum = curriculum;
		this.confirmed = confirmed;
	}

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

	@Size(max = 255)
	@Column(name = "NAME")
	private String name;

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

	@Column(name = "CURRICULUM")
	@Enumerated(EnumType.STRING)
	private Curriculum curriculum;

	@Size(max = 2083)
	@Column(name = "PICTURE_URL")
	private String pictureUrl;

	@Column(name = "CONFIRMED")
	private Boolean confirmed;

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
		if (user.isConfirmed() != null)
			this.setIsConfirmed(user.isConfirmed());
		if (user.getCurriculum() != null)
			this.setCurriculum(user.getCurriculum());
		if (user.getName() != null)
			this.setName(user.getName());
		if (user.getPictureUrl() != null)
			this.setPictureUrl(user.getPictureUrl());
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
		this.password = PasswordEncrypter.encryptPassword(password);
	}

	public Boolean isConfirmed() {
		return confirmed;
	}

	public void setIsConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
}
