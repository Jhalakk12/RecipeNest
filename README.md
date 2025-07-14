# RecipeNest 🍳

A comprehensive Android application for discovering, exploring, and managing delicious recipes. Built with modern Android development practices and integrated with Firebase for user authentication and data management.

## 📱 Features

- **Recipe Discovery**: Browse through a vast collection of recipes
- **Meal Categories**: Filter recipes by categories (like Beverages, Main Course, Dessert, Side dish) with dropdown selection
- **Search Functionality**: Find recipes by name, ingredients, or cuisine type
- **User Authentication**: Secure login and registration with Firebase Auth (persistent login)
- **Recipe Details**: Detailed view with ingredients, instructions, and nutritional information
- **Similar recipes**: Suggests similar recipes
- **User Profiles**: Personalized user experience with profile management
- **Favorites**: Save and manage your favorite recipes
- **Responsive Design**: Optimized for various Android devices

## 🛠️ Technology Stack

- **Language**: Java
- **Platform**: Android (API 28+)
- **Architecture**: MVP (Model-View-Presenter)
- **Backend**: Firebase (Authentication, Firestore)
- **Networking**: Retrofit 2 with Gson converter
- **Image Loading**: Picasso
- **UI Components**: Material Design Components
- **Build System**: Gradle

## 📋 Prerequisites

- Android Studio Meerkat
- Android SDK API 28 or higher
- Java 11
- Firebase project setup (google-services.json)

## ⚙️ Installation & Setup

1. **Clone the repository**

2. **Open in Android Studio**
   - Open Android Studio
   - Click "Open an existing project"
   - Navigate to the cloned repository and select the project folder

3. **Firebase Configuration**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add your Android app to the Firebase project
   - Download the `google-services.json` file
   - Place it in the `app/` directory
   - Enable Authentication and Firestore in your Firebase project

4. **API Configuration**
   - The app uses Spoonacular API for recipe data
   - Add your API key in the appropriate configuration file

5. **Build and Run**
   - Sync the project with Gradle files
   - Build and run the application on your device or emulator


## 🚀 Usage

1. **Welcome Screen**: Initial app introduction
2. **Authentication**: Register or login with email/password
3. **Home Screen**: Browse featured and random recipes
4. **Meal Categories**: Use the dropdown menu to filter recipes by meal type:
   - Select from categories like Beverages, Main Course, Appetizers, Desserts, etc.
   - Quick access to recipes based on meal planning needs
5. **Search**: Use the search functionality to find specific recipes
6. **Recipe Details**: Tap on any recipe to view detailed information
7. **Similar recipes**: Gives suggestion of similar recipes
8. **Profile**: Manage your account and view saved recipes
