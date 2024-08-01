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
package org.bedework.testsuite.webtest.publiceventsadministration;

import org.bedework.testsuite.webtest.util.SeleniumUtil;
import org.bedework.testsuite.webtest.util.TestDefs;

import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author johnsa
 *
 */
public class AddPublicEventTestCase {
  private WebDriver d;
  private WebDriverWait wait;
  private Select select;

  /**
   */
  @Before
  public void setUpBeforeClass() {
    System.out.println("\n\n*********************************");
    System.out.println("Test \"Add Public Event\" starting.\n");
    d = SeleniumUtil.getWebDriver();
    wait = SeleniumUtil.getWebDriverWait();
    SeleniumUtil.login("admin","vbede","bedework"); // log in as a typical event admin
  }

  /**
   */
  @After
  public void tearDownAfterClass() {
    // SeleniumUtil.logout(); // why bother - we're closing the browser anyway.
    System.out.println("Test \"Add Public Event\" complete.\n");
  }

  /**
   */
  @Test
  public void testProcess() {
    final String uuid = UUID.randomUUID().toString();
    final String eventTitle = "SELENIUM - CreatePubEventsTest - " + uuid;

    // get to the Add Event page
    d.findElement(By.xpath("//a[contains(@href,'initAddEvent.do')]")).click();
    SeleniumUtil.checkPage("admin");

    assertEquals(d.findElement(By.tagName("h2")).getText(),TestDefs.adminEventInfoTitle);
    System.out.println("On " + TestDefs.adminEventInfoTitle + " page.");

    // test basic validation without filling in the form
    d.findElement(By.name("addEvent")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("errors")));
    MatcherAssert.<String>assertThat(
            "Error should be thrown for no topical area: ",
            d.findElement(By.id("errors")).getText(),
            containsString(
                    TestDefs.adminErrorUpdateEventTopicalArea));

    // we must select a topical area to get to the next errors
    d.findElement(By.xpath("//input[@name='alias' and @value='/user/agrp_calsuite-MainCampus/Arts/Concerts']")).click();

    // test next validation error (no title)
    d.findElement(By.name("addEvent")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("errors")));
    assertThat("Error should be thrown for 'no title': ",
               d.findElement(By.id("errors")).getText(),
               containsString(TestDefs.adminErrorUpdateEventNoTitle));

    // add a title
    d.findElement(By.name("summary")).sendKeys(eventTitle);

    // test next validation error (no description)
    d.findElement(By.name("addEvent")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("errors")));
    assertThat("Error should be thrown for 'no description': ",
               d.findElement(By.id("errors")).getText(),
               containsString(TestDefs.adminErrorUpdateEventNoDescription));

    // add a description
    d.findElement(By.id("description")).sendKeys("selenium test description");

    // test next validation error (no location)
    d.findElement(By.name("addEvent")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("errors")));
    assertThat("Error should be thrown for 'no location': ",
               d.findElement(By.id("errors")).getText(),
               containsString(TestDefs.adminErrorUpdateEventNoLocation));

    // select a location
    d.findElement(By.id("bwLocationAllButton")).click();
    select = new Select(d.findElement(By.id("bwAllLocationList")));
    select.selectByIndex(1);

    // test next validation error (no contact)
    d.findElement(By.name("addEvent")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("errors")));
    assertThat("Error should be thrown for 'no contact': ",
               d.findElement(By.id("errors")).getText(),
               containsString(TestDefs.adminErrorUpdateEventNoContact));

    // select a contact
    d.findElement(By.id("bwContactAllButton")).click();
    select = new Select(d.findElement(By.id("bwAllContactList")));
    select.selectByIndex(1);

    // FILL in the rest of the form
    // ================================================

    // select the main calendar (only do the following if we're in as 'admin')
    /* d.findElement(By.id("toggleCalendarListsAll")).click();
     select = new Select(d.findElement(By.id("bwAllCalendars")));
     select.selectByValue("/public/cals/MainCal"); */

    // set the time to 2pm; the date should be "today" by default
    select = new Select(d.findElement(By.id("eventStartDateHour")));
    select.selectByIndex(14);
    select = new Select(d.findElement(By.id("eventStartDateMinute")));
    select.selectByIndex(0);

    // set the cost and link
    d.findElement(By.name("eventCost")).sendKeys("FREE");
    d.findElement(By.name("eventLink")).sendKeys("http://www.jasig.org/bedework");

    // image and thumbnail URLs
    // we'll check image uploads in update event test, because we need to also test image removal
    d.findElement(By.id("xBwImageHolder")).sendKeys("http://www.jasig.org/sites/jasig.webchuckhosting.com/files/bedework_logo.jpg");
    d.findElement(By.id("xBwImageThumbHolder")).sendKeys("http://www.jasig.org/misc/feed.png");

    // submit the event
    d.findElement(By.name("addEvent")).click();

    // ****************************************
    // Now test the event in the public client.
    System.out.println("Event is published.");
    System.out.println("Waiting ten seconds for indexer to get the event in the public client....");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Now checking event in public web client.");

    d.get("http://localhost:8080/cal/setup.do");
    SeleniumUtil.checkPage("public");

    // The event should exist today.  It should be on the current page.
    // The following will fail out if not found:
    final WebElement element = d.findElement(By.xpath("//li[@class='titleEvent']/a[contains(text(),'" + uuid + "')]"));

    // We found it, now click the link to visit the event detail page.
    element.click();
    System.out.println("Event \"" + uuid + "\" found.");

    assertThat("Time should be at \"2:00 PM\": ",
               d.findElement(By.xpath("//div[@class='eventWhen']/span[@class='time']")).getText(),
               containsString("2:00 PM"));
    System.out.println("Time matches 2:00 PM");

    // THIS IS TEMPORARY.
    // just to pause a moment and watch the work prior to teardown.
    // sleep will be removed.
    try {
      Thread.sleep(8000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
