package net.blakecaldwell.hpt.repository;

import net.blakecaldwell.hpt.entity.CustomerEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository for our CustomerEntity.
 */
public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long>
{
}
