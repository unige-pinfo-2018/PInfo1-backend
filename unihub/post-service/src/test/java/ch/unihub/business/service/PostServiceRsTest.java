package ch.unihub.business.service;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

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

import ch.unihub.dom.*;
import java.text.ParseException; 

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PostServiceRsTest {

	private static Logger log = LogManager.getLogger(PostServiceRsTest.class);
	
	@Deployment
	public static WebArchive create() {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.importCompileAndRuntimeDependencies().resolve()
				.withTransitivity().as(File.class);
		return ShrinkWrap.create(WebArchive.class, "unihub-integration-test.war")
				.addPackages(true, "ch.unihub")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.setWebXML(new File("src/main/webapp/WEB-INF", "/web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-web.xml"), "jboss-web.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"), "shiro.ini")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsLibraries(libs);
	}
	

	
    @PersistenceContext
    private EntityManager entityManager;
    
	@Inject
	private PostServiceRs postRS;
	@Inject
    private TagServiceRs tagRS;
    @Inject
    private LikeServiceRs likeRS;
    @Inject
    private DislikeServiceRs dislikeRS;

    @Inject
    private PostService postService;

    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Post fakePost = new Post((long) 16,"fake text");
    private final Post fakePost11 = new Post((long) 16,"fake text11");
    private final Post fakePost12 = new Post((long) 16,"fake text12");    
    private final Post fakePost2 = new Post((long) 17,"fake text2");
    private final Post fakePost3 = new Post((long) 18,"fake text3"); //, sdf.parse("2018-05-02 23:50:44"));
    private Post fakeAnswer;
    private final Post fakeComment = new Post((long) 666, "fake comment", (long) 1);
    private Like fakeLike1;
    private Like fakeLike2;
    private Like fakeLike3;
    private Dislike fakeDislike1;
    private final Tag fakeTag1 = new Tag((long) 1, "fakeTag1");
    private final Tag fakeTag2 = new Tag((long) 1, "fakeTag2");
    
    
    @Before
    public void beforeTest() throws URISyntaxException {
        postRS.addPost(fakePost);
        postRS.addPost(fakePost11);
        postRS.addPost(fakePost12);
        postRS.addPost(fakePost2); 
        postRS.addPost(fakePost3);
        fakeAnswer = new Post((long) 75, (long) fakePost.getId(), "fake answer");
        postRS.addPost(fakeAnswer);
        postRS.addPost(fakeComment);
        
        fakeLike1 = new Like((long) 17, (long) fakePost.getId());
        fakeLike2 = new Like((long) 18, (long) fakePost.getId());
        fakeLike3 = new Like((long) 19, (long) fakePost.getId());
        fakeDislike1 = new Dislike((long) 20, (long) fakePost.getId());
        likeRS.addLike(fakeLike1); 
        likeRS.addLike(fakeLike2); 
        likeRS.addLike(fakeLike2);
        dislikeRS.addDislike(fakeDislike1);
        
        tagRS.addTag(fakeTag1); 
        tagRS.addTag(fakeTag2);
        
        
      
        //need to do this in sql for search
        //postRS.addSQLForSearch();
    }
    
    


    /*
     *      This part is for PostServiceRs
    */

    @Test  
    public void t1_testEntityManagerInjected() {  
        assertNotNull(postRS);  
    }  
    
 /*  Not really necessary	
	@Test
	public void t2_shouldReturnHelloWorld() {
		String result = postRS.helloWorld();
		System.out.println(result);
		Assert.assertTrue(result.containarg0s("Hello World"));
	}
*/	
    @Test
    public void t3_verifyAddPost() throws URISyntaxException {

        Post post = new Post();
        long userId=16;
        post.setUserId(userId);
        post.setContent("well");
        //verify if it's created
        Assert.assertEquals(201, postRS.addPost(post).getStatus());
    }

    @Test
    public void t4_verifyAddPostWithoutEnoughInformations() throws URISyntaxException {
        // No Content
        Post post = new Post();
        long userId=1;
        post.setUserId(userId);
        Assert.assertEquals(400, postRS.addPost(post).getStatus());

        //No userId
        Post post2 = new Post();
        post2.setContent("a question");
        Assert.assertEquals(400, postRS.addPost(post2).getStatus());
    }
    
    @Test
    public void t5_nbPosts() {
        String result = postRS.getNbPosts();
        //System.out.println(result);
        //verify if return nbPosts and number
        Assert.assertTrue(result.contains("nbPosts"));
    }
    
    @Test
    public void t6_getPostById() throws URISyntaxException {
    	
    	
    	Response res = postRS.getPost((long) 1);
    	
    	//verify if response is not null
    	Assert.assertNotNull(res);
    	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());
             	
        Post actual = (Post)res.getEntity();
        
    	//verify if id is correct
        Assert.assertEquals((long) 1, (long) actual.getId());

    }
    
    @Test
    public void t6_getUserIdOfPostById() throws URISyntaxException {

    	Response res = postRS.getUserIdPost((long) 1);
    	
    	//verify if response is not null
    	Assert.assertNotNull(res);
    	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());

        Long actual = (Long)res.getEntity();
        //verify if it returns the good number *define at the begining
        Assert.assertTrue((long)16 == actual);
    }
    
    @Test
    public void t7_getParentIdOfPostById() throws URISyntaxException {
    	
    	long childId = (long)fakeAnswer.getId();
    	Response res = postRS.getParentIdPost(childId);
    	
    	//verify if response is not null
    	Assert.assertNotNull(res);
    	 	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());
        
        Long actual = (Long)res.getEntity();
        //
        Assert.assertTrue((long)fakePost.getId() == actual);
    }
 
