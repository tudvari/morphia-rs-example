package com.tudvari.morphia.rs.example.di;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ReadPreference;

import com.tudvari.morphia.rs.example.repository.BlogRepository;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class DependencyFactory
{
    private static final String ENTITY_PACKAGE = "com.tudvari.morphia.rs.example";

    public static BlogRepository createBlogRepository(final Datastore datastore)
    {
        return new BlogRepository(datastore);
    }

    /**
     * Create a new Morphia Datastore Instance with ReadPreference
     * @param mongoUri Connection String URI for the MongoDB ReplicaSet.
     * @param dbName Name of the Database.
     * @param readPreference the ReadPreference client side option.
     * @return Morphia Datastore instance.
     */
    public static Datastore createDataStoreWithReadPreference(final String mongoUri, final String dbName, ReadPreference readPreference)
    {
        final Morphia morphia = new Morphia();

        morphia.mapPackage(ENTITY_PACKAGE);
        MongoClientOptions.Builder options = MongoClientOptions.builder().readPreference(readPreference);
        final Datastore datastore = morphia.createDatastore(new MongoClient(new MongoClientURI(mongoUri, options)), dbName);

        datastore.ensureIndexes();

        return datastore;
    }

    /**
     * Create a new Morphia Datastore Instance.
     * @param mongoUri Connection String URI for the MongoDB ReplicaSet.
     * @param dbName Name of the Database.
     * @return Morphia Datastore instance.
     */
    public static Datastore createSimpleDataStore(final String mongoUri, final String dbName)
    {
        final Morphia morphia = new Morphia();

        morphia.mapPackage(ENTITY_PACKAGE);
        final Datastore datastore = morphia.createDatastore(new MongoClient(new MongoClientURI(mongoUri)), dbName);

        datastore.ensureIndexes();

        return datastore;
    }
}
