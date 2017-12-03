package com.tudvari.morphia.rs.example.repository;

import com.mongodb.WriteResult;

import com.tudvari.morphia.rs.example.entity.Article;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;

public class BlogRepository
{
    private final BasicDAO<Article, String> dao;

    public BlogRepository(final Datastore datastore)
    {
        dao = new BasicDAO(Article.class, datastore);
    }

    public Article find(String id)
    {
        return dao.get(id);
    }

    public Article insert(final Article article)
    {
        Key<Article> returnedKey = dao.save(article);

        return dao.getDatastore().getByKey(Article.class, returnedKey);
    }

    public WriteResult remove(Article deletedArticle)
    {
        return dao.delete(deletedArticle);
    }
}
