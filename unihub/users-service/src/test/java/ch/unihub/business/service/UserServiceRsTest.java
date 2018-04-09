package ch.unihub.business.service;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
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
    public void testEntityManagerInjected() {  
        assertNotNull(sut);  
    }  
	
	@Test
	public void shouldNbUserReturnTheCurrentNbOfUsers() {
		// We mostly test the return codes, syntax and encoding.
		String result = sut.getNbUsers();
		System.out.println(result);
		Assert.assertTrue(result.contains("nbUsers"));
	}
}
