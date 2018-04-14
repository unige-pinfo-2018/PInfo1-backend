package ch.unihub.dom;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Arthur Deschamps
 */
@Entity
@Table(name = "RESET_PASSWORD_REQUEST")
public class ResetPasswordRequest implements Serializable {

    private static final long serialVersionUID = 8189638718050542908L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotNull
    @Column(name = "USER_EMAIL")
    private String userEmail;

    @NotNull
    @Column(name ="REQUEST_ID")
    @Size(min = 36, max = 50)
    private String requestId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String emailAddress) {
        this.userEmail = emailAddress;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
