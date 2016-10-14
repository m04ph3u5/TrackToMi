/**
 * 
 */
package it.polito.ToMi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author m04ph3u5
 *
 */
@Configuration
@ComponentScan("it.polito.ToMi.controller")
@EnableWebMvc
public class RestConfig {

}
