package net.blakecaldwell.hpt.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.blakecaldwell.hpt.entity.CustomerEntity;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.stereotype.Component;

/**
 * DAO with methods to test fetching CustomerEntity data with
 * different approaches.
 */
@Component
public class CustomerEntityDao
{
	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	/**
	 * Fetch disconnected customer entities as DTOs with
	 * AliasToBeanResultTransformer.
	 */
	@SuppressWarnings("unchecked")
	public List<CustomerEntity> fetchAllCustomerEntitiesWithAliasToBeanResultTransformer()
	{
		Session session = (Session) entityManager.getDelegate();
		//@formatter:off
		return session.createQuery(
				"SELECT " +
					"id as id, " +
					"firstName as firstName, " +
					"lastName as lastName " +
				"FROM CustomerEntity")
				.setResultTransformer(new AliasToBeanResultTransformer(CustomerEntity.class))
				.list();
		//@formatter:on
	}

	/**
	 * Fetch disconnected customer entities as DTOs by manually
	 * building them from table data.
	 */
	@SuppressWarnings("unchecked")
	public List<CustomerEntity> fetchAllCustomerEntitiesWithTableData()
	{
		//@formatter:off
		List<Object[]> rows = entityManager.createQuery(
				"SELECT " +
					"id, " +
					"firstName, " +
					"lastName " +
				"FROM " +
					"CustomerEntity")
				.getResultList();
		//@formatter:on

		List<CustomerEntity> results = new ArrayList<CustomerEntity>();
		CustomerEntity customerEntity;
		for (Object[] row : rows)
		{
			customerEntity = new CustomerEntity();
			customerEntity.setId((Long) row[0]);
			customerEntity.setFirstName((String) row[1]);
			customerEntity.setLastName((String) row[2]);
			results.add(customerEntity);
		}
		return results;
	}

	/**
	 * Use StatelessSession to fetch all CustomerEntities,
	 * disconnected.
	 * 
	 * More info:
	 * 
	 * http://docs.jboss.org/hibernate/orm/4.3/devguide/en-US/
	 * html_single/#d5e990
	 */
	public List<CustomerEntity> fetchAllCustomerEntitiesWithStatelessSession()
	{
		Session regularSession = (Session) entityManager.getDelegate();
		SessionFactory sessionFactory = regularSession.getSessionFactory();

		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = session.beginTransaction();

		ScrollableResults customers = session.createQuery("FROM CustomerEntity")
		        .scroll(ScrollMode.FORWARD_ONLY);

		List<CustomerEntity> results = new ArrayList<CustomerEntity>();
		while (customers.next())
		{
			results.add((CustomerEntity) customers.get(0));
		}

		tx.commit();
		session.close();

		return results;
	}

}
