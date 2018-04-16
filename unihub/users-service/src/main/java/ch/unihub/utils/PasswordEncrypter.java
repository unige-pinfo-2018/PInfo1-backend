package ch.unihub.utils;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

public class PasswordEncrypter {
    private static PasswordService passwordService = new DefaultPasswordService();

    /**
     * Encrypts a password.
     * @param plainPassword A password in clear.
     * @return The hashed password.
     */
    public static String encryptPassword(String plainPassword) {
        return passwordService.encryptPassword(plainPassword);
    }
}
