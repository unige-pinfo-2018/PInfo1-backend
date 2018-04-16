package ch.unihub.business.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.ws.rs.NotFoundException;
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
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.junit.runner.RunWith;

import ch.unihub.dom.User;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceRsTest {

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

	private static final Gson gson = new Gson();

	private class UsernameAndPassword {
		public String username;
		public String password;

		public UsernameAndPassword(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}

	private final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();
	private final int NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();
	private final int CREATED = Response.Status.CREATED.getStatusCode();
	private final int NO_CONTENT = Response.Status.NO_CONTENT.getStatusCode();
	private final int OK = Response.Status.OK.getStatusCode();
	private final int UNAUTHORIZED = Response.Status.UNAUTHORIZED.getStatusCode();

	private final String fakeUsername = "fakeusername";
	private final String fakePassword = "fakepassword";
	private final String fakeEmail = "fakeemail@epfl.ch";
	private final Role fakeRole = Role.USER;
	private final Curriculum fakeCurriculum = Curriculum.BACHELOR_STUDENT;
	private final boolean isFakeUserConfirmed = false;
	private final User fakeUser = new User(fakeUsername, fakePassword, fakeEmail, fakeRole, fakeCurriculum,
			isFakeUserConfirmed);
	private final String fakeUsernameAuthenticated = "fakeusernameauthenticated";
	private final String fakeEmailAuthenticated = "fakeemailauthenticated@epfl.ch";
	private final User fakeUserAuthenticated = new User(fakeUsernameAuthenticated, fakePassword, fakeEmailAuthenticated,
			fakeRole, fakeCurriculum, true);

	@Before
	public void beforeTest() throws URISyntaxException {
		service.addUser(fakeUser);
		service.addUser(fakeUserAuthenticated);
		fakeUserAuthenticated.setIsConfirmed(true);
		service.updateUser(fakeUserAuthenticated);
	}

	@After
	public void afterTest() {
		try {
			service.deleteUser(fakeUsername);
			service.deleteUser(fakeUsernameAuthenticated);
		} catch (NotFoundException | EJBException ignored) {}

	}

	private static String generateRandomString() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

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
    	Assert.assertEquals(BAD_REQUEST, service.addUser(user).getStatus());
    	// No email
    	user = new User();
    	user.setUsername("username");
    	user.setPassword("password");
    	Assert.assertEquals(BAD_REQUEST, service.addUser(user).getStatus());
    	// No username
		user = new User();
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(BAD_REQUEST, service.addUser(user).getStatus());
	}

	@Test
	public void t02_shouldAddUserReturn200() throws URISyntaxException {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(CREATED, service.addUser(user).getStatus());
		service.deleteUser(user.getUsername());
	}

	@Test
	public void t03_shouldDeleteUserReturn204() throws URISyntaxException {
		// Delete by username
		Assert.assertEquals(NO_CONTENT, service.deleteUser(fakeUser.getUsername()).getStatus());
		service.addUser(fakeUser);
		// Delete by id
		Assert.assertEquals(NO_CONTENT, service.deleteUser(fakeUser.getId()).getStatus());
	}

	@Test
	public void t04_shouldNbUserReturnAStringWith_nbUser_() {
		Assert.assertTrue(service.getNbUsers().contains("nbUsers"));
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
		// + 2 for the fake users that are created before every test
		Assert.assertEquals(new Integer(usernames.length + 2), Integer.valueOf(parsed.get("nbUsers").toString()));
	}
	
	@Test
	public void t06_shouldAddSaveAllFieldsAndEncryptPassword() {
		Response result = service.getUser(fakeUser.getId());
		Assert.assertEquals(OK, result.getStatus());
		final User savedUser = (User)result.getEntity();
		Assert.assertEquals(fakeUser.getUsername(), savedUser.getUsername());
		// Password should be encrypted
		Assert.assertNotEquals(fakeUser.getPassword(), savedUser.getPassword());
		Assert.assertEquals(fakeUser.getEmail(), savedUser.getEmail());
		Assert.assertEquals(fakeUser.getRole(), savedUser.getRole());
		Assert.assertEquals(fakeUser.getCurriculum(), savedUser.getCurriculum());
		// Should be unconfirmed
		Assert.assertEquals(false, savedUser.isConfirmed());
	}
	
	@Test
	public void t07_shouldUpdateUserReturn200() {
		final String updatedUsername = "William";
		fakeUser.setUsername(updatedUsername);
		service.updateUser(fakeUser);
		Response result = service.getUser(fakeUser.getId());
		Assert.assertEquals(OK, result.getStatus());
		Assert.assertEquals(updatedUsername, ((User) result.getEntity()).getUsername());
	}

	@Test
	public void t08_shouldGetUserByEmailReturn200() {
    	Assert.assertEquals(OK, service.getUserByEmail(fakeEmail).getStatus());
	}

	@Test
	public void t09_shouldGetUserByUsernameReturn200() {
    	Assert.assertEquals(OK, service.getUser(fakeUsername).getStatus());
	}

	@Test
	public void t10_shouldGetUserByIdReturn200() {
    	Assert.assertEquals(OK, service.getUser(fakeUser.getId()).getStatus());
	}

	@Test
	public void t11_shouldGetUserOnNonExistingUserReturn404() {
    	Assert.assertEquals(
    			NOT_FOUND,
				service.getUserByEmail(generateRandomString() + "@epfl.ch").getStatus()
		);
    	Assert.assertEquals(
    			NOT_FOUND,
				service.getUser(generateRandomString()).getStatus()
		);
    	long fakeId = (long) 1e3;
    	try {
    		service.deleteUser(fakeId);
		} catch (EJBException ignored){}
    	Assert.assertEquals(
    			NOT_FOUND,
				service.getUser(fakeId).getStatus()
		);
	}

	@Test
	public void t12_loginShouldSucceedForExistingUser() {
    	Assert.assertEquals(
    			OK,
				service.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, fakePassword))).getStatus()
		);
	}

	@Test
	public void t13_loginShouldFailWithWrongUsername() {
    	Assert.assertEquals(
    			UNAUTHORIZED,
				service.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated + "z", fakePassword))).getStatus()
		);
	}

	@Test
	public void t14_loginShouldFailWithWrongPassword() {
		Assert.assertEquals(
				UNAUTHORIZED,
				service.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, fakePassword + "z"))).getStatus()
		);
	}

	@Test
	public void t15_loginShouldFailWithEmail() {
		Assert.assertEquals(
				UNAUTHORIZED,
				service.login(gson.toJson(new UsernameAndPassword(fakeEmailAuthenticated, fakePassword))).getStatus()
		);
	}

	@Test
	public void t16_shouldBeLoggedInAfterLogin() {
		service.login(gson.toJson(new UsernameAndPassword(fakeEmailAuthenticated, fakePassword)));
		Response response = service.isLoggedIn();
		System.out.println(response.getEntity().toString());
	}

	@Test
	public void t17_shouldConfirmUserWithRightIdAndEmail() {
		// TODO
	}

	@Test
	public void t18_shouldResetPasswordWithRightIdAndEmail() {
		// TODO
	}
}
