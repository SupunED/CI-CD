# HealthPlus Android App

[![CI/CD](https://github.com/supunED/CI-CD/actions/workflows/main.yml/badge.svg)](https://github.com/supunED/CI-CD/actions)

HealthPlus is a Micro SaaS Android application that empowers users to manage their health effectively through features like:
- ✅ BMI Calculator
- 🌫 Real-time Air Quality Index (AQI)
- 🍎 Food Nutrition Lookup
- 🚨 Emergency Contact Info & Medical Data
- 🔒 Firebase Authentication & Secure Realtime DB

---

## 📲 Features
- **Firebase Auth**: Login, register, and reset password securely.
- **API Integrations**: Real-time data from API Ninjas & CalorieNinjas.
- **Emergency Info**: Save & edit personal health/emergency data.
- **Settings**: Update birthday, password, username.

---

## 🛠️ Tech Stack
- Android (Java, ViewBinding)
- Firebase Authentication + Realtime DB
- GitHub Actions for CI/CD
- REST APIs for AQI & Nutrition

---

## 🚀 Setup Instructions
1. Clone the repository
2. Add your API keys in `local.properties`:
   ```properties
   CALORIE_NINJAS_API_KEY=your_key
   AIR_QUALITY_API_KEY=your_key

3. Build and run the app


## CI/CD
- GitHub Actions pipeline runs
    - `gradlew build`
    - lint
    - unit-test
    - package
    - Secrets excluded via `.gitignore`

## 📄 Docs
- [Product Requirements (PRD)](docs/product-requirements.md)
- [Technical Design](docs/technical-design.md)
- [OpenAPI Specs](docs/api/)
- [Architecture Diagrams](docs/architecture/)

## 🧠 Future Improvements
- Push notifications for health alerts
- OAuth login via Google
- Offline data sync

© 2025 HealthPlus – DevOps Micro SaaS Project for IT31023