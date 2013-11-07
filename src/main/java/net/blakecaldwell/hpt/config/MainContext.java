package net.blakecaldwell.hpt.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main beans configuration.
 */
@Configuration
@ComponentScan(basePackages = { "net.blakecaldwell" })
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = { "net.blakecaldwell.hpt.repository" })
@PropertySource("classpath:application.properties")
public class MainContext
{
	/**
	 * Enable reading of our property files defined in
	 * 
	 * @PropertySource.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
	{
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * Our data source - an HSQLDB database.
	 */
	@Bean(name = "dataSource")
	public DataSource dataSource()
	{
		DataSource dataSource = new EmbeddedDatabaseBuilder() //
		        .setType(EmbeddedDatabaseType.HSQL)//
		        .setName("db") //
		        .build();

		return dataSource;
	}

	/**
	 * Build our entity manager factory, which uses our data source.
	 */
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean()
	{
		LocalContainerEntityManagerFactoryBean fb = new LocalContainerEntityManagerFactoryBean();
		fb.setDataSource(dataSource());
		fb.setPackagesToScan("net.blakecaldwell.hpt.entity");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
		fb.setJpaVendorAdapter(vendorAdapter);

		// set the options: turn off caching
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE, false);
		map.put(org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE, false);

		// don't show SQL
		map.put(org.hibernate.cfg.AvailableSettings.SHOW_SQL, false);

		fb.setJpaPropertyMap(map);

		return fb;
	}

	/**
	 * Transaction manager.
	 */
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager()
	{
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactoryBean().getObject());
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}
}
