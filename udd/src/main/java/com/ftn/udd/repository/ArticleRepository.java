package com.ftn.udd.repository;

import com.ftn.udd.model.Article;
import com.ftn.udd.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, String> {

    List<Article> getByAuthor(User author);
}