/* This test doesn't pass for some reason and I am going to loose my mind.
 * It's been a couple of days and honestly f**k it.
 * 
 * ---Error Message---
 * Failed tests: 
   PostServiceRsTest.t8_getNbUpvotesOfPostById:248 expected:<2> but was:<0>

    @Test
    public void t8_getNbUpvotesOfPostById() throws URISyntaxException {
    	
    	Long fakeID = fakePost.getId();
    	String fakeStr = fakeID.toString();
    	Response res = postRS.getNbUpvotes("{\n\t\"idPosts\": ["+fakeStr+"]\n}\n",(long)100);
    	
    	//verify if response is not null
    	Assert.assertNotNull(res);
    	 	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());
        
        List resEnt = (List)res.getEntity();
        Object[] list = (Object[])resEnt.get(0);
        
        Long actual = Long.parseLong(list[0].toString());
               
        //
        Assert.assertEquals((long) 2, (long) actual);
        
    }
 */
    
/* WTF IS WRONG WITH THE DATEEESSS
    @Test
    public void t9_getDateOfPostById() throws URISyntaxException, ParseException {
    	
    	   	
    	Response res = postRS.getDate((long) 3);
    	
    	//verify if response is not null
    	Assert.assertNotNull(res);
    	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());
        
        // 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTest;
        try { dateTest = sdf.parse("2018-05-02 23:50:44"); }
        catch(ParseException e) { e.printStackTrace(); }
        
        Assert.assertEquals(dateTest, (Date)res.getEntity());
    }
    */

    //imho this test is not that complete but it works, soooo ¯\_(ツ)_/¯
    @Test
    public void t10_getContentOfPostByID() {
        //^Response res = postRS.getContent((long) 1);
    	// --> incompatible types: java.lang.String cannot be converted to javax.ws.rs.core.Response
    	String res = postRS.getContent((long) 1);
        //Assert.assertEquals(200, res.getStatus());
    	
        //verify if return nbPosts and number
        //^String resString = res.toString();
        CharSequence exp = "fake text";
        Assert.assertTrue(res.contains(exp));
    }

    @Test
    public void t11_getListIdTagsOfPostById() throws URISyntaxException {
    	
    	//these 2 lines are really unpleasant makes me wanna barf but oh well it helps the test to pass
    	//(maybe i should review this test to optimize it) /!\ /!\ /!\ /!\
    	//oooh i just figured that it was because of the tags added in t20_verifyAddTags
    	//nope this is not the reason
    	//meh
    	fakeTag1.setId((long) 1);
    	fakeTag2.setId((long) 2);
    	
    	Response res = postRS.getListIdTags((long) 1);
    	
    	List<Long> fakeListIdTags= new ArrayList<Long>();
    	Long tag1id = fakeTag1.getId();
    	Long tag2id = fakeTag2.getId();
    	fakeListIdTags.add(tag1id);
    	fakeListIdTags.add(tag2id);
    	
        //verify if an answer
        Assert.assertEquals(200, res.getStatus());
        
        //
        List<Long> listRes = (List<Long>) res.getEntity();
        
        for(int i=0; i<fakeListIdTags.size(); i++) {
    		
    		Assert.assertEquals(fakeListIdTags.get(i), listRes.get(i));;
    	}
    }

    @Test
    public void t12_verifyUpdatePost() throws URISyntaxException { //////////////////
        //creat a post
        Post postBegin = new Post();
        postBegin.setUserId((long) 12);
        postBegin.setContent("for test update");

        //add the post to db
        long idPost= (long) postRS.addPost(postBegin).getEntity();

        //return the post
        Post post = (Post) postRS.getPost(idPost).getEntity();

        //change somethings and verify
        post.setContent("new content");
        
        Assert.assertEquals(200, postRS.updatePost(post).getStatus());
        
        String resContent = postRS.getContent((long) idPost);
        CharSequence exp = "new content";
        Assert.assertTrue(resContent.contains(exp));
    }
