/**
 * 
 */
package it.polito.ToMi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

/**
 * @author m04ph3u5
 *
 */
@Configuration
@EnableMongoRepositories
public class MongoConfig extends AbstractMongoConfiguration{

  @Value("${mongo.host}")
  private String mongoClient;
  
//  @Value("${mongo.port}")
//  private int mongoPort;
  
  @Value("${mongo.db.name}")
  private String dbName;
  
  
  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.config.AbstractMongoConfiguration#getDatabaseName()
   */
  @Override
  protected String getDatabaseName() {
    return dbName;
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mongodb.config.AbstractMongoConfiguration#mongo()
   */
  @Override
  public Mongo mongo() throws Exception {
    return new MongoClient(mongoClient);
  }

  @Override
  protected String getMappingBasePackage() {
    return "it.polito.ToMi.repository";
  }

  @Override
  public MongoTemplate mongoTemplate() throws Exception{
    MongoTemplate template = new MongoTemplate(mongo(), getDatabaseName());
    template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
    return template;
  }
}
