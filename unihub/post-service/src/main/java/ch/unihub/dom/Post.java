package ch.unihub.dom;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "POSTS")
public class Post implements Serializable {

	private static final long serialVersionUID = -6146935825517747043L;
	
	/** The unique id in a technical sense. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	/** The creater of the post */
	@NotNull
	@Column(name = "USERID")
	private Long userId;
	
	/** The parenting post */
	@Column(name = "PARENTID")
	private Long parentId;

	/** Replying to */
	@Column(name = "REPLYTOID")
	private Long replyToId;

	/** The user content */
	@NotNull
	@Size(min = 2, max = 800)
	@Column(name = "CONTENT")
	private String content;

    /** The date of the post */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "DATEPOST")
    private Date datePost;

	public Post() {
	}
	public Post(Long userId, String content) {
		this.setUserId(userId);
		this.setContent(content);
	}
	public Post(Long userId, Long parentId, String content) {
		this.setUserId(userId);
		this.setParentId(parentId);
		this.setContent(content);
	}

	// Constructor used fto test date
	public Post(Long userId, String content, Date datePost) {
		this.setUserId(userId);
		this.setContent(content);
		this.setDatePost(datePost);
	}
	
	// Constructor used to test comments
	public Post(Long userId, Long replyToId, String content) {
		this.setUserId(userId);
		this.setReplyToId(replyToId);
		this.setContent(content);
	}
	
	
	public void copyFields(final Post post) {
		if (post.getUserId() != null)
			this.setUserId(post.getUserId());

		if (post.getParentId() != null)
			this.setParentId(post.getParentId());

		if (post.getReplyToId() != null)
			this.setReplyToId(post.getReplyToId());

		if (post.getContent() != null)
			this.setContent(post.getContent());

        if (post.getDatePost() != null)
            this.setDatePost(post.getDatePost());
	}

	public final Long getId() {
		return id;
	}

	public final void setId(Long id) {
		this.id = id;
	}
	
	public final Long getUserId() {
		return userId;
	}

	public final void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public final Long getParentId() {
		return parentId;
	}

	public final void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public final Long getReplyToId() {
		return replyToId;
	}

	public final void setReplyToId(Long replyToId) {
		this.replyToId = replyToId;
	}
	
	public final String getContent() {
		return content;
	}

	public final void setContent(String content) {
		this.content = content;
	}

    public final Date getDatePost() {
        return datePost;
    }

    public final void setDatePost(Date datePost) {
        this.datePost = datePost;
    }
}