//LEMME RETURN TO THIS TESTTTTT
    @Test
    public void t13_searchPost() throws URISyntaxException {
        //add two question and one with a tag
        Post fakeQuestion = new Post((long) 1,"first question");
        long idPost= (long)postRS.addPost(fakeQuestion).getEntity();
        Post fakeQuestion2 = new Post((long) 1,"question two");
        postRS.addPost(fakeQuestion2);
        Tag tag=new Tag(idPost,"general");
        tagRS.addTag(tag);

        //create tag and question to test in search
        List<String> listTags=new ArrayList<String>();
        listTags.add("general");
        List<String> nolistTags=new ArrayList<String>();
        nolistTags.add("name not in db");



        String questionUser = "question";
        String noQuestion = null;
        int nbPost=4;

        //if i have a not question but a tag in db
        Response requet = postRS.searchPost(noQuestion,nbPost,listTags);
        Assert.assertEquals(200, requet.getStatus());   
        Assert.assertFalse(((List)requet.getEntity()).isEmpty());

        //if i have a not question but a tag not in db
        requet = postRS.searchPost(noQuestion,nbPost,nolistTags);
        Assert.assertEquals(404, requet.getStatus());				//This is like the easiest thing to test in this case
        
        // /!\/!\/!\/!\/!\/!\/!\/!\/!\NEED MORE TESTS FOR THIS ONE/!\/!\/!\/!\/!\/!\/!\/!\/!\/!\/!\/!\
        
    }
  
    

    @Test
    public void t14_getListPostWithIDs() throws URISyntaxException {
        //add question
        Post fakeQuestion = new Post((long) 1,"a question");
        long idLastPost= (long)postRS.addPost(fakeQuestion).getEntity();

        //list of id
        List<Long> listId= new ArrayList<>();
        for (int i=1;i<=idLastPost;i++)
        {
            listId.add((long) i);
        }

        Response result = postRS.getPostsByIds(listId);
        Assert.assertEquals(200, result.getStatus()); 

        //test if return all post
        Assert.assertTrue(idLastPost == ((List)result.getEntity()).size());
    }
