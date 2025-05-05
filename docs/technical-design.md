# Technical Design Document – HealthPlus

## 1. Tech Stack
--------------------------------------------------------------------
| Layer              | Technology                                  |
|--------------------|---------------------------------------------|
| Frontend           | Android (Java)                              |
| Authentication     | Firebase Authentication                     |
| Database           | Firebase Realtime DB                        |
| External APIs      | API Ninjas (AQI), CalorieNinjas (Nutrition) |
| CI/CD              | GitHub Actions                              |
| Documentation      | Markdown, OpenAPI 3.0                       |
--------------------------------------------------------------------

## 2. App Architecture
- MVVM (Model-View-ViewModel)
- ViewBinding for UI
- Fragment-based navigation

## 3. Component Design
- `activity_home`shows Landing Page
- `activity_register`shows the user registration page
- `activity_login` shows the user login page
- `activity_settings` shows the user profile settings
- `GalleryFragment` shows AQI info
- `fragment_slideshow` shows nutrition info
- `fragment_emergency_contacts` for emergency fields and phone calls

## 4. Security
- API keys hidden using `local.properties`
- Firebase rules restrict data access to authenticated users

## 5. Scalability Considerations
- Firebase autoscaling + low latency
- API usage metered with limits
- Modularized app code for future plugin features
