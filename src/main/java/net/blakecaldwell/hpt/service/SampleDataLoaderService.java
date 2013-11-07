package net.blakecaldwell.hpt.service;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.blakecaldwell.hpt.entity.CustomerEntity;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to load up sample data.
 */
@Component
public class SampleDataLoaderService
{
	private EntityManager entityManager;

	/**
	 * Get our entity manager from Spring.
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	/**
	 * Populate our database with sample data with CustomerEntity
	 * records.
	 * 
	 * @param customerCount
	 *            number of CustomerEntity records to insert
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void loadSampleData(final int customerCount)
	{
		System.out.println("Loading " + customerCount + " CustomerEntity records");
		for (int i = 1; i <= customerCount; i++)
		{
			// customer
			CustomerEntity customerEntity = new CustomerEntity();
			customerEntity.setFirstName(randomGuid());
			customerEntity.setLastName(randomGuid());

			if (i % 1000 == 0)
			{
				entityManager.flush();
				entityManager.clear();
				System.out.println("Loading customer " + i);
			}

			entityManager.persist(customerEntity);
			customerEntity = null;
		}

		entityManager.flush();
		entityManager.clear();
	}

	/**
	 * Return a random guid String.
	 */
	private String randomGuid()
	{
		return UUID.randomUUID().toString();
	}
}
