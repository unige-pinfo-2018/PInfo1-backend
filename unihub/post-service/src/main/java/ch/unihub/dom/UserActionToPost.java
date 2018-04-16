package ch.unihub.dom;

import java.io.Serializable;

/**
 * not use actually but could be parent of dislike and like
 */
public class UserActionToPost implements Serializable {

    private static final long serialVersionUID = -6146935825517747043L;

    /** The unique id in a technical sense. */
    private Long id;

    /** The creater of the post */
    private Long userId;

    /** The postId of the like */
    private Long postId;

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

    public final Long getPostId() {
        return postId;
    }

    public final void setPostId(Long postId) {
        this.postId = postId;
    }

}