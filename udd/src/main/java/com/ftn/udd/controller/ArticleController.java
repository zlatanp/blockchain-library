package com.ftn.udd.controller;

import com.ftn.udd.model.Article;
import com.ftn.udd.model.ElasticArticle;
import com.ftn.udd.model.User;
import com.ftn.udd.repository.AreaCodeRepository;
import com.ftn.udd.repository.ArticleRepository;
import com.ftn.udd.repository.ElasticArticleRepository;
import com.ftn.udd.repository.UserRepository;
import com.ftn.udd.service.MultipartToPDF;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AreaCodeRepository areaCodeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ElasticArticleRepository elasticArticleRepository;

    private MultipartToPDF multipartToPDF = new MultipartToPDF();

    @PostMapping("/add")
    public void addArticleWitAuthors(HttpServletResponse response, @RequestParam("dArticleTitle") String dArticleTitle, @RequestParam("dArticleAuthor") String dArticleAuthor, @RequestParam("dArticleKeywords") String[] dArticleKeywords, @RequestParam("dArticleAbstract") String dArticleAbstract, @RequestParam("dArticleAreaCodes") String dArticleAreaCodes, @RequestParam("file") MultipartFile file,
                                     @RequestParam("DfirstCommittee") String DfirstCommittee, @RequestParam("DsecondCommittee") String DsecondCommittee, @RequestParam("DthirdCommittee") String DthirdCommittee) {

        System.out.println(dArticleTitle + dArticleAuthor + dArticleKeywords + dArticleAbstract + dArticleAreaCodes + DfirstCommittee + DsecondCommittee + DthirdCommittee);


        User author = userRepository.findByEmail(dArticleAuthor);

        Article article = new Article();
        article.setTitle(dArticleTitle);
        article.setAuthor(author);
        article.setKeywords(Arrays.asList(dArticleKeywords));
        article.setApstract(dArticleAbstract);
        article.setAreaCode(areaCodeRepository.findByName(dArticleAreaCodes));
        article.setStatus("PENDING");

        ArrayList<User> commiteeMembers = new ArrayList<User>();
        commiteeMembers.add(userRepository.findByEmail(DfirstCommittee));
        commiteeMembers.add(userRepository.findByEmail(DsecondCommittee));
        commiteeMembers.add(userRepository.findByEmail(DthirdCommittee));
        article.setCommitteeMembers(commiteeMembers);
        article.setSignedBy(new ArrayList<User>());
        try {
            article.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
            article.setContent(multipartToPDF.convert(file));
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "http://localhost:8080/");
        }

        articleRepository.save(article);

        ElasticArticle elasticArticle = new ElasticArticle(article);
        elasticArticleRepository.save(elasticArticle);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", "http://localhost:8080/");

    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public List<Article> getAll(HttpServletResponse response){
        return articleRepository.findAll();
    }

    @RequestMapping(value = "/deleteArticle", method = RequestMethod.POST, produces = "application/json")
    public void deleteArticle(HttpServletResponse response, @RequestParam("id") String id){

        articleRepository.deleteById(id);

    }

    @RequestMapping(value = "/getArticleById", method = RequestMethod.GET, produces = "application/json")
    public Article getArticleById(HttpServletResponse response, @RequestParam("id") String id){

       return articleRepository.findById(id).get();

    }

    @RequestMapping(value = "/checkReview", method = RequestMethod.GET, produces = "application/json")
    public ArrayList<Article> checkReview(HttpServletResponse response, @RequestParam("email") String email){

        ArrayList<Article> pendingArticles = new ArrayList<Article>();

        List<Article> articles = articleRepository.findAll();
        for (Article a: articles) {
            List<User> comitteeMembers = a.getCommitteeMembers();
            List<User> reviewedMembers = a.getSignedBy();
            boolean comitted = false;
            boolean reviewed = false;
            for(int i =0; i<comitteeMembers.size(); i++){
                if (comitteeMembers.get(i).getEmail().equals(email)){
                    comitted = true;
                }
            }
            for(int i =0; i<reviewedMembers.size(); i++){
                if (reviewedMembers.get(i).getEmail().equals(email)){
                    reviewed = true;
                }
            }
            if(comitted && !reviewed)
                pendingArticles.add(a);
        }

        return pendingArticles;
    }

    @RequestMapping(value = "/signArticles", method = RequestMethod.POST, produces = "application/json")
    public void getArticleById(HttpServletResponse response, @RequestParam("email") String email, @RequestParam("ids") String ids){

        System.out.println(email);
        System.out.println(ids);


    }

}
