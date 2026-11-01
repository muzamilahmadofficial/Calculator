# Step-by-Step Google Play Console Publishing Guide

Follow these steps to upload and publish **MA Calculator** to your developer account:

**Direct Play Console Link:**  
👉 **[Open Your Google Play Console](https://play.google.com/console/u/0/developers/8714944766272646337/app-list)**

---

## ⚠️ Important Note on Account Security
> **Security Notice**: For your personal privacy and safety, **never share your Google Account passwords or login credentials** with anyone or any AI assistant. Google Play Console uses two-factor authentication (2FA) and security protections that require you to upload the app directly from your browser. Everything has been pre-packaged and verified so the entire process takes less than 5 minutes!

---

## Step 1: Create the App Entry in Play Console

1. Navigate to: [https://play.google.com/console/u/0/developers/8714944766272646337/app-list](https://play.google.com/console/u/0/developers/8714944766272646337/app-list)
2. In the top right corner, click the blue **"Create app"** button.
3. Fill in the App Details:
   - **App name**: `MA Calculator - Clean & Fast`
   - **Default language**: `English (United States) - en-US`
   - **App or game**: Select **App**
   - **Free or paid**: Select **Free**
4. Check the declaration boxes (Developer Program Policies and US export laws).
5. Click **Create app** at the bottom right.

---

## Step 2: Fill in the Main Store Listing

In the left-hand navigation menu under **Grow**, click **Store presence** > **Main store listing**:

1. **Short description** (copy & paste):
   ```text
   Clean, private standard & scientific calculator with unit converter and history.
   ```
2. **Full description**:
   Open [`playstore_assets/STORE_LISTING_METADATA.md`](./STORE_LISTING_METADATA.md) and copy the full description block.
3. **Graphics Upload**:
   - **App icon**: Drag & drop `playstore_assets/icon_512x512.png` (512x512 PNG).
   - **Feature graphic**: Drag & drop `playstore_assets/feature_graphic_1024x500.png` (1024x500 PNG).
   - **Phone screenshots**: Upload all 5 screenshots from `playstore_assets/screenshots/`:
     - `1_standard.png`
     - `2_scientific.png`
     - `3_converter.png`
     - `4_history.png`
     - `5_privacy.png`
4. Click **Save** at the bottom right.

---

## Step 3: Complete App Content Declarations

In the left menu, scroll down to the **Policy and programs** section and click **App content**:

1. **Privacy Policy**:
   - Enter your public privacy policy URL (you can host `playstore_assets/privacy_policy.html` on GitHub Pages, Google Sites, or your website).
2. **Ads**:
   - Select **No, my app does not contain ads**.
3. **App Access**:
   - Select **All functionality is available without special access restrictions**.
4. **Content Ratings**:
   - Click **Start questionnaire**.
   - Email address: Enter your developer email.
   - Category: Choose **Utility, Productivity, Communication, or Other**.
   - Answer **No** to all questions (no violence, sexuality, profanity, drug references, etc.).
   - Click **Save** > **Next** > **Submit**. Rating will be **PEGI 3 / Everyone**.
5. **Target Audience and Content**:
   - Select age groups: **13-15**, **16-17**, and **18 and over**.
   - Could your store listing appeal to children?: Select **No**.
6. **Data Safety Form**:
   - Does your app collect or share any user data?: Select **No**.
   - (Since the app is 100% offline and requests zero dangerous permissions).
7. **Government Apps & Financial Features**:
   - Answer **No** to both.

---

## Step 4: Upload the Release Bundle (.aab)

1. In the left menu under **Release**, click **Production** (or **Closed testing** / **Internal testing** if your developer account requires testing before production rollout).
2. In the top right, click **Create new release**.
3. In the **App bundles** section, click **Upload**.
4. Select the file:
   📁 `c:\Users\muzam\OneDrive\Documents\Calculator App\playstore_assets\MA-Calculator-release.aab`
5. The console will automatically detect:
   - Application ID: `com.ma.calculator`
   - Version Code: `1`
   - Version Name: `1.0.0`
6. In **Release notes**, enter:
   ```text
   Initial release of MA Calculator: sleek arithmetic, advanced scientific mode, smart unit converter, and private local history.
   ```
7. Click **Next** at the bottom right.
8. Review the summary and click **Save** / **Start rollout to Production**!

---

## Step 5: Test Locally on Your Android Phone Right Now

You don't have to wait for Google's review to test the app! You can install the debug APK immediately on your Android device:
1. Transfer `playstore_assets/MA-Calculator-debug.apk` to your phone (via USB cable, Google Drive, WhatsApp, or email).
2. Tap the `.apk` on your phone and tap **Install**.
3. Open **MA Calculator** and enjoy your custom app!
