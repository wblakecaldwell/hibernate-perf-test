hibernate-perf-test
===================

Compare large dataset query performance using different approaches in Hibernate.

What is this?
-------------

This is a simple Maven Spring project that creates an in-memory HSQLDB database, populates it 500,000 records, and then uses several Hibernate query strategies to fetch every one, and report on their average execution times.

Approaches Tested
-----------------

1. Using a [JpaRepository](http://docs.spring.io/spring-data/jpa/docs/dev/api/org/springframework/data/jpa/repository/JpaRepository.html) interface's [findAll](http://docs.spring.io/spring-data/jpa/docs/dev/api/org/springframework/data/jpa/repository/JpaRepository.html#findAll\(\)) method to return a list of attached Hibernate entities.

2. Using Hibernate's [StatelessSession](http://docs.jboss.org/hibernate/orm/3.3/reference/en-US/html/batch.html#batch-statelesssession) interface to return a list of detached Hibernate entities.

3. Selecting the specific fields of the entity, using Hibernate to return a simple List<Object[]>, and then manually converting that list to a list of detached entities (as DTOs, basically).

4. Selecting the specific fields of the entity, then using Hibernate's [AliasToBeanResultTransformer](http://docs.jboss.org/hibernate/orm/3.3/api/org/hibernate/transform/AliasToBeanResultTransformer.html) to build a list of detached entities (as DTOs, basically).

Changing Execution Parameters
-----------------------------

By default, the database is loaded with 500,000 records, and each test is repeated in its own transaction 10 times. You can change both of these values in [src/main/resources/application.properties](https://github.com/wblakecaldwell/hibernate-perf-test/blob/master/src/main/resources/application.properties).

Running Tests
-------------

This big of a database does take up over 256MB of memory, so you might have to increase your heap space. If you run the tests from Maven, you should be fine, since I increase it in the plugin's settings.

Download the sample project and run the following command from inside its directory:

  	mvn clean test

The test might take several minutes to run. At the end, the test will output the results.

My Results
----------

The results are listed slowest to fastest:

	  ---------------------------
	  Testing JpaRepository query
	  Total # of runs: 10
	  JpaRepository avg time: 1073.5ms
	  
	  ---------------------------
	  Testing stateless session query
	  Total # of runs: 10
	  Stateless Session avg time: 818.5ms
	
	  ---------------------------
	  Testing RowData query
	  Total # of runs: 10
	  RowData avg time: 317.7ms
	  
	  ---------------------------
	  Testing ResultTransformer query
	  Total # of runs: 10
	  ResultTransformer avg time: 311.9ms
  

The individual times will vary on different systems - the relative performance is what's important. 

The StatelessSession query was a little more efficient than returning attached entities, but still pretty slow for this big query. The AliasToBeanResultTransformer and my custom List<Object[]> -> DTO approaches tied as the best performers. I was hoping for this result, but worried that the reflective nature of AliasToBeanResultTransformer might have introduced some overhead. It did not.


Problems? Let me know!
----------------------

I tried being as careful as I could with these tests:

- took the average of several test runs
- turned off Hibernate's second-level cache
- turned off Hibernate's query cache
- cleared the entity manager before each run
- ran each test in its own transaction
- ignored the first query in the transaction as to avoid any initial performance hit from opening it
 
I encourage you to download the project, take a look at the code, and try it out for yourself. If you see any issues with my methodology, please let me know, and I'll correct for it.
