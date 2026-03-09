package org.bedework.testsuite.webtest.eventreg;

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

public class EventregBase extends PublicAdminTestBase {
  @Override
  public String getClientName() {
    return getProperty("eventregClientName");
  }
}
