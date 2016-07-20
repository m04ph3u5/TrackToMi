package it.polito.ToMi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@EnableScheduling
public class ToMiBootApplication {

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
	
}
