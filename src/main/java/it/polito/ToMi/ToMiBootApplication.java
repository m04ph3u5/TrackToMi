package it.polito.ToMi;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@EnableScheduling
public class ToMiBootApplication{

  public static void main(String[] args) {
    SpringApplication.run(ToMiBootApplication.class, args);
  }


  @Bean
  public ServletRegistrationBean servletRestRegistration(){
    DispatcherServlet dispatcherServlet = new DispatcherServlet();   
    AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
    applicationContext.register(RestConfig.class);
    dispatcherServlet.setApplicationContext(applicationContext);
    ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/api/*");
    servletRegistrationBean.setName("servletRest");
    return servletRegistrationBean;
  }

  @Bean
  public ThreadPoolTaskExecutor getExecutor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setThreadNamePrefix("ClusteringExecutor-");
    executor.initialize();
    return executor;
  }


  @Bean
  public EmbeddedServletContainerFactory servletContainer() {
    TomcatEmbeddedServletContainerFactory tomcat =
        new TomcatEmbeddedServletContainerFactory() {

      @Override
      protected void postProcessContext(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
      }
    };
    tomcat.addAdditionalTomcatConnectors(createHttpConnector());
    return tomcat;
  }

  private Connector createHttpConnector() {
    Connector connector =
        new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setSecure(false);
    connector.setPort(8080);
    connector.setRedirectPort(8443);
    return connector;
  }

}
