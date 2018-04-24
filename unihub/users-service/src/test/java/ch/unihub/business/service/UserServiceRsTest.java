package ch.unihub.business.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import ch.unihub.dom.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.junit.runner.RunWith;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceRsTest {

	private static final Logger logger = LogManager.getLogger(UserServiceRsTest.class);

	@Deployment
	public static WebArchive create() {
		logger.trace("Entering users service integration testing");

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
	private UserServiceRs restService;
	@Inject
	private UserService userService;
	private static final Gson gson = new Gson();

	private class UsernameAndPassword {
		public String username;
		public String password;

		public UsernameAndPassword(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}

	private class EmailAndRequestIdAndPassword {
		public String email;
		public String id;
		public String password;

		public EmailAndRequestIdAndPassword(String email, String id, String password) {
			this.email = email;
			this.id = id;
			this.password = password;
		}
	}

	private class Ids {
		public long[] ids;
		public Ids(long[] ids) {
			this.ids = ids;
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
	private final User fakeUser = new User(fakeUsername, fakePassword, fakeEmail, fakeRole, fakeCurriculum,
			false);
	private final String fakeUsernameAuthenticated = "fakeusernameauthenticated";
	private final String fakeEmailAuthenticated = "fakeemailauthenticated@epfl.ch";
	private final User fakeUserAuthenticated = new User(fakeUsernameAuthenticated, fakePassword, fakeEmailAuthenticated,
			fakeRole, fakeCurriculum, true);

	@Before
	public void beforeTest() throws URISyntaxException {
		restService.addUser(fakeUser);
		restService.addUser(fakeUserAuthenticated);
		fakeUserAuthenticated.setIsConfirmed(true);
		restService.updateUser(fakeUserAuthenticated);
	}

	@After
	public void afterTest() {
		try {
			restService.deleteUser(fakeUsername);
			restService.deleteUser(fakeUsernameAuthenticated);
		} catch (NotFoundException | EJBException ignored) {}

	}

	private static String generateRandomString() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

    @Test  
    public void t00_testEntityManagerInjected() {
		logger.trace("yooooooooooo");
        assertNotNull(restService);
    }

	@Test
	public void t01_shouldAddUserWithMalformedRequestReturn400() throws URISyntaxException {
    	// No password
    	User user = new User();
    	user.setUsername("username");
    	user.setEmail("email@hotmail.com");
    	Assert.assertEquals(BAD_REQUEST, restService.addUser(user).getStatus());
    	// No email
    	user = new User();
    	user.setUsername("username");
    	user.setPassword("password");
    	Assert.assertEquals(BAD_REQUEST, restService.addUser(user).getStatus());
    	// No username
		user = new User();
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(BAD_REQUEST, restService.addUser(user).getStatus());
	}

	@Test
	public void t02_shouldAddUserReturn200() throws URISyntaxException {
		User user = new User();
		user.setUsername("username");
		user.setEmail("email@hotmail.com");
		user.setPassword("password");
		Assert.assertEquals(CREATED, restService.addUser(user).getStatus());
		restService.deleteUser(user.getUsername());
	}

	@Test
	public void t03_shouldDeleteUserReturn204() throws URISyntaxException {
		// Delete by username
		Assert.assertEquals(NO_CONTENT, restService.deleteUser(fakeUser.getUsername()).getStatus());
		restService.addUser(fakeUser);
		// Delete by id
		Assert.assertEquals(NO_CONTENT, restService.deleteUser(fakeUser.getId()).getStatus());
	}

	@Test
	public void t04_shouldNbUserReturnAStringWith_nbUser_() {
		Assert.assertTrue(restService.getNbUsers().contains("nbUsers"));
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
			restService.addUser(user);
		}
		String result = restService.getNbUsers();
		Map parsed = gson.fromJson(result, Map.class);
		// + 2 for the fake users that are created before every test
		Assert.assertEquals(new Integer(usernames.length + 2), Integer.valueOf(parsed.get("nbUsers").toString()));
	}
	
	@Test
	public void t06_shouldAddSaveAllFieldsAndEncryptPassword() {
		Response result = restService.getUser(fakeUser.getId());
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
		restService.updateUser(fakeUser);
		Response result = restService.getUser(fakeUser.getId());
		Assert.assertEquals(OK, result.getStatus());
		Assert.assertEquals(updatedUsername, ((User) result.getEntity()).getUsername());
	}

	@Test
	public void t08_shouldGetUserByEmailReturn200() {
    	Assert.assertEquals(OK, restService.getUserByEmail(fakeEmail).getStatus());
	}

	@Test
	public void t09_shouldGetUserByUsernameReturn200() {
    	Assert.assertEquals(OK, restService.getUser(fakeUsername).getStatus());
	}

	@Test
	public void t10_shouldGetUserByIdReturn200() {
    	Assert.assertEquals(OK, restService.getUser(fakeUser.getId()).getStatus());
	}

	@Test
	public void t11_shouldGetUserOnNonExistingUserReturn404() {
    	Assert.assertEquals(
    			NOT_FOUND,
				restService.getUserByEmail(generateRandomString() + "@epfl.ch").getStatus()
		);
    	Assert.assertEquals(
    			NOT_FOUND,
				restService.getUser(generateRandomString()).getStatus()
		);
    	long fakeId = (long) 1e3;
    	try {
    		restService.deleteUser(fakeId);
		} catch (EJBException ignored){}
    	Assert.assertEquals(
    			NOT_FOUND,
				restService.getUser(fakeId).getStatus()
		);
	}

	@Test
	public void t12_loginShouldSucceedForExistingUser() {
    	Assert.assertEquals(
    			OK,
				restService.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, fakePassword))).getStatus()
		);
	}

	@Test
	public void t13_loginShouldFailWithWrongUsername() {
    	Assert.assertEquals(
    			UNAUTHORIZED,
				restService.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated + "z", fakePassword))).getStatus()
		);
	}

	@Test
	public void t14_loginShouldFailWithWrongPassword() {
		Assert.assertEquals(
				UNAUTHORIZED,
				restService.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, fakePassword + "z"))).getStatus()
		);
	}

	@Test
	public void t15_loginShouldFailWithEmail() {
		Assert.assertEquals(
				UNAUTHORIZED,
				restService.login(gson.toJson(new UsernameAndPassword(fakeEmailAuthenticated, fakePassword))).getStatus()
		);
	}

	@Test
	public void t16_shouldBeLoggedInAfterLogin() {
		Assert.assertEquals(
				OK,
				restService.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, fakePassword))).getStatus()
		);
		// tries two times
		Response response = restService.isLoggedIn();
		Assert.assertEquals(OK, response.getStatus());
		Object[] isLoggedInAndUser = (Object[]) response.getEntity();
		Assert.assertEquals(2, isLoggedInAndUser.length);
		Assert.assertTrue((boolean) isLoggedInAndUser[0]);
		Assert.assertEquals(fakeUserAuthenticated.getUsername(), ((User) isLoggedInAndUser[1]).getUsername());
	}

	@Test
	public void t17_shouldConfirmUserWithRightIdAndEmailWork() {
		List<AccountConfirmation> confirmations = userService.findAccountConfirmations(fakeEmail);
		assertTrue(confirmations.size() > 0);
		AccountConfirmation confirmation = confirmations.get(0);
		Assert.assertEquals(OK, restService.confirmUser(fakeEmail, confirmation.getConfirmationId()).getStatus());
		Assert.assertTrue(userService.findAccountConfirmations(fakeEmail).isEmpty());
		final Optional<User> user = userService.getUserByEmail(fakeEmail);
		Assert.assertTrue(user.isPresent());
		Assert.assertTrue(user.get().isConfirmed());
	}

	@Test
	public void t18_shouldResetPasswordWithRightIdAndEmailWork() {
		restService.requestPasswordReset(fakeEmailAuthenticated);
		List<ResetPasswordRequest> requests = userService.findResetPasswordRequests(fakeEmailAuthenticated);
		Assert.assertTrue(requests.size() > 0);
		ResetPasswordRequest request = requests.get(0);
		final String newPassword = "mynewpassword";
		Assert.assertEquals(OK, restService.resetPassword(
				gson.toJson(new EmailAndRequestIdAndPassword(
						fakeEmailAuthenticated,
						request.getRequestId(),
						newPassword
				))
		).getStatus());
		Assert.assertTrue(userService.findResetPasswordRequests(fakeEmailAuthenticated).isEmpty());
		Assert.assertEquals(
				OK,
				restService.login(gson.toJson(new UsernameAndPassword(fakeUsernameAuthenticated, newPassword))).getStatus()
		);
	}

	@Test
	public void t19_shouldGetUsersByIdsReturnMultipleUsers() {
		long[] ids = new long[] {fakeUser.getId(), fakeUserAuthenticated.getId()};
		Response response = restService.getUsersByIds(gson.toJson(new Ids(ids)));
		Assert.assertEquals(OK, response.getStatus());
		User[] users = gson.fromJson(gson.toJson(response.getEntity()), User[].class);
		Assert.assertEquals(2, users.length);
	}

	@Test
	public void t20_shouldIsLoggedInReturnFalseWhenUserIsLoggedOut() {
		Assert.assertFalse((boolean) ((Object[]) restService.isLoggedIn().getEntity())[0]);
	}
}
