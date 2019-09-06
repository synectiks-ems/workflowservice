package com.synectiks.state;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.state.machine.config.ApplicationProperties;

import io.github.jhipster.config.JHipsterConstants;

/**
 * @author Rajesh
 */
@SpringBootApplication
@ComponentScan("com.synectiks")
@EnableConfigurationProperties({ApplicationProperties.class})
public class SynectiksSSMApplication implements InitializingBean {

	private static final Logger logger = LoggerFactory
			.getLogger(SynectiksSSMApplication.class);

	private static ConfigurableApplicationContext ctx;

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		ctx = SpringApplication
				.run(SynectiksSSMApplication.class, args);
		logApplicationStartup(ctx.getEnvironment());
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

	@EventListener(ApplicationReadyEvent.class)
	public void setIndexAndMapping() {
		populateEnvPropertiesInSystem();
	}

	private void populateEnvPropertiesInSystem() {
		AbstractEnvironment absEnv = (AbstractEnvironment) env;
		absEnv.getPropertySources().forEach(pSrc -> {
			//logger.info("Source----->: " + pSrc.getName()
			//		+ "\n\tcls: " + pSrc.getSource().getClass().getName());
			if (pSrc instanceof MapPropertySource) {
				Map<String, Object> propMap = ((MapPropertySource) pSrc).getSource();
				for (String key : propMap.keySet()) {
					Object val = propMap.get(key);
					//logger.info("Val class: "
					//		+ (val != null ? val.getClass().getName() : "null"));
					if (!IUtils.isNull(val) && (val instanceof String ||
							val instanceof Number || val instanceof OriginTrackedValue)) {
						logger.info("Setting: " + key);
						System.setProperty(key, String.valueOf(val));
					}
				}
			}
		});
	}

	/**
	 * Initializes search.
	 * <p>
	 * Spring profiles can be configured with a program argument
	 * --spring.profiles.active=your-active-profile
	 * <p>
	 * You can find more information on how profiles work with JHipster on
	 * <a href=
	 * "https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<String> activeProfiles = Arrays
				.asList(env.getActiveProfiles());
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
			logger.error("You have misconfigured your application! It should not run "
					+ "with both the 'dev' and 'prod' profiles at the same time.");
		}
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
			logger.error("You have misconfigured your application! It should not "
					+ "run with both the 'dev' and 'cloud' profiles at the same time.");
		}
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		String serverPort = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path");
		if (StringUtils.isBlank(contextPath)) {
			contextPath = "/";
		}
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.warn(
					"The host name could not be determined, using `localhost` as fallback");
		}
		logger.info("\n----------------------------------------------------------\n\t"
				+ "Application '{}' is running! Access URLs:\n\t"
				+ "Local: \t\t{}://localhost:{}{}\n\t" + "External: \t{}://{}:{}{}\n\t"
				+ "Profile(s): \t{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"), protocol, serverPort,
				contextPath, protocol, hostAddress, serverPort, contextPath,
				env.getActiveProfiles());
	}
}
