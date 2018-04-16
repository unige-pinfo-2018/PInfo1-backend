package ch.unihub.business.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import ch.unihub.dom.Curriculum;
import ch.unihub.dom.Role;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unihub.dom.User;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceRsTest {	
	@Deployment
	public static WebArchive create() {
		
		return ShrinkWrap.create(WebArchive.class, "unihub-integration-test.war").addPackages(true, "ch.unihub")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/main/webapp/WEB-INF", "/web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
	}

	@Inject
	private UserServiceRs sut;

    @Test  
    public void t1_testEntityManagerInjected() {  
        assertNotNull(sut);  
    }  
	
	@Test
	public void t2_shouldNbUserReturnAStringWith_nbUser_() {
		String result = sut.getNbUsers();
		System.out.println(result);
		Assert.assertTrue(result.contains("nbUsers"));
	}
	
	@Test
	public void t3_shouldNbUserReturnTheCurrentNbOfUser() {
		String result = sut.getNbUsers();
		System.out.println(result);
		Assert.assertTrue(result.equals("{\"nbUsers\":\"0\"}"));
	}
	
	@Test
	public void t4_shouldAddUserReturn200() throws URISyntaxException {
    	User user = new User(
    			"JaneDoe",
				"weakpassword",
				"mail@email.com",
				Role.USER,
				Curriculum.DOCTOR,
				true
		);
		sut.addUser(user);
		Response result = sut.getUser((long) 1);
		Assert.assertEquals(200, result.getStatus());
	}
	
	@Test
	public void t5_shouldNbUserReturnTheCurrentNbOfUserAfterAdd() {
		String result = sut.getNbUsers();
		System.out.println(result);
		Assert.assertTrue(result.equals("{\"nbUsers\":\"1\"}"));
	}
	
	@Test
	public void t6_shouldUpdateUserReturn200() throws URISyntaxException {
		Response result = sut.getUser((long) 1);
		User s = (User) result.getEntity();
		s.setUsername("William");
		sut.updateUser(s);
		result = sut.getUser((long) 1);
		Assert.assertEquals(200, result.getStatus());
	}
	
	@Test
	public void t7_shouldUpdatedUserBeCorrectlyUpdated() throws URISyntaxException {
		Response result = sut.getUser((long) 1);
		User s = (User) result.getEntity();
		s = (User) result.getEntity();
		Assert.assertEquals(200, result.getStatus());
		Assert.assertEquals(s.getUsername(), "William");
	}
}
