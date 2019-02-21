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
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
    private ElasticsearchOperations es;

    @Autowired
    private ElasticArticleRepository elasticArticleRepository;

    private MultipartToPDF multipartToPDF = new MultipartToPDF();
    RestTemplate restTemplate = new RestTemplate();

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
        article.setStatus("ACTIVE");

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
        String restCall = restTemplate.postForObject("http://localhost:8090/blockchain/add/"+article.getId(),null, String.class);
        ElasticArticle elasticArticle = new ElasticArticle(article);
        elasticArticleRepository.save(elasticArticle);


        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", "http://localhost:8080/");

    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET, produces = "application/json")
    public List<Article> getAll(HttpServletResponse response){
        List<Article> allArticles = articleRepository.findAll();
        ArrayList<Article> articles = new ArrayList<Article>();
        for (Article a: allArticles) {
            if(a.getStatus().equals("ACTIVE")){
                articles.add(a);
            }
        }

        return articles;
    }

    @RequestMapping(value = "/deleteArticle", method = RequestMethod.POST, produces = "application/json")
    public void deleteArticle(HttpServletResponse response, @RequestParam("id") String id){

        articleRepository.deleteById(id);

    }

    @RequestMapping(value = "/getArticleById", method = RequestMethod.GET, produces = "application/json")
    public Article getArticleById(HttpServletResponse response, @RequestParam("id") String id){

       return articleRepository.findById(id).orElseGet(null);

    }

    @RequestMapping(value = "/checkReview", method = RequestMethod.GET, produces = "application/json")
    public ArrayList<Article> checkReview(HttpServletResponse response, @RequestParam("email") String email){

        ArrayList<Article> pendingArticles = new ArrayList<Article>();

        List<Article> allArticles = articleRepository.findAll();
        ArrayList<Article> articles = new ArrayList<Article>();
        for (Article a: allArticles) {
            if(a.getStatus().equals("ACTIVE")){
                articles.add(a);
            }
        }

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
    public String getArticleById(@RequestParam("email") String email, @RequestParam("pin") String pin, @RequestParam("ids") String ids) {

        User user = userRepository.findByEmail(email);
        if (user != null && user.getPinCode().equals(pin)){
            //do the stuff
            List<String> articleIds = Arrays.asList(ids.split(","));
            for (String id: articleIds) {
                Article article = articleRepository.findById(id).get();
                Article articleToSave = new Article(article);
                article.setStatus("DELETED");

                //Delete old in MongoDB and ElasticSearch
                Client client = es.getClient();
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                                .filter(QueryBuilders.matchQuery("filename", article.getId()))
                                .source("article")
                                .get();
                articleRepository.save(article);

                //Update new
                try {
                    articleToSave.getSignatures().add(sign(articleToSave, user));
                    articleToSave.getSignedBy().add(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //save New in MongoDB, ElasticSearch and Blockchain
                articleRepository.save(articleToSave);
                ElasticArticle elasticArticle = new ElasticArticle(articleToSave);
                elasticArticleRepository.save(elasticArticle);
                String restCall = restTemplate.postForObject("http://localhost:8090/blockchain/add/"+articleToSave.getId(),null, String.class);
            }
            return "OK";
        }else{
            return "BADPIN";
        }


    }

    public String sign(Article articleToSave, User user) throws Exception{

        byte[] privateKeyBytes = Base64.getDecoder().decode(user.getPrivateKey());
        byte[] publicKeyBytes = Base64.getDecoder().decode(user.getPublicKey());
        KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        byte[] pdf = articleToSave.getFile().getData();

        Signature instance = Signature.getInstance("SHA256withRSA");
        instance.initSign(privateKey, new SecureRandom());
        instance.update(pdf);
        byte[] signature = instance.sign();
        articleToSave.setFile(new Binary(BsonBinarySubType.BINARY, pdf));

        instance.initVerify(publicKey);
        instance.update(pdf);

        System.out.println(Base64.getEncoder().encodeToString(signature));
        System.out.println(instance.verify(signature));

        return Base64.getEncoder().encodeToString(signature);
    }

}