package pages.web;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * HomePage for Eventbrite - Main page with navigation and search functionality
 * <p>
 * This class represents the Eventbrite main page and encapsulates all functionality
 * available without authentication, including event search, category navigation,
 * and access to different application sections.
 * <p>
 * Extends BasePage to leverage the advanced wait system and logging.
 */
public class HomePage extends BasePage {

    // ========== HEADER NAVIGATION ELEMENTS ========== //

    @FindBy(css = "[data-heap-id='seo-global-nav-logo-desktop-click']")
    private WebElement eventbriteLogo;

    @FindBy(css = "input[placeholder*='Search events'], input[placeholder*='Buscar eventos']")
    private WebElement searchEventsField;

    @FindBy(css = "button.searchButton, button[type='button']")
    private WebElement searchButton;

    @FindBy(linkText = "Log In")
    private WebElement loginLink;

    @FindBy(linkText = "Sign Up")
    private WebElement signUpLink;

    // Alternative selectors for Spanish version
    @FindBy(linkText = "Iniciar sesión")
    private WebElement loginLinkSpanish;

    @FindBy(linkText = "Registrarse")
    private WebElement signUpLinkSpanish;

    // ========== MAIN CONTENT ELEMENTS ==========

    @FindBy(css = "[data-testid='icon-category-browse']")
    private WebElement mainEventbriteCategories;

    @FindBy(css = "[data-testid='category-card'], [href*='music']")
    private WebElement musicCategory;

    @FindBy(css = "[data-testid='category-card'], [href*='business']")
    private WebElement businessCategory;

    @FindBy(css = "[data-testid='category-card'], [href*='food']")
    private WebElement foodCategory;

    @FindBy(css = "[data-testid='category-card'], [href*='nightlife']")
    private WebElement nightlifeCategory;

    // ========== LOCATION ELEMENTS ==========

    @FindBy(css = "input[placeholder*='location'], input[placeholder*='ubicación'], input[placeholder*='Ashburn']")
    private WebElement locationSelector;

    @FindBy(css = "button[data-testid='location-button'], .location-button")
    private WebElement locationButton;

    // ========== ADDITIONAL CONTENT ELEMENTS ==========

    @FindBy(css = "[data-testid='icon-category-browse'], .iconCategoryBrowse")
    private WebElement categoriesSection;

    @FindBy(css = ".featured-events, .popular-events")
    private WebElement featuredEventsSection;

