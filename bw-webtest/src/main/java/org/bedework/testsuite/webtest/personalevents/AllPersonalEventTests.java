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
package org.bedework.testsuite.webtest.personalevents;

import org.bedework.testsuite.webtest.util.SeleniumUtil;
import org.bedework.testsuite.webtest.util.TestDefs.DriverType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AddPublicSubTestCase.class })
public class AllPersonalEventTests {

  /**
   */
  @BeforeClass
  public static void setUpBeforeClass() {
    SeleniumUtil.setDriverType(DriverType.FIREFOX);
  }

  /**
   */
  @AfterClass
  public static void tearDownAfterClass() {
    SeleniumUtil.closeDriver();
  }
}

