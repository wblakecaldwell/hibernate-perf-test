package net.blakecaldwell.hpt;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import junit.framework.Assert;
import net.blakecaldwell.hpt.config.MainContext;
import net.blakecaldwell.hpt.entity.CustomerEntity;
import net.blakecaldwell.hpt.repository.CustomerEntityDao;
import net.blakecaldwell.hpt.repository.CustomerEntityRepository;
import net.blakecaldwell.hpt.service.SampleDataLoaderService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test fixture to compare Hibernate query performance for large data
 * sets using different approaches.
 * 
 * Remarks: Data is loaded when the Spring context refreshes, via
 * ContextRefreshedListener.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextHierarchy(@ContextConfiguration(classes = { MainContext.class }))
@TransactionConfiguration(defaultRollback = true)
public class HibernateQueryPerformanceTest
{
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * The number of test runs per query approach.
	 */
	@Value("${db.numberOfTestRuns}")
	private int numberOfRuns;

	/**
	 * The number of records expected in our database.
	 */
	@Value("${db.numberOfCustomersToLoad}")
	private int numberOfRecordsExpected;

	/**
	 * Loads up all of our sample data.
	 */
	@Autowired
	private SampleDataLoaderService sampleDataLoader;

	/**
	 * Repository for speed test.
	 */
	@Autowired
	private CustomerEntityRepository customerEntityRepository;

	/**
	 * Custom DAO for speed test.
	 */
	@Autowired
	private CustomerEntityDao customerEntityDao;

	@Test
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runJpaRepositoryTests()
	{
		long start;
		List<CustomerEntity> allCustomers;
		float totalTime = 0.0f;

		System.out.println("\n---------------------------");
		System.out.println("Testing JpaRepository query");
		for (int i = 0; i <= numberOfRuns; i++)
		{
			// clear the entity manager to get rid of any first-level
			// entity caching
			entityManager.clear();

			// try repository first
			start = System.currentTimeMillis();
			allCustomers = customerEntityRepository.findAll();
			if (i > 0)
			{
				// ignore the first run, in case there's a performance
				// penalty for starting the transaction
				totalTime += System.currentTimeMillis() - start;
			}
			Assert.assertEquals(numberOfRecordsExpected, allCustomers.size());
		}

		System.out.println("Total # of runs: " + numberOfRuns);
		System.out.println("JpaRepository avg time: " + (totalTime / numberOfRuns) + "ms");
	}

	@Test
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runRowDataPerformanceTests()
	{
		long start;
		List<CustomerEntity> allCustomers;
		float totalTime = 0.0f;

		System.out.println("\n---------------------------");
		System.out.println("Testing RowData query");
		for (int i = 0; i <= numberOfRuns; i++)
		{
			// clear the entity manager to get rid of any first-level
			// entity caching
			entityManager.clear();

			// now try from row data
			start = System.currentTimeMillis();
			allCustomers = customerEntityDao.fetchAllCustomerEntitiesWithAliasToBeanResultTransformer();

			if (i > 0)
			{
				// ignore the first run, in case there's a performance
				// penalty for starting the transaction
				totalTime += System.currentTimeMillis() - start;
			}
			Assert.assertEquals(numberOfRecordsExpected, allCustomers.size());
		}

		System.out.println("Total # of runs: " + numberOfRuns);
		System.out.println("RowData avg time: " + (totalTime / numberOfRuns) + "ms");
	}

	@Test
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runAliasToBeanResultTransformerTests()
	{
		long start;
		List<CustomerEntity> allCustomers;
		float totalTime = 0.0f;

		System.out.println("\n---------------------------");
		System.out.println("Testing ResultTransformer query");
		for (int i = 0; i <= numberOfRuns; i++)
		{
			// clear the entity manager to get rid of any first-level
			// entity caching
			entityManager.clear();

			// now try AliasToBeanResultTransformer
			start = System.currentTimeMillis();
			allCustomers = customerEntityDao.fetchAllCustomerEntitiesWithAliasToBeanResultTransformer();
			if (i > 0)
			{
				// ignore the first run, in case there's a performance
				// penalty for starting the transaction
				totalTime += System.currentTimeMillis() - start;
			}
			Assert.assertEquals(numberOfRecordsExpected, allCustomers.size());
		}

		System.out.println("Total # of runs: " + numberOfRuns);
		System.out.println("ResultTransformer avg time: " + (totalTime / numberOfRuns) + "ms");
	}

	/**
	 * No transaction here - we're opening one up ourselves in the
	 * DAO.
	 */
	@Test
	public void runStatelessSessionTests()
	{
		long start;
		List<CustomerEntity> allCustomers;
		float statelessSessionTotalTime = 0.0f;

		System.out.println("\n---------------------------");
		System.out.println("Testing stateless session query");
		for (int i = 0; i <= numberOfRuns; i++)
		{
			// now try from stateless sessions
			start = System.currentTimeMillis();
			allCustomers = customerEntityDao.fetchAllCustomerEntitiesWithStatelessSession();
			if (i > 0)
			{
				// ignore the first run, in case there's a performance
				// penalty for starting the transaction
				statelessSessionTotalTime += System.currentTimeMillis() - start;
			}
			Assert.assertEquals(numberOfRecordsExpected, allCustomers.size());

		}

		System.out.println("Total # of runs: " + numberOfRuns);
		System.out.println("Stateless Session avg time: " + (statelessSessionTotalTime / numberOfRuns) + "ms");
	}
}
