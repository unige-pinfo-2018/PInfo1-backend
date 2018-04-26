package ch.unihub.business.service;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
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

import ch.unihub.dom.Post;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostServiceRsTest {
	@Deployment
	public static WebArchive create() {
		File[] libs = Maven.resolver()
				.loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve()
				.withTransitivity().as(File.class);
		return ShrinkWrap.create(WebArchive.class, "unihub-integration-test.war").addPackages(true, "ch.unihub")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/main/webapp/WEB-INF", "/web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"), "shiro.ini")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsLibraries(libs);
	}

	@Inject
	private PostServiceRs sut;

    private final Post fakePost = new Post((long) 16,"fake text");
    @Before
    public void beforeTest() throws URISyntaxException {
        sut.addPost(fakePost);
    }

    @Test  
    public void t1_testEntityManagerInjected() {  
        assertNotNull(sut);  
    }  
	
	@Test
	public void t2_shouldReturnHelloWorld() {
		String result = sut.helloWorld();
		System.out.println(result);
		Assert.assertTrue(result.contains("Hello World"));
	}

    @Test
    public void t3_verifyAddPost() throws URISyntaxException {

        Post post = new Post();
        long userId=16;
        post.setUserId(userId);
        post.setContent("well");
        //verify if it's create
        Assert.assertEquals(201, sut.addPost(post).getStatus());
    }

    @Test
    public void t4_verifyAddPostWithoutEnougthInformations() throws URISyntaxException {
        // No Content
        Post post = new Post();
        long userId=1;
        post.setUserId(userId);
        Assert.assertEquals(400, sut.addPost(post).getStatus());

        //No userId
        Post post2 = new Post();
        post2.setContent("a question");
        Assert.assertEquals(400, sut.addPost(post2).getStatus());
    }

    @Test
    public void t5_nbPosts() {
        String result = sut.getNbPosts();
        System.out.println(result);
        //verify if return nbPosts and number
        Assert.assertTrue(result.contains("nbPosts"));
    }

    @Test
    public void t6_getPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getPost((long) 1).getStatus());
    }

    @Test
    public void t6_getUserIdOfPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getUserIdPost((long) 1).getStatus());

        //verify if it return the good number *define at the begining
        Assert.assertTrue((long)16 == (long)sut.getUserIdPost((long) 1).getEntity());
    }

    @Test
    public void t7_getParentIdOfPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getParentIdPost((long) 1).getStatus());
    }

    @Test
    public void t8_getNbUpvotesOfPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getNbUpvotes((long) 1).getStatus());
    }

    @Test
    public void t9_getDateOfPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getDate((long) 1).getStatus());
    }

    @Test
    public void t10_getContentOfPostByID() {
        String result = sut.getContent((long)1);
        //verify if return nbPosts and number
        Assert.assertTrue(result.contains("content"));
    }

    @Test
    public void t11_getListIdTagsOfPostById() throws URISyntaxException {
        //verify if an answer
        Assert.assertEquals(200, sut.getListIdTags((long) 1).getStatus());
    }

    @Test
    public void t12_verifyUpdatePost() throws URISyntaxException {
        //creat a post
        Post postBegin = new Post();
        postBegin.setUserId((long) 12);
        postBegin.setContent("for test update");

        //add the post to bdd
        long idPost= (long) sut.addPost(postBegin).getEntity();

        //return the post
        Post post = (Post) sut.getPost(idPost).getEntity();

        //change somethings and verify
        post.setContent("new content");
        System.out.println(post.getContent());
        Assert.assertEquals(200, sut.updatePost(post).getStatus());
    }

}
