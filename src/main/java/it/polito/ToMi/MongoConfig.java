/**
 * 
 */
package it.polito.ToMi;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;

/**
 * @author m04ph3u5
 *
 */
@Configuration
@EnableMongoRepositories
public class MongoConfig extends AbstractMongoConfiguration{

	/* (non-Javadoc)
	 * @see org.springframework.data.mongodb.config.AbstractMongoConfiguration#getDatabaseName()
	 */
	@Override
	protected String getDatabaseName() {
		return "tomi";
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.mongodb.config.AbstractMongoConfiguration#mongo()
	 */
	@Override
	public Mongo mongo() throws Exception {
		 return new MongoClient("localhost", 27017);
	}
	
	@Override
    protected String getMappingBasePackage() {
        return "it.polito.ToMi.repository";
	}

	@Override
	public MongoTemplate mongoTemplate() throws Exception{
		System.out.println("MONGO TEMPLATE INSTANCE");
		MongoTemplate template = new MongoTemplate(mongo(), getDatabaseName());
		template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		return template;
	}
}
