package ch.unihub.business.service;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

@Stateless
public class EmailSender {

    private static final String fromAddress = "unihub@noreply.com";
    private static final String host = "smtp.gmail.com";
    private static final int tlsPort = 587;
    private static final String username = "unihuborg@gmail.com";
    private static final String password = "Ranters2018";
    private static final String from = "UniHub";

    private static final Mailer mailer = MailerBuilder
            .withSMTPServer(host, tlsPort, username, password)
            .buildMailer();

    @Asynchronous
    public void sendRegistrationMail(final String emailAddress, final String username, final String confirmationId) {
        final Email email = EmailBuilder.startingBlank()
                .from(from, fromAddress)
                .to(username, emailAddress)
                .withSubject("UniHub - Confirm your account")
                .withHTMLText("<p>Please click on the following link to confirm your account on UniHub:</p>" +
                        "<p>" + confirmationLink(emailAddress, confirmationId) + "</p>")
                .buildEmail();

        mailer.sendMail(email);
    }

    @Asynchronous
    public void sendPasswordResettingEmail(final String emailAddress,
                                           final String passwordResetRequestId) {
        final Email email = EmailBuilder.startingBlank()
                .from(from, fromAddress)
                .to(emailAddress, emailAddress)
                .withSubject("UniHub - Reset your password")
                .withHTMLText("<p>Please click on the following link to reset your account's password on UniHub</p>" +
                "<p>" + resetPasswordLink(emailAddress, passwordResetRequestId) + "</p>")
                .buildEmail();

        mailer.sendMail(email);
    }

    private String confirmationLink(final String emailAddress, final String confirmationId) {
        // TODO: make it relative somehow ????
        return "http://localhost:18080/users-service/rest/users/confirm?email=" + emailAddress +
                "&id=" + confirmationId;
    }

    private String resetPasswordLink(final String emailAddress, final String passwordResetRequestId) {
        // TODO: put url of a client page
        return "http://localhost:18080/users-service/rest/users/reset_password?email=" + emailAddress +
                "&id=" + passwordResetRequestId;
    }
}
