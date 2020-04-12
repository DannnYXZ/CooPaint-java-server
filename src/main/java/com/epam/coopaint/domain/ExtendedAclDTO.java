package com.epam.coopaint.domain;

import java.util.List;
import java.util.Set;

public class ExtendedAclDTO {
  private List<UserResourceActions> users;
  private Set<ResourceAction> guests;

  public List<UserResourceActions> getUsers() {
    return users;
  }

  public ExtendedAclDTO setUsers(List<UserResourceActions> users) {
    this.users = users;
    return this;
  }

  public Set<ResourceAction> getGuests() {
    return guests;
  }

  public ExtendedAclDTO setGuests(Set<ResourceAction> guests) {
    this.guests = guests;
    return this;
  }
}
