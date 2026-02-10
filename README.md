# Gusteau 👨‍🍳

**Gusteau** is a feature-rich Android application that helps users discover meal recipes, plan their weekly diet, and manage their favorite dishes. Whether you are looking for a quick snack or a full course dinner, Gusteau brings international cuisines and detailed recipes right to your fingertips.

## 📱 Screenshots

| Splash & Login | Home Screen | Meal Details | Weekly Planner |
|:---:|:---:|:---:|:---:|
| *(Add Image)* | *(Add Image)* | *(Add Image)* | *(Add Image)* |

## ✨ Features

### 🚀 Onboarding & Authentication
* **Splash Screen:** Welcoming entry point to the application.
* **Flexible Login:**
    * **Sign in with Google:** Seamless one-tap authentication.
    * **Email/Password:** Traditional Registration and Login flows.
    * **Guest Mode:** Explore the app without creating an account (with limited features).
* **Onboarding:** A guided introduction for new users after registration or first-time Google sign-in.

### 🏠 Discovery & Browsing
* **Meal of the Day:** Featured recipe displayed prominently on the Home screen.
* **Search & Filter:**
    * Search for meals by name.
    * Apply filters by **Country**, **Category**, or **Key Ingredient**.
* **Categories:** Browse meals by specific food categories (e.g., Seafood, Vegan).
* **Cuisines:** Explore recipes based on country of origin.
* **Ingredients:** Find meals based on specific key ingredients.

### 🍽️ Meal Management (Registered Users)
* **Favorites:** * Save meals to a dedicated "Favorites" screen.
    * Quick-add to favorites directly from list views or the meal details screen.
* **Weekly Planner:**
    * Plan meals for the current day + the next 6 days.
    * Assign meals to specific slots: **Breakfast, Lunch, Dinner,** or **Snack**.
* **Cloud Backup:** Sync your Favorites and Weekly Plan to the cloud via Settings.
* **Offline Access:** View your saved Favorites and Planned meals even when offline/logged out.

### 📖 Recipe Details
* Comprehensive instructions and steps.
* List of ingredients.
* **YouTube Integration:** Watch recipe video tutorials directly within the app.

## 👤 User Roles

| Feature | 🟢 Registered User | 🟡 Guest |
| :--- | :---: | :---: |
| Browse & Search Meals | ✅ | ✅ |
| View Meal Details & Videos | ✅ | ✅ |
| Filter by Country/Category | ✅ | ✅ |
| **Add to Favorites** | ✅ | ❌ |
| **Weekly Meal Planner** | ✅ | ❌ |
| **Cloud Backup** | ✅ | ❌ |
| **Access Settings** | ✅ | ❌ |

## 🛠️ Architecture & Tech Stack

* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel)
* **UI:** XML / Jetpack Compose *(Adjust based on your actual implementation)*
* **Networking:** Retrofit / Volley
* **Local Database:** Room (for offline caching of plans and favorites)
* **Images:** Glide / Coil
* **Authentication:** Firebase Auth / Google Sign-In

## 💻 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/OsamaKhaled/Gusteau.git](https://github.com/OsamaKhaled/Gusteau.git)
    ```
2.  **Open in Android Studio:**
    * Open Android Studio -> File -> Open -> Select the cloned folder.
3.  **Sync Gradle:**
    * Allow the project to sync dependencies.
4.  **Configure API Keys:**
    * Add your `google-services.json` file to the `app/` directory for Firebase/Google Auth to work.
    * *(Optional)* Add your Recipe API key in `local.properties` if required.
5.  **Run the App:**
    * Connect a physical device or start an Emulator.
    * Click the **Run** (Play) button.

## 👥 Team

* **Osama Khaled** - *Android Developer*

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
