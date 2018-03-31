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
	@Size(min = 2, max = 35)
	@Column(name = "USERID")
	private Long userId;
	
	/** The parenting post */
	@NotNull
	@Size(min = 2, max = 35)
	@Column(name = "PARENTID")
	private Long parentId;

	/** The creater of the post */
	@NotNull
	@Size(min = 2, max = 35)
	@Column(name = "REPLYTOID")
	private Long replyToId;
	
	
	/** The user content */
	@NotNull
	@Size(min = 0, max = 400)
	@Column(name = "CONTENT")
	private String content;
	
		

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
}
