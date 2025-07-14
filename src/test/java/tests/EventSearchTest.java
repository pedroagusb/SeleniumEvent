package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import testrunner.BaseTest;

public class EventSearchTest extends BaseTest {
    @Test(description = "Search events and verify event detail page")
    public void searchEventsBasicFlow(){
        HomePage homePage = new HomePage(getDriver());

        String summaryEvent = homePage.verifyHomePageLoaded().
                verifyNavigationElementsPresent().
                searchForEvents("congreso"). //"running" no da resultados
                verifySearchResultsLoaded().
                clickFirstEvent().
                verifyEventDetailPageDisplayed().
                getEventSummary();

        log().info("Event summary retrieved: {}", summaryEvent);
        Assert.assertNotNull(summaryEvent, "Event summary should not be null");
    }

    @Test(description = "Search events with no results")
    public void searchEventWithoutResults(){
        HomePage homePage = new HomePage(getDriver());

        boolean areEventResults = homePage.verifyHomePageLoaded().
                verifyNavigationElementsPresent().
                searchForEvents("xyzzzzz1234&*^&!@#!@").
                verifySearchResultsLoaded().
                hasResults();

        log().info("Events found? {}", areEventResults);
        Assert.assertFalse(areEventResults, "Event results shouldn't be present");
    }

    @Test(description = "Search for free events and verify price")
    public void searchFreeEvents(){
        HomePage homePage = new HomePage(getDriver());

        boolean areFreeEvents = homePage.verifyHomePageLoaded().
                verifyNavigationElementsPresent().
                searchForEvents("").
                verifySearchResultsLoaded().
                selectFreeEvent().
                areAllFreeEvents();

        log().info("Are all events found free? {}", areFreeEvents);
        Assert.assertTrue(areFreeEvents);
    }
}
