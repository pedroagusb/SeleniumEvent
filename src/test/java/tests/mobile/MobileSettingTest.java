package tests.mobile;

import org.testng.annotations.Test;
import pages.mobile.AppSectionPage;
import pages.mobile.NetworkSectionPage;
import pages.mobile.SettingsPage;
import testrunner.MobileBaseTest;

public class MobileSettingTest extends MobileBaseTest {
    @Test(description="Launch Setting app and verify it's displayed")
    public void launchSettingTest() {
        log().info("Starting first mobile test!");

        SettingsPage settingsPage = new SettingsPage(getDriver());

        settingsPage.verifySettingsPageLoaded();

        log().info("Settings page verification completed successfully!");

    }

    @Test(description = "Navigate from Setting page to Apps page")
    public void settingToAppsNavigation(){
        SettingsPage settingsPage = new SettingsPage(getDriver());
        AppSectionPage appPage = settingsPage.verifySettingsPageLoaded()
                .navigateToAppsSection()
                .verifyAppSectionLoaded();

        SettingsPage backToSettings = appPage.navigateBackToSettings();

        backToSettings.verifySettingsPageLoaded();
    }

    @Test(description = "Navigate from Setting page to Network page")
    public void settingsToNetworkNavigationTest() {
        SettingsPage settingsPage = new SettingsPage(getDriver());

        NetworkSectionPage networkPage = settingsPage.verifySettingsPageLoaded()
                .navigateToNetworkSection()
                .verifyNetworkSectionLoaded();

        SettingsPage backToSettings = networkPage.navigateBackToSettings();

        backToSettings.verifySettingsPageLoaded();
    }

}
