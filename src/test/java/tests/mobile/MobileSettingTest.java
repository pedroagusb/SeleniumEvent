package tests.mobile;

import org.testng.annotations.Test;
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

}
