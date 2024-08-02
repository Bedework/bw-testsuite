package org.bedework.testsuite.webtest.personal;

import org.bedework.testsuite.webtest.util.SeleniumUtil;
import org.bedework.testsuite.webtest.util.TestDefs;

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
public class AddPublicSubTestCase {
  private WebDriver d;
  private WebDriverWait wait;
  private WebElement element;
  private Select select;

  /**
   */
  @Before
  public void setUpBeforeClass() {
    System.out.println("\n\n*********************************");
    System.out.println("Test \"Add Public Subscription\" starting.\n");
    d = SeleniumUtil.getWebDriver();
    wait = SeleniumUtil.getWebDriverWait();
    SeleniumUtil.login("personal","vbede","bedework"); // log in as a typical user
  }

  /**
   */
  @After
  public void tearDownAfterClass() {
    System.out.println("Test \"Add Public Subscription\" complete.\n");
  }

  /**
   */
  @Test
  public void testProcess() {
    final String subFinder = UUID.randomUUID().toString().substring(0, 4);

    // get to the Add Event page
    d.findElement(By.xpath("//a[@class='calManageLink']")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("footer")));

    assertEquals(d.findElement(By.tagName("h2")).getText(),TestDefs.userManageCalTitle);
    System.out.println("On " + TestDefs.userManageCalTitle + " page.");

    // click the add subscription button
    d.findElement(By.id("addSubButton")).click();
    wait.until(SeleniumUtil.visibilityOfElementLocated(By.id("footer")));

    // click the public listing toggle
    d.findElement(By.id("subSwitchPublic")).click();
    d.findElement(By.xpath("//ul[@id='publicSubscriptionTree']//li[@class='alias']/a = 'Lectures']")).click();

    // determine if the display name updated
    assertEquals(d.findElement(By.id("intSubDisplayName")).getText(),"Lectures");
    // change to a new display name
    d.findElement(By.id("intSubDisplayName")).sendKeys("Lec" + subFinder);

    // add the subscription
    d.findElement(By.id("intSubSubmit")).click();

    // *****************************************************************
    // Now test the subscription is there in the normal calendar listing
    assertThat("Subscription not created.",
               d.findElement(By.xpath("//td[@id='sidebar']//li[@class='alias']/a = 'Lec'" + subFinder + "]")).getText(),
               containsString(subFinder)); // this is dumb ... should just check for existence
    System.out.println("Public subscription added.");


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
