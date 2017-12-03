package com.tudvari.morphia.rs.example;

import com.mongodb.MongoTimeoutException;
import com.mongodb.ReadPreference;

import com.tudvari.morphia.rs.example.di.DependencyFactory;
import com.tudvari.morphia.rs.example.entity.Article;
import com.tudvari.morphia.rs.example.repository.BlogRepository;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReplicaSetTests
{
    private final static String VALID_MONGO_URI =
        "mongodb://192.168.42.100:27017,192.168.42.110:27017,192.168.42.120:27017/?replicaSet=rs-example";
    private final static String DBNAME = "rs-example";
    private static Article insertedArticle;

    @BeforeClass public static void setup()
    {
        insertedArticle = new Article();
        insertedArticle.setId("1");
        insertedArticle.setTitle("test title");
        insertedArticle.setBody("test body");
    }

    @AfterClass public static void teardown()
    {
        BlogRepository brepo = DependencyFactory.createBlogRepository(DependencyFactory.createSimpleDataStore(VALID_MONGO_URI, DBNAME));

        brepo.remove(insertedArticle);
    }

    /**
     * This test should be failed, because a secondary node doesn't able to process a write request,
     * and the driver coudn't find a primary on based on the connection URI.
     */
    @Test(expected = MongoTimeoutException.class)
    public void Insert_Article_ToSecondary()
    {
        BlogRepository brepo = DependencyFactory.createBlogRepository(DependencyFactory.createSimpleDataStore("mongodb://192.168.42.120",
                    DBNAME));

        brepo.insert(new Article());
    }

    /**
     * This operation should use the nearest (based on the network latency) node to perform the read operation.
     */
    @Test public void Read_Article_With_ReadPreference_Nearest()
    {
        BlogRepository brepo = DependencyFactory.createBlogRepository(DependencyFactory.createDataStoreWithReadPreference(VALID_MONGO_URI,
                    DBNAME, ReadPreference.nearest()));

        brepo.insert(insertedArticle);
        Article foundArticle = brepo.find(insertedArticle.getId());

        Assert.assertEquals(insertedArticle, foundArticle);
    }

    /**
     * This operation should use a secondary node to perform the read.
     */
    @Test public void Read_Article_With_ReadPreference_Secondary()
    {
        BlogRepository brepo = DependencyFactory.createBlogRepository(DependencyFactory.createDataStoreWithReadPreference(VALID_MONGO_URI,
                    DBNAME, ReadPreference.secondary()));

        brepo.insert(insertedArticle);
        Article foundArticle = brepo.find(insertedArticle.getId());

        Assert.assertEquals(insertedArticle, foundArticle);
    }

    /**
     * This operation should use the primary node to fulfill this request, this is the default behavior.
     */
    @Test public void Read_Article_Without_ReadPreference()
    {
        BlogRepository brepo = DependencyFactory.createBlogRepository(DependencyFactory.createSimpleDataStore(VALID_MONGO_URI, DBNAME));

        brepo.insert(insertedArticle);
        Article foundArticle = brepo.find(insertedArticle.getId());

        Assert.assertEquals(insertedArticle, foundArticle);
    }
}
