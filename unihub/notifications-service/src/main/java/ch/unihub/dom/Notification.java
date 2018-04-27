package ch.unihub.dom;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "NOTIFICATIONS")
public class Notification implements Serializable {
    private static final long serialVersionUID = 463065342540595934L;

    public Notification() {
        // DO NOT USE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME")
    @Size(max = 35)
    @NotNull
    private String username;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "DATE_CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "IS_READ")
    private Boolean isRead;

    public Notification(String username, String content) {
        this.id = null;
        this.username = username;
        this.content = content;
        this.dateCreated = new Date();
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        this.isRead = read;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
