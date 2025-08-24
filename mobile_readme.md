# SeleniumEvent - Mobile Testing Framework 📱

## 🎯 Overview

Testing framework supporting **Web** and **Mobile** testing with unified architecture. This README focuses on the Mobile Testing capabilities using **Appium + Java**.

### 🏗️ Architecture
- **Web Testing**: Selenium + Java + TestNG
- **Mobile Testing**: Appium + Java + TestNG ⭐

### 📊 Framework Capabilities
```
✅ Cross-platform mobile testing (Android + iOS ready)
✅ Page Object Model implementation
✅ Advanced wait strategies
✅ Method chaining navigation
✅ Unified logging and reporting
✅ CI/CD ready configuration
```

---

## 📋 Prerequisites

### Required Software
- **Java 18+** - [Download OpenJDK](https://openjdk.org/)
- **Maven 3.8+** - [Download Maven](https://maven.apache.org/)
- **Node.js 18+** - [Download Node.js](https://nodejs.org/)
- **Android Studio** - [Download Android Studio](https://developer.android.com/studio)

### Operating System
- **macOS** (Recommended - supports both Android and iOS)
- **Windows** (Android only)
- **Linux** (Android only)

---

## ⚙️ Installation & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd SeleniumEvent
```

### 2. Install Maven Dependencies
```bash
mvn clean install
```

### 3. Android Studio Setup

#### Install Android Studio
1. Download and install Android Studio
2. Open Android Studio and go through initial setup
3. Install recommended SDK components

#### Configure Android SDK
1. **Android Studio → Preferences/Settings → Appearance & Behavior → System Settings → Android SDK**
2. Install the following SDK versions:
   - ✅ Android 13.0 (API 33) - Recommended
   - ✅ Android 12.0 (API 31) - Backup
   - ✅ Android 11.0 (API 30) - Optional

#### SDK Tools (Required)
In **SDK Tools** tab, ensure these are installed:
- ✅ Android SDK Build-Tools (latest)
- ✅ Android Emulator
- ✅ Android SDK Platform-Tools
- ✅ Android SDK Command-line Tools (latest)

### 4. Environment Variables

#### For macOS/Linux (add to ~/.zshrc or ~/.bash_profile):
```bash
# Android SDK Configuration
export ANDROID_HOME=$HOME/Library/Android/sdk
export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

#### For Windows (add to System Environment Variables):
```
ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
ANDROID_SDK_ROOT=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
PATH=%PATH%;%ANDROID_HOME%\emulator;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\cmdline-tools\latest\bin
```

#### Apply changes:
```bash
# macOS/Linux
source ~/.zshrc

# Windows
# Restart command prompt/PowerShell
```

### 5. Create Android Virtual Device (AVD)

#### Using Android Studio:
1. **Android Studio → Tools → AVD Manager**
2. **Create Virtual Device**
3. **Select Device**: Pixel 6 Pro (recommended)
4. **System Image**: Android 13.0 (API 33)
   - ⚠️ **Important**: For Apple Silicon Macs, use **arm64-v8a** images
5. **AVD Configuration**:
   - **RAM**: 4096 MB
   - **Internal Storage**: 16 GB
   - **Graphics**: Automatic

### 6. Install Appium

#### Install Appium Server:
```bash
# Install Appium globally
npm install -g appium

# Install Android driver
appium driver install uiautomator2

# Install verification tool
npm install -g @appium/doctor
```

#### Verify Installation:
```bash
# Check Android setup
appium-doctor --android

# Should show ✅ for all critical items
```

---

## 🚀 Running Tests

### 1. Start Android Virtual Device
```bash
# Option A: From Android Studio
Android Studio → AVD Manager → Click ▶️ on your AVD

# Option B: From command line
emulator -avd <your-avd-name>
```

### 2. Start Appium Server
```bash
# In a new terminal
appium

# Should see: [Appium] Appium REST http interface listener started on 127.0.0.1:4723
```

### 3. Run Mobile Tests

#### Single Test:
```bash
mvn test -Dtest=MobileSettingTest
```

#### All Mobile Tests:
```bash
mvn test -Dtest="tests.mobile.*"
```

#### With Specific Configuration:
```bash
mvn test -Dtest=MobileSettingTest -Dbrowser=MOBILE
```

---

## 📁 Project Structure

```
src/
├── main/java/
│   ├── driver/
│   │   ├── DriverFactory.java          # Multi-platform driver factory
│   │   ├── MobileDriverManager.java    # Mobile driver configuration
│   │   ├── ChromeDriverManager.java    # Web Chrome driver
│   │   └── FirefoxDriverManager.java   # Web Firefox driver
│   ├── pages/
│   │   ├── web/                        # Web page objects
│   │   │   ├── HomePage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   └── EventDetailPage.java
│   │   └── mobile/                     # Mobile page objects
│   │       ├── MobileBasePage.java     # Base mobile page class
│   │       ├── SettingsPage.java       # Settings screen
│   │       ├── AppSectionPage.java     # Apps section screen
│   │       └── NetworkSectionPage.java # Network section screen
│   ├── testrunner/
│   │   ├── BaseTest.java               # Web test base class
│   │   └── MobileBaseTest.java         # Mobile test base class
│   └── utils/
│       ├── Constants.java
│       └── PropertyReader.java
└── test/java/
    └── tests/
        ├── web/
        │   └── EventSearchTest.java    # Web tests
        └── mobile/
            └── MobileSettingTest.java  # Mobile tests
```

---

## 📱 Available Test Scenarios

### Current Mobile Tests
- ✅ **Settings App Launch** - Verify Settings app opens
- ✅ **Navigation to Apps** - Settings → Apps section
- ✅ **Navigation to Network** - Settings → Network section
- ✅ **Back Navigation** - Return from sections to main Settings

### Test Examples
```java
@Test
public void settingToAppsNavigation() {
    // Settings → Apps → Back navigation flow
}

@Test 
public void settingsToNetworkNavigationTest() {
    // Settings → Network → Back navigation flow
}
```

---

## 🔧 Configuration

### Mobile Driver Configuration
Default configuration in `MobileDriverManager.java`:
```java
platformName: "Android"
deviceName: "Pixel 6 Pro" 
appPackage: "com.android.settings"
appActivity: "com.android.settings.Settings"
automationName: "UiAutomator2"
```

### Appium Server Configuration
- **Host**: 127.0.0.1
- **Port**: 4723
- **Protocol**: HTTP

---

## 🐛 Troubleshooting

### Common Issues

#### 1. "Could not start a new session" Error
```bash
# Check environment variables
echo $ANDROID_HOME
echo $ANDROID_SDK_ROOT

# If empty, source your profile
source ~/.zshrc  # or ~/.bash_profile
```

#### 2. "Method is not implemented" Error
- This usually means PageWaitBuilder is being used in mobile (not supported)
- Use only ElementWaitBuilder for mobile testing

#### 3. AVD Performance Issues
- Ensure you're using **arm64-v8a** images on Apple Silicon Macs
- Set AVD RAM to 4096 MB maximum
- Enable **Graphics: Hardware - GLES 2.0**

#### 4. Appium Doctor Warnings
Critical items that must pass:
- ✅ ANDROID_HOME is set
- ✅ ANDROID_SDK_ROOT is set  
- ✅ adb exists and is in PATH
- ✅ Android emulator exists

#### 5. Element Not Found Errors
- Elements may have different text in different Android versions
- Use Inspector tool for debugging: `appium inspector`
- Use more generic XPath selectors: `//*[contains(@text,'App')]`

---

## 🔍 Debugging Tools

### Appium Inspector
```bash
# Install separately (Appium 2.0+)
# Download from: https://github.com/appium/appium-inspector/releases

# Or use adb for element inspection
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml
```

### ADB Commands
```bash
# List connected devices
adb devices

# Get device properties
adb shell getprop ro.build.version.release

# Install APK
adb install app.apk

# Get app package info
adb shell dumpsys package com.android.settings
```

---

## 📈 Performance Optimization

### AVD Optimization
- Use **Hardware Graphics** acceleration
- Set appropriate **RAM** (4GB recommended)
- Enable **Host GPU** if available

### Test Optimization
- Use **shorter timeouts** for mobile (5s vs 10s for web)
- Implement **page stabilization waits** after navigation
- Use **element visibility** instead of page load waits

---

## 🤝 Contributing

### Adding New Mobile Page Objects
1. Extend `MobileBasePage`
2. Follow existing patterns in `SettingsPage.java`
3. Implement verification methods
4. Add navigation methods with method chaining
5. Create corresponding test class

### Best Practices
- Use **data-testid** when available (most stable)
- Prefer **id** over **xpath** when possible
- Use **contains()** for text that might change
- Implement **defensive waits** after navigation
- Add comprehensive **logging** for debugging

---

## 📚 Additional Resources

- [Appium Documentation](https://appium.io/docs)
- [Android Developer Guides](https://developer.android.com/guide)
- [TestNG Documentation](https://testng.org/doc/)
- [Selenium Documentation](https://selenium.dev/documentation/)

---

## 🏆 Framework Status

### Completed Features
- ✅ Mobile driver management
- ✅ Page Object Model for mobile
- ✅ Navigation patterns
- ✅ Wait strategies adaptation  
- ✅ TestNG integration
- ✅ Logging and reporting

### Roadmap
- 📋 iOS support (XCUITest integration)
- 📋 Real device testing
- 📋 Mobile gestures (swipe, scroll)
- 📋 Screenshot automation
- 📋 Performance monitoring
- 📋 CI/CD pipeline integration

---

**🚀 Happy Mobile Testing!**