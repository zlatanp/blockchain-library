package com.ftn.udd.repository;


import com.ftn.udd.model.ElasticArticle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticArticleRepository extends ElasticsearchRepository<ElasticArticle, String > {

    List<ElasticArticle> findByTitle(String title);
}
