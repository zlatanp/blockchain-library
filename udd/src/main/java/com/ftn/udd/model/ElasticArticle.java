package com.ftn.udd.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Document(indexName = "article", type = "default")
@Getter
@Setter
@NoArgsConstructor
public class ElasticArticle {

    @Id
    private String id;
    private String title;
    private User author;
    private List<String> keywords;
    private String apstract;
    private AreaCode areaCode;
    private List<User> commiteeMembers;
    private List<User> signedBy;
    private String filename;
    private String content;


    public ElasticArticle(Article article) {
        this.title = article.getTitle();
        this.author = article.getAuthor();
        this.keywords = article.getKeywords();
        this.apstract = article.getApstract();
        this.areaCode = article.getAreaCode();
        this.commiteeMembers = article.getCommitteeMembers();
        this.signedBy = article.getSignedBy();
        this.filename = article.getId();
        this.content = article.getContent();
    }
}
