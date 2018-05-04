package ch.unihub.business.service;

/**
 * A message sent to our ws notification service.
 * @author Arthur Deschamps
 */
public class NotificationServiceMessage {

    /**
     * Every type of message to possibly receive.
     */
    public enum MessageType {
        GET, CREATE, UPDATE, DELETE
    }

    /**
     * Fields contained in the body of a CREATE message.
     */
    public static class CreateBody {
        private String recipient;
        private String content;

        public CreateBody(String recipient, String content) {
            this.recipient = recipient;
            this.content = content;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private MessageType messageType;
    private String body;

    public NotificationServiceMessage(MessageType messageType, String body) {
        this.messageType = messageType;
        this.body = body;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