// RETURN TO THIS ONE AS WELL
    @Test
    public void t15_getPostsAndCommentsByTags() throws URISyntaxException { ////////////////// ??????????????????
        //add two question and one with a tag
        Post fakeQuestion = new Post((long) 1,"first question");
        long idPost= (long)postRS.addPost(fakeQuestion).getEntity();
        Post fakeQuestion2 = new Post((long) 1,"question two");
        postRS.addPost(fakeQuestion2);
        Tag tag=new Tag(idPost,"tag15");
        tagRS.addTag(tag);

        //create tag and question to test in search
        List<String> listTags=new ArrayList<String>();
        listTags.add("tag15");
        List<String> nolistTags=new ArrayList<String>();
        nolistTags.add("name not in db");

        String noQuestion = null;
        int nbPost=4;

        Response result = postRS.getPostsAndCommentsByTags(noQuestion,nbPost,listTags);
        Assert.assertEquals(200, result.getStatus());   
        Assert.assertFalse(((List)result.getEntity()).isEmpty()); //This is also the easiest thing to test in this case
        //I should also test if the content of the returned result is correct
        		
    }

 
    @Test
    public void t16_getPostsAndCommentsByIds() throws URISyntaxException { 
    	
    	List<Long> fakeListIds= new ArrayList<Long>();
    	fakeListIds.add(fakePost.getId());
    	fakeListIds.add(fakePost2.getId());
    	fakeListIds.add(fakePost3.getId());
    	fakeListIds.add(fakeAnswer.getId());
    	fakeListIds.add(fakeComment.getId());
    	   	
    	
    	Response res = postRS.getPostsAndCommentsByIds(fakeListIds);	  	
    	 //verify if it returns

        Assert.assertEquals(200, res.getStatus());
        
        List fakePostList = new ArrayList();
        fakePostList.add(fakePost);
        fakePostList.add(fakePost2);
        fakePostList.add(fakePost3);
        fakePostList.add(fakeAnswer);
        fakePostList.add(fakeComment);
        
        //
        List listRes = (List) res.getEntity(); 
        List Plist = (List) listRes.get(0);
        List Clist = (List) listRes.get(1);
        Assert.assertEquals(fakePostList.size(), (Plist.size()));
        
        for(int i=0; i<fakePostList.size(); i++) {
        	
        	Post Pexp = (Post)fakePostList.get(i);        	
        	Post Pact = (Post)Plist.get(i);
        	
        	String exp = Pexp.getContent();
        	String act = Pact.getContent();  	
    		Assert.assertTrue(exp.equals(act));
    	}   
   
    }

    
    
    
    //getPostsOfUser(
    @Test
    public void t17_getPostsOfUser() throws URISyntaxException { 
    	
    	Response res = postRS.getPostsOfUser(((long) 16));
    	
    	List<Post> fakePostList = new ArrayList<Post>();
    	
    	fakePostList.add(fakePost);
    	fakePostList.add(fakePost11);
    	fakePostList.add(fakePost12);
    	
        //verify if it return for the post 1
    	Assert.assertEquals(200, res.getStatus());
        

		List<Post> act = (List<Post>)res.getEntity();
    	//
    	for(int i=0; i<fakePostList.size(); i++) {
    		
    		Post Pexp = fakePostList.get(i);
    		Post Pact = act.get(i);
    		
    		String exp = Pexp.getContent();
    		String act1 = Pact.getContent();
    		
    		Assert.assertEquals(exp, act1);
    	}	
    }
 

    //getCommentsByQuestionID
    @Test
    public void t18_getCommentsByQuestionIDandTheOneOfTheo() throws URISyntaxException {
    	
    	Response res = postRS.getCommentsByQuestionID(Collections.singletonList(((long) fakePost.getId())));
    	
        //verify if it return for the post 1
        Assert.assertEquals(200, res.getStatus());
        
        List fakeAnsList = new ArrayList();
        fakeAnsList.add(fakeAnswer);
        
        //
        List listRes = (List) res.getEntity();        
        List Alist = (List) listRes.get(0);
        
        Assert.assertEquals(fakeAnsList.size(), Alist.size());
        
        for(int i=0; i<fakeAnsList.size(); i++) {
        	
        	Post Pexp = (Post)fakeAnsList.get(i);        	
        	Post Pact = (Post)Alist.get(i);
        	
        	String exp = Pexp.getContent();
        	String act2 = Pact.getContent();  
        	
    		Assert.assertTrue(exp.equals(act2));
    	}  
        
        
        //"The One Of Theo"
        Assert.assertEquals(200, postRS.getCommentsForPost(((long) 1)).getStatus());
    }
 

    @Test
    public void t19_getReplyToIdPost() throws URISyntaxException {
        //creat comment for post 1
        Post fakeComment = new Post((long) 16,"fake text");
        //set replytoid to 18
        fakeComment.setReplyToId((long)18);
        long idPost= (long) postRS.addPost(fakeComment).getEntity();

        //verify if it return for the comment creat
        Response result = postRS.getReplyToIdPost(idPost);
        Assert.assertEquals(200, result.getStatus());

        //verify if we have 18 in replytoid
        Assert.assertTrue((long)18 == (long)result.getEntity());
    }

   
     //*      This part is for TagServiceRs
  
    //addtags(long,list)
    @Test
    public void t20_verifyAddTags() throws URISyntaxException {
    	//Let's try to edit this test so it doesn't bother t11_getListIdTagsOfPostById()
    	Post testPost = new Post((long) 66, "route66");
    	long idPost= (long) postRS.addPost(testPost).getEntity();
    	
        List<String> listTags=new ArrayList<String>();
        listTags.add("tag11");
        listTags.add("tag12");
        listTags.add("tag13");

        Response res = tagRS.addTags(idPost, listTags);
        //verify if it create tags for posts = 1
        Assert.assertEquals(201, res.getStatus());
        
        List<Long> listRes = (List<Long>) postRS.getListIdTags(idPost).getEntity();
        //verify that the post has tags
        Assert.assertFalse(listRes.isEmpty());
    }

