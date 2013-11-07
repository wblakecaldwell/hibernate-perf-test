package net.blakecaldwell.hpt.entity;

import javax.persistence.Entity;

@Entity
public class CustomerEntity extends EntityBase
{
	private String firstName;

	private String lastName;

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}
