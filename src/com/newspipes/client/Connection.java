package com.newspipes.client;

import javax.jdo.annotations.*;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Connection {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String firstName;

  @Persistent
  private String lastName;

  public Connection(final Long id, final String firstName, final String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
