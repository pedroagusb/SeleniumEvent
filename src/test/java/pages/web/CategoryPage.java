package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CategoryPage extends BasePage{
    @FindBy(css = ".category-browse--header")
    private WebElement categoryHeader;

    @FindBy(css = "[data-testid='breadcrumb-final-element']")
    private WebElement finalCategoryBreadCrumb;

    public CategoryPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver,this);

        log().info("CategoryPage initialized");
    }

    public CategoryPage verifyCategoryPageDisplayed(){
        log().info("Verifying that the Category Page is displayed correctly");

        verifyPageLoaded();
        waitFor(categoryHeader).toBeVisible().withTimeout(15);

        log().info("Category page verified successfully");
        return this;
    }

    public boolean verifyCategoryName(String categoryName){
        log().info("Verifying that if the Category Name matches the selected one");

        verifyCategoryPageDisplayed().waitFor(finalCategoryBreadCrumb).toBeVisible().withTimeout(5);

        String categoryBreadCrumbText = finalCategoryBreadCrumb.getText();

        if(categoryBreadCrumbText.contains(categoryName)){
            log().info("The category selected {} matches with the category name in the breadcrumb", categoryName);

            return true;
        } else {
            log().error("The category name {} didn't match with the one in the breadcrumb: {} ", categoryName, categoryBreadCrumbText);

            return false;
        }
    }
}
