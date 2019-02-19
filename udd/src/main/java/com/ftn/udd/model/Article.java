package com.ftn.udd.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    private String id;
    private String title;
    private User author;
    private List<String> keywords;
    private String apstract;
    private AreaCode areaCode;
    private String status;
    private List<User> committeeMembers;
    private List<User> signedBy;
    private Binary file;
    private String content;


    public Article(String title, User author, List<String> keywords, String apstract, AreaCode areaCode, String status, List<User> committeeMembers, List<User> signedBy,Binary file, String content) {
        this.title = title;
        this.author = author;
        this.keywords = keywords;
        this.apstract = apstract;
        this.areaCode = areaCode;
        this.status = status;
        this.committeeMembers = committeeMembers;
        this.signedBy = signedBy;
        this.file = file;
        this.content = content;
    }
}
