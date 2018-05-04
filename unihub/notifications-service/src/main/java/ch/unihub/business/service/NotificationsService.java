package ch.unihub.business.service;

import ch.unihub.dom.Notification;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.UnsupportedDataTypeException;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Stateful
@ServerEndpoint(value = "/notifications/{username}/{wsSessionId}")
public class NotificationsService {

    final static HashMap<String, String> usernamesWithSessionIds = new HashMap<>();

    private Logger logger = LoggerFactory.getLogger(NotificationsService.class);

    private static final Gson gson = new Gson();

    private Session session;
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> users = new HashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("username") String username,
                       @PathParam("wsSessionId") String websocketSessionId) throws IOException
    {
        // Only accepts connections with right credentials
        if (usernamesWithSessionIds.containsKey(username) &&
                usernamesWithSessionIds.get(username).equals(websocketSessionId)) {
            // Get session and WebSocket connection
            this.session = session;
            sessions.add(session);
            users.put(session.getId(), username);
        } else {
            logger.info(usernamesWithSessionIds.toString());
            session.getBasicRemote().sendText("Unauthorized");
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
        final String username = users.get(session.getId());
        NotificationServiceMessage parsedMessage =
                gson.fromJson(message, NotificationServiceMessage.class);
        final String body = parsedMessage.getBody();
        switch (parsedMessage.getMessageType()) {
            case GET:
                final RemoteEndpoint.Basic remote = session.getBasicRemote();
                handleGetRequest(username, body, remote);
                break;
            case UPDATE:
                handleUpdateRequest(username, body);
                break;
            case CREATE:
                handleCreateRequest(body);
                break;
            case DELETE:
                handleDeleteRequest(username, body);
                break;
            default:
                throw new UnsupportedDataTypeException("Message type not recognized");
        }
    }

    @OnClose
    public void onClose(Session session) {
        // WebSocket connection closes
        final String username = users.get(session.getId());
        // Removes session id
        usernamesWithSessionIds.remove(username);
        sessions.remove(session);
        users.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.error(throwable.getMessage());
        users.remove(session.getId());
    }

    /**
     * @param username The username to retrieve the notifications from.
     * @return The 5 last notifications of the associated user.
     */
    private List getLastNotifications(final String username) {
        return getAllNotificationsQuery(username).setMaxResults(5).getResultList();
    }

    /**
     * @param username The username to retrieve the notifications from.
     * @return All the user's notifications.
     */
    private List getAllNotifications(final String username) {
        return getAllNotificationsQuery(username).getResultList();
    }

    /**
     * @param username The username of the user to retrieve the notifications of.
     * @param from Index of the first result to retrieve.
     * @param to Index of the last result to retrieve.
     * @return A list of notifications from {@code from} to {@code to}.
     */
    private List getNotifications(final String username, final int from, final int to) {
        return getAllNotificationsQuery(username).setFirstResult(from).setMaxResults(to).getResultList();
    }

    private Query getAllNotificationsQuery(final String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Notification> getNotifications = cb.createQuery(Notification.class);

        Root<Notification> root = getNotifications.from(Notification.class);
        getNotifications.where(cb.equal(root.get("username"), username));
        return entityManager.createQuery(getNotifications);
    }

    /**
     * Sets all notifications' "read" field to true from the given user.
     * @param username The username to modify the notifications of.
     */
    private void setAllRead(final String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Notification> update = cb.createCriteriaUpdate(Notification.class);

        Root<Notification> root = update.from(Notification.class);
        update.where(cb.equal(root.get("username"), username)).where(cb.isFalse(root.get("isRead")));
        update.set(root.get("isRead"), true);
        entityManager.createQuery(update).executeUpdate();
    }

    /**
     * Creates a notification and saves it to the database.
     * @param recipient The notification's recipient.
     * @param content The content/message of the notification.
     * @return The freshly created notification.
     */
    @Transactional
    private Notification createNotification(final String recipient, final String content) {
        // Creates a notification
        Notification notification = new Notification(recipient, content);
        // Saves it in database
        entityManager.persist(notification);
        return notification;
    }

    /**
     *
     * @param username The username of the user to find the {@code NotificationService} of.
     * @return The corresponding {@code NotificationService}.
     */
    private Optional<Session> findNotificationServiceForUser(final String username) {
        String id = null;
        // First find the user's corresponding session id
        for(HashMap.Entry<String, String> entry : users.entrySet()) {
            String sessionId = entry.getKey();
            String sessionUsername = entry.getValue();
            if (username.equals(sessionUsername)) id = sessionId;
        }
        if (id == null) return Optional.empty();
        final String finalId = id;
        return sessions
                .stream()
                .filter(session -> session.getId().equals(finalId))
                .findFirst();
    }

    /**
     * Deletes all the notifications associated with the given user.
     * @param username The user's username.
     */
    private void deleteAllNotifications(final String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Notification> deleteQuery = cb.createCriteriaDelete(Notification.class);

        Root<Notification> root = deleteQuery.from(Notification.class);
        deleteQuery.where(cb.equal(root.get("username"), username));
        entityManager.createQuery(deleteQuery).executeUpdate();
    }

    private void handleGetRequest(final String username, String body, final RemoteEndpoint.Basic remote) throws IOException {
        switch (body) {
            case "last":
                remote.sendText(gson.toJson(getLastNotifications(username)));
                break;
            case "all":
                remote.sendText(gson.toJson(getAllNotifications(username)));
                break;
            default:
                // Removes spaces
                body = body.replaceAll("\\s+","");
                // Verifies if we have a range query
                if ((body.length() > 5) && body.contains("-") && body.substring(0, 5).equals("range")) {
                    // Gets rid of the initial query name
                    body = body.substring(5);
                    // Tries to extract the range
                    String[] bounds = body.split("-");
                    try {
                        final int from = Integer.valueOf(bounds[0]);
                        final int to = Integer.valueOf(bounds[1]);
                        remote.sendText(gson.toJson(getNotifications(username, from, to)));
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage());
                        throw new IllegalArgumentException("Bad request formatting. Can't extract range.");
                    }
                    return;
                }
                throw new IllegalArgumentException("GET request with body '" + body + "' can't be handled");
        }
    }

    private void handleUpdateRequest(final String username, final String body) {
        switch (body) {
            case "read":
                setAllRead(username);
                break;
            default:
                throw new IllegalArgumentException("UPDATE message with body '" + body +
                        "' can't be handled");
        }
    }

    private void handleCreateRequest(final String body) {
        NotificationServiceMessage.CreateBody notificationBody =
                gson.fromJson(body, NotificationServiceMessage.CreateBody.class);
        final String recipient = notificationBody.getRecipient();
        final Notification createdNotification = createNotification(recipient, notificationBody.getContent());
        // If the recipient is currently connected, it will receive a notification right away
        findNotificationServiceForUser(recipient).ifPresent(recipientSession -> {
            try {
                recipientSession.getBasicRemote().sendText(gson.toJson(createdNotification));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
    }

    private void handleDeleteRequest(final String username, final String body) {
        switch (body) {
            case "all":
                deleteAllNotifications(username);
                break;
            default:
                throw new IllegalArgumentException("DELETE message with body '" + body + "' can't be handled.");
        }
    }
}
