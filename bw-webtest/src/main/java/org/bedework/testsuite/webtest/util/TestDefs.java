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
package org.bedework.testsuite.webtest.util;

/** Global test strings
*
* @author johnsa
*
*/
public class TestDefs {

  /** Browser drivers */
  public enum DriverType {
    HTMLUNIT, FIREFOX, IE, CHROME;
  }
  /** Logout string - found in the URL */
  public static final String logoutText = "logout=true";

  /** Admin client strings for testing - assumes we are using en_US locale */
  public static final String adminFooter = "Bedework Website | show XML | refresh XSLT";
  public static final String adminEventInfoTitle = "Event Information";
  public static final String adminErrorUpdateEventTopicalArea = "please supply at least one topical area";
  public static final String adminErrorUpdateEventNoTitle = "please supply a title";
  public static final String adminErrorUpdateEventNoDescription = "please supply a description";
  public static final String adminErrorUpdateEventNoLocation = "please supply a location";
  public static final String adminErrorUpdateEventNoContact = "please supply a contact";

  /** Public client strings for testing */
  public static final String publicFooter = "This theme is based on work by Duke and Yale Universities with thanks also to the University of Chicago";

  /** Personal client strings for testing */
  public static final String personalFooter = "Demonstration calendar; place footer information here.";
  public static Object userManageCalTitle = "Manage Calendars & Subscriptions";

  /** Submissions client strings for testing */
  public static final String submissionsFooter = "Based on the";

}
