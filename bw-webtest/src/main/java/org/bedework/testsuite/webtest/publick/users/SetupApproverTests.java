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

import org.bedework.testsuite.webtest.publick.PublicAdminTestBase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author johnsa
 *
 */
@DisplayName("Setup approver user for later tests")
public class SetupApproverTests extends PublicAdminTestBase {
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
  public void doApproverSetup() {
    // Login as a superuser
    adminLogin(getProperty(propAdminSuperUser),
               getProperty(propAdminSuperUserPw));

    userRolesPage();

    // In general, we may have to add the user to the page by setting a role
    getAdminPageByHrefSeg(getProperty(propApproverPrincipal));
    if (setCheckboxValueIfNeeded("editAuthUserApprover", true)) {
      clickByName("modAuthUser");
    }

    // Now add to group
    adminGroupPage(getProperty(propApproverUserGroupName));
    addUserMemberIfNeeded(getProperty(propApproverUser));

    logout();

    // Log in to admin client and check visibility of elements
    adminLogin(getProperty(propApproverUser),
               getProperty(propApproverUserPw));

    assertThat("Should not see user and system tabs",
               presentByXpath(getProperty(propAdminTabMainPath)) &&
                       presentByXpath(getProperty(propAdminTabApprovalqPath)) &&
                       presentByXpath(getProperty(propAdminTabSuggestionqPath)) &&
                       presentByXpath(getProperty(propAdminTabPendingqPath)) &&
                       presentByXpath(getProperty(propAdminTabCalendarSuitePath)) &&
                       !presentByXpath(getProperty(propAdminTabUsersPath)) &&
                       !presentByXpath(getProperty(propAdminTabSystemPath)));

  }

}
