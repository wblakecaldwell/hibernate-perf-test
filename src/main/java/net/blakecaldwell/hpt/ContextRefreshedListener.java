package net.blakecaldwell.hpt;

import net.blakecaldwell.hpt.service.SampleDataLoaderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Load data into the database on application startup.
 */
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent>
{
	/**
	 * The number of records expected in our database.
	 */
	@Value("${db.numberOfCustomersToLoad}")
	private int numberOfRecordsExpected;

	/**
	 * Service to load up sample data.
	 */
	private SampleDataLoaderService sampleDataLoaderService;

	@Autowired
	public ContextRefreshedListener(SampleDataLoaderService sampleDataLoaderService)
	{
		this.sampleDataLoaderService = sampleDataLoaderService;
	}

	/**
	 * On spring context startup, we load 50,000 records into our
	 * database for testing.
	 */
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		// Load up 500,000 customers
		sampleDataLoaderService.loadSampleData(numberOfRecordsExpected);
	}
}
