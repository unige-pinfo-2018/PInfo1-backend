package ch.unihub.business.service;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

@Stateless
public class EmailSender {

    @Asynchronous
    public void sendRegistrationMail(final String emailAddress) {

    }
}
