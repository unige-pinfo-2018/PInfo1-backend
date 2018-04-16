package ch.unihub.dom;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Arthur Deschamps
 */
@Entity
@Table(name = "ACCOUNT_CONFIRMATIONS")
public class AccountConfirmation implements Serializable {

    private static final long serialVersionUID = 5922499249530705338L;

    public AccountConfirmation() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "USER_EMAIL")
    private String userEmail;

    @NotNull
    @Size(min = 36, max = 50)
    @Column(name = "CONFIRMATION_ID")
    private String confirmationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
