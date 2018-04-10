package ch.unihub.dom;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Arthur Deschamps
 */
@Entity
@Table(name = "HASHES")
public class Hash implements Serializable {

    /** The unique id in a technical sense. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "USER_ID")
    private User user;

    @NotNull
    @Size(max = 1000)
    @Column(name = "HASH")
    private String hash;

    @NotNull
    @Size(max = 100)
    @Column(name = "SALT")
    private String salt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
