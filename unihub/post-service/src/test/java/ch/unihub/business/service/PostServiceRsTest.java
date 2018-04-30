package ch.unihub.business.service;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.*;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import ch.unihub.dom.Tag;
import ch.unihub.dom.Like;
import ch.unihub.dom.Dislike;

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
	@Inject
    private TagServiceRs tagService;
    @Inject
    private LikeServiceRs likeService;
    @Inject
    private DislikeServiceRs dislikeService;

    @Inject
    private PostService postServiceImp;

    private final Post fakePost = new Post((long) 16,"fake text");
    @Before
    public void beforeTest() throws URISyntaxException {
        sut.addPost(fakePost);

        //need to do this in sql for search
        //sut.addSQLForSearch();
    }

    /*
     *      This part is for PostServiceRs
     */

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
        Assert.assertEquals(200, sut.getNbUpvotes("{\n\t\"idPosts\": [1,1]\n}\n").getStatus());
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

    @PersistenceContext
    private EntityManager entityManager;


    @Test
    public void t13_searchPost() throws URISyntaxException {
        //add two question and one with a tag
        Post fakeQuestion = new Post((long) 1,"first question");
        long idPost= (long)sut.addPost(fakeQuestion).getEntity();
        Post fakeQuestion2 = new Post((long) 1,"question two");
        sut.addPost(fakeQuestion2);
        Tag tag=new Tag(idPost,"general");
        tagService.addTag(tag);

        //creat tag and question to test in search
        List<String> listTags=new ArrayList<String>();
        listTags.add("general");
        List<String> nolistTags=new ArrayList<String>();
        nolistTags.add("name not in db");



        String questionUser = "question";
        String noQuestion = null;
        int nbPost=4;

        //if i have a not question but a tag in db
        Response requet = sut.searchPost(noQuestion,nbPost,listTags);
        Assert.assertEquals(200, requet.getStatus());

        //if i have a not question but a tag not in db
        requet = sut.searchPost(noQuestion,nbPost,nolistTags);
        Assert.assertEquals(404, requet.getStatus());
    }

    @Test
    public void t14_getListPostWithIDs() throws URISyntaxException {
        //add question
        Post fakeQuestion = new Post((long) 1,"a question");
        long idLastPost= (long)sut.addPost(fakeQuestion).getEntity();

        //list of id
        List<Long> listId= new ArrayList<>();
        for (int i=1;i<=idLastPost;i++)
        {
            listId.add((long) i);
        }

        Response result = sut.getPostsByIds(listId);
        Assert.assertEquals(200, result.getStatus());

        //test if return all post
        Assert.assertTrue(idLastPost == ((List)result.getEntity()).size());
    }

    @Test
    public void t15_getPostsAndCommentsByTags() throws URISyntaxException {
        //add two question and one with a tag
        Post fakeQuestion = new Post((long) 1,"first question");
        long idPost= (long)sut.addPost(fakeQuestion).getEntity();
        Post fakeQuestion2 = new Post((long) 1,"question two");
        sut.addPost(fakeQuestion2);
        Tag tag=new Tag(idPost,"t15");
        tagService.addTag(tag);

        //creat tag and question to test in search
        List<String> listTags=new ArrayList<String>();
        listTags.add("t15");
        List<String> nolistTags=new ArrayList<String>();
        nolistTags.add("name not in db");

        String noQuestion = null;
        int nbPost=4;

        Response result = sut.getPostsAndCommentsByTags(noQuestion,nbPost,listTags);
        Assert.assertEquals(200, result.getStatus());
    }

    @Test
    public void t16_getPostsAndCommentsByIds() throws URISyntaxException {
        //verify if it return for the post 1
        Assert.assertEquals(200, sut.getPostsAndCommentsByIds(Collections.singletonList((long) 1)).getStatus());
    }

    //getPostsOfUser(
    @Test
    public void t17_getPostsOfUser() throws URISyntaxException {
        //verify if it return for the post 1
        Assert.assertEquals(200, sut.getPostsOfUser(((long) 16)).getStatus());
    }

    //getCommentsByQuestionID
    @Test
    public void t18_getCommentsByQuestionIDandTheOneOfTheo() throws URISyntaxException {
        //verify if it return for the post 1
        Assert.assertEquals(200, sut.getCommentsByQuestionID(Collections.singletonList(((long) 1))).getStatus());
        Assert.assertEquals(200, sut.getCommentsForPost(((long) 1)).getStatus());
    }

    @Test
    public void t19_getReplyToIdPost() throws URISyntaxException {
        //creat comment for post 1
        Post fakeComment = new Post((long) 16,"fake text");
        //set replytoid to 18
        fakeComment.setReplyToId((long)18);
        long idPost= (long) sut.addPost(fakeComment).getEntity();

        //verify if it return for the comment creat
        Response result = sut.getReplyToIdPost(idPost);
        Assert.assertEquals(200, result.getStatus());

        //verify if we have 18 in replytoid
        Assert.assertTrue((long)18 == (long)result.getEntity());
    }

    /*
     *      This part is for TagServiceRs
     */

    //addtags(long,list)
    @Test
    public void t20_verifyAddTags() throws URISyntaxException {
        List<String> listTags=new ArrayList<String>();
        listTags.add("tag1");
        listTags.add("tag2");
        listTags.add("tag3");

        //verify if it create tags for posts = 1
        Assert.assertEquals(201, tagService.addTags((long)1,listTags).getStatus());
    }

    //getTag(sring,string)
    @Test
    public void t21_verifyGetTag() throws URISyntaxException {
        //verify if we have acces to the tag created in test "t20"
        Assert.assertEquals(200, tagService.getTag("tag1","name").getStatus());
    }


    /*
     *      This part is for LikeServiceRs
     */

    @Test
    public void t22_verifyAddLike() throws URISyntaxException {
        //crete a fake like
        Like fakeLike=new Like();
        fakeLike.setPostId((long)11);
        fakeLike.setUserId((long)22);

        //verify if it create like
        Assert.assertEquals(201, likeService.addLike(fakeLike).getStatus());
    }

    @Test
    public void t23_verifyGetLike() throws URISyntaxException {
        //verify if we have acces to the like created in test "t20"
        Assert.assertEquals(200, likeService.getLike((long)11,"postId").getStatus());
    }

    /*
     *      This part is for DislikeServiceRs
     */

    @Test
    public void t24_verifyAddDislike() throws URISyntaxException {
        //crete a fake dislike
        Dislike fakeDislike=new Dislike();
        fakeDislike.setPostId((long)12);
        fakeDislike.setUserId((long)23);

        //verify if it create like
        Assert.assertEquals(201, dislikeService.addDislike(fakeDislike).getStatus());
    }

    @Test
    public void t25_verifyGetDislike() throws URISyntaxException {
        //verify if we have acces to the dislike created in test "t24"
        Assert.assertEquals(200, dislikeService.getDislike((long)12,"postId").getStatus());
    }
}
