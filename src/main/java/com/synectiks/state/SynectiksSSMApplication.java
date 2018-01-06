package com.synectiks.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Rajesh
 */
@SpringBootApplication
@ComponentScan("com.synectiks")
public class SynectiksSSMApplication {

	private static final Logger logger = LoggerFactory
			.getLogger(SynectiksSSMApplication.class);

	private static ConfigurableApplicationContext ctx;

	public static void main(String[] args) {
		ctx = SpringApplication
				.run(SynectiksSSMApplication.class, args);
		for (String bean : ctx.getBeanDefinitionNames()) {
			logger.info("Beans: " + bean);
		}
	}

	/**
	 * Utility method to get bean from spring context.
	 * @param cls
	 * @return
	 */
	public static <T> T getBean(Class<T> cls) {
		return ctx.getBean(cls);
	}
	
}
