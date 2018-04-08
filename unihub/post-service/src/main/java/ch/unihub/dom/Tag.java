package ch.unihub.dom;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TAGS")
public class Tag implements Serializable {

    private static final long serialVersionUID = -6146935825517747043L;

    /** The unique id in a technical sense. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** The postId of the like */
    @NotNull
    @Column(name = "POSTID")
    private Long postId;

    /** The tag */
    @NotNull
    @Size(min = 2, max = 60)
    @Column(name = "NAME")
    private String name;

    public final Long getId() {
        return id;
    }

    public final void setId(Long id) {
        this.id = id;
    }

    public final Long getPostId() {
        return postId;
    }

    public final void setPostId(Long postId) {
        this.postId = postId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

}