    /**
     * Constructor that initializes the page and its elements
     *
     * @param driver WebDriver instance to interact with the page
     */
    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);

        log().info("HomePage initialized for Eventbrite");
    }

    // ========== VERIFICATION METHODS ==========

    /**
     * Verifies that the main page has loaded correctly
     * Uses the advanced wait system from BasePage
     *
     * @return this same instance to allow method chaining
     */
    public HomePage verifyHomePageLoaded() {
        log().debug("Verifying complete load of Eventbrite main page");

        // Verify basic page load using inherited method
        verifyPageLoaded();

        // Verify specific main page elements
        waitFor(eventbriteLogo).toBeVisible().withTimeout(10);
        waitFor(searchEventsField).toBeVisible().withTimeout(10);
        waitFor(mainEventbriteCategories).toBeVisible().withTimeout(15);

        log().info("Eventbrite main page loaded and verified successfully");
        return this;
    }

    /**
     * Verifies that all main navigation elements are present and accessible
     *
     * @return this same instance to allow method chaining
     */
    public HomePage verifyNavigationElementsPresent() {
        log().debug("Verifying presence of main navigation elements");

        waitFor(searchEventsField).toBeVisible().withTimeout(10);
        waitFor(getActiveLoginLink()).toBeVisible().withTimeout(10);
        waitFor(categoriesSection).toBeVisible().withTimeout(15);

        log().info("All main navigation elements verified successfully");
        return this;
    }

    // ========== SEARCH FUNCTIONALITY ==========

    /**
     * Method to search of an event using a searchTerm
     *
     * @param searchTerm String with the term to search for
     * @return SearchResultsPage new instance of the search results page
     */
    public SearchResultsPage searchForEvents(String searchTerm) {
        log().info("Starting event search with term: {}", searchTerm);

        waitFor(searchEventsField).toBeClickable().withTimeout(10);
        searchEventsField.clear();
        searchEventsField.click();

        log().debug("Typing search term: {}", searchTerm);
        searchEventsField.sendKeys(searchTerm + Keys.ENTER);

        log().info("Search initiated successfully for term: {}", searchTerm);
        return new SearchResultsPage(getDriver());
    }

    /**
     * This method handles both search term and location parameters
     *
     * @param searchTerm String with the event search term
     * @param location String with the location to search in
     * @return SearchResultsPage new instance of the search results page
     */
    public SearchResultsPage searchForEventsWithLocation(String searchTerm, String location) {
        log().info("Starting advanced search - Term: {}, Location: {}", searchTerm, location);

        waitFor(locationSelector).toBeClickable().withTimeout(10);
        locationSelector.click();

        log().debug("Typing location term: {}", location);
        locationSelector.sendKeys(location);

        return searchForEvents(searchTerm);
    }

    // ========== NAVIGATION METHODS ==========

    /**
     * Method to enter into Login Page
     *
     * @return LoginPage new instance of the login page
     */
    public LoginPage navigateToLogin() {
        log().info("Navigating to login page");

        WebElement finalLogIn = getActiveLoginLink();

        waitFor(finalLogIn).toBeClickable().withTimeout(5);
        loginLink.click();

        return new LoginPage(getDriver());
    }

    /**
     * Method to enter into Sign-up Page
     *
     * @return SignUpPage new instance of the sign-up page
     */
    public SignUpPage navigateToSignUp() {
        log().info("Navigating to sign-up page");

        WebElement finalSignUp = getActiveSignUpLink();

        waitFor(finalSignUp).toBeClickable().withTimeout(5);
        finalSignUp.click();

        return new SignUpPage(getDriver());
    }

    /**
     * Method to navigate into different Category Pages
     *
     * @param categoryName String with the category name (e.g., "Music", "Business")
     * @return CategoryPage new instance of the category page
     */
    public CategoryPage navigateToCategory(String categoryName) {
        log().info("Navigating to category: {}", categoryName);

        if (!isCategoryAvailable(categoryName)) {
            throw new IllegalArgumentException("Category not available: " + categoryName);
        }

        WebElement categoryElement = getCategoryElement(categoryName);

        if (categoryElement == null) {
            log().error("Unexpected null category element after validation passed for: {}", categoryName);
            throw new IllegalStateException("Category validation succeeded but element is null: " + categoryName);
        }

        waitFor(categoryElement).toBeClickable().withTimeout(5);
        categoryElement.click();

        return new CategoryPage(getDriver());
    }

    // ========== UTILITY METHODS ==========

    /**
     * Helper method to get the active login link (handles both English and Spanish)
     *
     * @return WebElement the login link that is currently visible
     */
    private WebElement getActiveLoginLink() {
        try {
            if (loginLink.isDisplayed()) {
                return loginLink;
            }
        } catch (Exception e) {
            log().debug("English login link not found, trying Spanish version");
        }

        return loginLinkSpanish;
    }

    /**
     * Helper method to get the active sign up link (handles both English and Spanish)
     *
     * @return WebElement the sign-up link that is currently visible
     */
    private WebElement getActiveSignUpLink() {
        try {
            if (signUpLink.isDisplayed()) {
                return signUpLink;
            }
        } catch (Exception e) {
            log().debug("English sign up link not found, trying Spanish version");
        }

        return signUpLinkSpanish;
    }

    /**
     * Checks if the page is currently displaying the main promotional content
     *
     * @return boolean true if main hero section is visible
     */
    public boolean isMainContentVisible() {
        try {
            return waitFor(mainEventbriteCategories).toBeVisible().withTimeout(5) != null;
        } catch (Exception e) {
            log().debug("Main content not visible: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the current main promotional text from the hero section
     *
     * @return String the text content of the main hero title
     */
    public String getMainHeroText() {
        waitFor(mainEventbriteCategories).toBeVisible().withTimeout(10);
        String heroText = mainEventbriteCategories.getText();
        log().debug("Retrieved hero text: {}", heroText);
        return heroText;
    }

    /**
     * Verifies that a specific category is available for navigation
     *
     * @param categoryName String name of the category to check
     * @return boolean true if the category is visible and clickable
     */
    public boolean isCategoryAvailable(String categoryName) {
        log().debug("Checking availability of category: {}", categoryName);

        WebElement categoryElement = getCategoryElement(categoryName);
        if (categoryElement == null) {
            return false;
        }

        try {
            return waitFor(categoryElement).toBeVisible().withTimeout(5) != null;
        } catch (Exception e) {
            log().debug("Category {} not available: {}", categoryName, e.getMessage());
            return false;
        }
    }

    /**
     * Private helper method to get category element by name
     *
     * @param categoryName String name of the category
     * @return WebElement corresponding to the category, or null if not found
     */
    private WebElement getCategoryElement(String categoryName) {
        return switch (categoryName.toLowerCase()) {
            case "música", "music" -> musicCategory;
            case "business", "negocios" -> businessCategory;
            case "food", "gastronomía", "gastronomia" -> foodCategory;
            case "nightlife", "vida nocturna" -> nightlifeCategory;
            default -> {
                log().warn("Unknown category name: {}", categoryName);
                yield null;
            }
        };
    }
}