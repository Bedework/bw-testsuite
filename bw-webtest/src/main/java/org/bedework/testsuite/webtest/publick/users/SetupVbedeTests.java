/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.bedework.testsuite.webtest.publick.users;

import org.bedework.testsuite.webtest.util.TestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * @author johnsa
 *
 */
@DisplayName("Setup user vbede for later tests")
public class SetupVbedeTests extends TestBase {
  private static final String testName = "Setup vbede";


  /**
   */
  @AfterEach
  public void tearDownAfterTest() {
    closeDriver();
  }

  /**
   */
  @Test
  @Order(1)
  public void doVbedeSetup() {
    // Login as a superuser
    login("admin","admin","bedework");

    // get to the user roles page
    gotoAdminPage("showUsersTab.rdo");
    gotoAdminPage("authuser/initUpdate.do");

    // In general, we may have to add the user to the page by setting a role
    gotoAdminPage("/principals/users/vbede");
    if (setCheckboxValueIfNeeded("editAuthUserContentAdmin", true)) {
      clickByName("modAuthUser");
    }

    // Now add to group
    gotoAdminPage("showUsersTab.rdo");
    gotoAdminPage("admingroup/initUpdate.do");
    gotoAdminPage("admingroup/fetchForUpdateMembers.do?b=de&adminGroupName=calsuite-MainCampus");

    // Is vbede in members
    if (!tableHasElementText("memberAccountList", "vbede")) {
      setTextById("agMember", "vbede");
      clickByName("addGroupMember");
    }

    logout();
    System.out.println("Test \"" + testName + "\" finished.");
  }

}