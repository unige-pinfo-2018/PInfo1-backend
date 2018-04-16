package ch.unihub.business.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.ws.rs.core.Response;

import ch.unihub.dom.Curriculum;
import ch.unihub.dom.Role;
import ch.unihub.utils.PasswordEncrypter;
import com.google.gson.Gson;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unihub.dom.User;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceRsTest {

	private static final Gson gson = new Gson();

	@Deployment
	public static WebArchive create() {
		File[] libs = Maven.resolver()
				.loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve()
				.withTransitivity().as(File.class);
		File[] gsonLib = Maven.resolver().loadPomFromFile("pom.xml")
				.importTestDependencies().resolve().withTransitivity().asFile();
		return ShrinkWrap.create(WebArchive.class, "unihub-integration-test-user.war")
				.addPackages(true, "ch.unihub")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/main/webapp/WEB-INF", "/web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"), "shiro.ini")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsLibraries(libs)
				.addAsLibraries(gsonLib);
	}

	@Inject
	private UserServiceRs service;

    @Test  
    public void t00_testEntityManagerInjected() {
        assertNotNull(service);
    }

	@Test
	public void t01_shouldAddUserWithMalformedRequestReturn400() throws URISyntaxException {
    	// No password
    	User user = new User();
    	user.setUsername("username");
    	user.setEmail("email@hotmail.com");
    	Assert.assertEquals(400, service.addUser(user).getStatus());
    	// No email
    	user = new User();
    	user.setUsername("username");
    	user.setPassword("password");
    	Assert.assertEquals(400, service.addUser(user).getStatus());
    	// No username
		user = new User();
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(400, service.addUser(user).getStatus());
	}

	@Test
	public void t02_shouldAddUserReturn200() throws URISyntaxException {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(201, service.addUser(user).getStatus());
	}

	@Test
	public void t03_shouldDeleteUserByUsername204() {
		Assert.assertEquals(204, service.deleteUser("username").getStatus());
	}

	@Test
	public void t04_shouldNbUserReturnAStringWith_nbUser_() {
		String result = service.getNbUsers();
		Assert.assertTrue(result.contains("nbUsers"));
	}
	
	@Test
	public void t05_shouldNbUserReturnTheCurrentNbOfUser() throws URISyntaxException {
		final String[] usernames = new String[]{"Arthur", "Kilian", "Théo", "Martinet", "Martinou", "Martin"};
		final String password = "password";
		final String[] emails = new String[]{"arthur@epfl.ch", "kilian@epfl.ch", "theo@epfl.ch",
		"martinet@epfl.ch", "martinou@epfl.ch", "martin@epfl.ch"};
		for (int i = 0; i < usernames.length; i++) {
			final User user = new User();
			user.setUsername(usernames[i]);
			user.setPassword(password);
			user.setEmail(emails[i]);
			service.addUser(user);
		}
		String result = service.getNbUsers();
		Map parsed = gson.fromJson(result, Map.class);
		Assert.assertEquals(new Integer(usernames.length), Integer.valueOf(parsed.get("nbUsers").toString()));
	}
	
	@Test
	public void t06_shouldAddSaveAllFieldsAndEncryptPassword() throws URISyntaxException {
    	final String username = "JaneDoe";
    	final String password = "weakpassword";
    	final String email = "mail@email.com";
    	final Role role = Role.USER;
    	final Curriculum curriculum = Curriculum.DOCTOR;
    	final User user = new User(username, password, email, role, curriculum, null);
		service.addUser(user);
		Response result = service.getUser(user.getId());
		Assert.assertEquals(200, result.getStatus());
		final User savedUser = (User)result.getEntity();
		Assert.assertEquals(username, savedUser.getUsername());
		// Password should be encrypted
		Assert.assertNotEquals(password, savedUser.getPassword());
		Assert.assertEquals(email, savedUser.getEmail());
		Assert.assertEquals(role, savedUser.getRole());
		Assert.assertEquals(curriculum, savedUser.getCurriculum());
		// Should be unconfirmed
		Assert.assertEquals(false, savedUser.isConfirmed());
	}
	
	@Test
	public void t07_shouldUpdateUserReturn200() throws URISyntaxException {
    	User s = new User();
    	s.setUsername("Laurent");
    	s.setPassword("password");
    	s.setEmail("email@epfl.com");
    	service.addUser(s);
		Response result = service.getUser(s.getUsername());
		s = (User) result.getEntity();
		final String updatedUsername = "William";
		s.setUsername(updatedUsername);
		service.updateUser(s);
		result = service.getUser(s.getId());
		Assert.assertEquals(200, result.getStatus());
		Assert.assertEquals(updatedUsername, ((User) result.getEntity()).getUsername());
	}

	@Test
	public void t08_shouldGetUserByEmailReturn200() throws URISyntaxException {
    	final String email = "arthur@epfl.ch";
    	final User user = new User();
    	user.setEmail(email);
    	Assert.assertEquals(200, service.getUserByEmail(email).getStatus());
	}

	@Test
	public void t09_shouldGetUserByUsernameReturn200() throws URISyntaxException {
    	final User user = new User();
    	user.setUsername(UUID.randomUUID().toString().substring(0, 20));
    	user.setEmail("fakeemail@epfl.ch");
    	user.setPassword("randompassword");
    	service.addUser(user);
    	Assert.assertEquals(200, service.getUser(user.getUsername()).getStatus());
	}

	@Test
	public void t10_shouldGetUserByIdReturn200() throws URISyntaxException {
    	User user = new User(
    			UUID.randomUUID().toString().substring(0,20),
				"password",
				"arthur.deschamps1208@hotmail.com",
				null,
				null,
				false
		);
    	service.addUser(user);
    	Assert.assertEquals(200, service.getUser(user.getId()).getStatus());
	}
}
