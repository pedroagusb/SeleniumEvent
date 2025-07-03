package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * SearchResultsPage for Eventbrite - Handles event search results and filtering
 * <p>
 * This class represents the page that displays search results after a user
 * searches for events. It provides functionality to verify results, extract
 * event information, apply filters, and navigate to specific events.
 * <p>
 * Extends BasePage to leverage the advanced wait system and logging.
 */
public class SearchResultsPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(SearchResultsPage.class);

    // ========== MAIN RESULTS ELEMENTS ==========

    @FindBy(css = ".search-results-container, .events-list, [data-testid='search-results']")
    private WebElement resultsContainer;

    @FindBy(css = "[data-testid='search-event']")
    private List<WebElement> eventCards;

    @FindBy(className = "empty-state__body")
    private WebElement noResultsMessage;

    // ========== SEARCH AND FILTER ELEMENTS ==========

    @FindBy(css = "input[placeholder*='Search'], input[placeholder*='Buscar']")
    private WebElement searchField;

    // ========== PAGINATION ELEMENTS ==========

    @FindBy(css = ".pagination, .page-navigation, [data-testid='pagination']")
    private WebElement paginationContainer;

    /**
     * Constructor that initializes the page and its elements
     *
     * @param driver WebDriver instance to interact with the page
     */
    public SearchResultsPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

        log().info("SearchResultsPage initialized");
    }

    // ========== VERIFICATION METHODS ==========

    /**
     * Verifies that the search results page has loaded correctly
     * This should wait for key elements and confirm we're on the right page
     *
     * @return this same instance to allow method chaining
     */
    public SearchResultsPage verifySearchResultsLoaded() {
        log().debug("Verifying search results page has loaded");

        // Verify basic page load using inherited method
        verifyPageLoaded();

        // Wait for the results container to be visible
        waitFor(resultsContainer).toBeVisible().withTimeout(15);

        log().info("Search results page loaded and verified successfully");
        return this;
    }

    /**
     * Verifies that search results are displayed (not an empty results page)
     * Should check that we have actual events showing, not a "no results" message
     *
     * @return this same instance to allow method chaining
     */
    public SearchResultsPage verifyResultsDisplayed() {
        log().debug("Verifying that search results are displayed");

        if(!verifySearchResultsLoaded().hasResults()){
            log().warn("The search didn't show any results on the list elements: {} ", eventCards);
        }

        log().info("Search results verified are displayed");
        return this;
    }

    // ========== RESULTS INFORMATION METHODS ==========

    /**
     * Verifies if there is no present the No Result Message, and returns true if there are Event Cards present.
     *
     * @return boolean true if events were found, false if no results
     */
    public boolean hasResults() {
        log().debug("Checking if search returned any results");

        if(!eventCards.isEmpty()){
            log().debug("Found {} event cards", eventCards.size());
            return true;
        }

        try {
            if(waitFor(noResultsMessage).toBeVisible().withTimeout(2) != null){
                log().debug("No results message confirmed");
            }
        } catch (Exception e) {
            log().debug("No explicit 'no results' message found, but no cards either");
        }

        return false;
    }

    public int getResultsCount() {
        return eventCards.size();
    }

    // ========== NAVIGATION METHODS ==========

    /**
     * This method waits for first event element to be clickable and then click it.
     *
     * @return EventDetailPage instance for the clicked event
     */
    public EventDetailPage clickFirstEvent() {
        log().info("Clicking on first event in search results");

        if(!hasResults()){
            throw new RuntimeException("No results available to click");
        }

        WebElement firstEvent = eventCards.get(0);
        waitFor(firstEvent).toBeClickable().withTimeout(5);
        firstEvent.click();

        return new EventDetailPage(getDriver());
    }

    /**
     * * This method waits for the event element by index to be clickable and then click it.
     *
     * @param eventIndex zero-based index of event to click (0 = first event)
     * @return EventDetailPage instance for the clicked event
     */
    public EventDetailPage clickEventByIndex(int eventIndex) {
        try {
            log().info("Clicking on event at index: {}", eventIndex);

            if(eventIndex >= eventCards.size() || eventIndex < 0){
                throw new InvalidArgumentException("Invalid event index: " + eventIndex);
            }

            if(!hasResults()){
                throw new RuntimeException("No results available to click");
            }

            WebElement eventElement = eventCards.get(eventIndex);
            waitFor(eventElement).toBeClickable().withTimeout(5);
            eventElement.click();

            return new EventDetailPage(getDriver());

        } catch (InvalidArgumentException e) {
            log.error("Index entered is invalid: {}. Error {}", eventIndex, e.getMessage());
            throw new RuntimeException();
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Gets the current search term shown in the search field
     * This is useful for verifying that search was performed correctly
     *
     * @return String current value in search field
     */
    public String getCurrentSearchTerm() {
        try {
            waitFor(searchField).toBeVisible().withTimeout(5);
            String currentTerm = searchField.getDomProperty("value");
            log().debug("Current search term: {}", currentTerm);
            return currentTerm != null ? currentTerm : "";
        } catch (Exception e) {
            log().warn("Could not retrieve current search term: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Checks if pagination controls are available
     * Useful for determining if there are more results to view
     *
     * @return boolean true if pagination is present and visible
     */
    public boolean hasPagination() {
        try {
            return waitFor(paginationContainer).toBeVisible().withTimeout(3) != null;
        } catch (Exception e) {
            log().debug("Pagination not available: {}", e.getMessage());
            return false;
        }
    }
}