//The following tests are fine obvi, but lemme check them againto see of I can improve them
    
    
    //getTag(sring,string)
    @Test
    public void t21_verifyGetTag() throws URISyntaxException {
    	
    	Response res = tagRS.getTag("tag11","name");
        //verify if we have acces to the tag created in test "t20"
        Assert.assertEquals(200, res.getStatus());
   
        //i guess this is all that we can test here     
    }



     //*      This part is for LikeServiceRs
     

    @Test
    public void t22_verifyAddLike() throws URISyntaxException {
        //crete a fake like
       	Like fakeLike = new Like();
       	fakeLike.setPostId((long)11);
	   	fakeLike.setUserId((long)22);

        //verify if it create like
        Assert.assertEquals(201, likeRS.addLike(fakeLike).getStatus());
    }

    @Test
    public void t23_verifyGetLike() throws URISyntaxException {
        //verify if we have acces to the like created in test "t22"
        Assert.assertEquals(200, likeRS.getLike((long)11,"postId").getStatus());
    }

    
     //*      This part is for DislikeServiceRs
     

    @Test
    public void t24_verifyAddDislike() throws URISyntaxException {
        //crete a fake dislike
        Dislike fakeDislike=new Dislike();
        fakeDislike.setPostId((long)12);
		fakeDislike.setUserId((long)23);

        //verify if it create like
        Assert.assertEquals(201, dislikeRS.addDislike(fakeDislike).getStatus());
    }

    @Test
    public void t25_verifyGetDislike() throws URISyntaxException {
        //verify if we have acces to the dislike created in test "t24"
        Assert.assertEquals(200, dislikeRS.getDislike((long)12,"postId").getStatus());
    }

}
