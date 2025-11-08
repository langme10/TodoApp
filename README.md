# 🎒 CSE 476 - Assignment & Class Tracker (Android, Java)

This Android application was developed for **CSE 476 (Mobile App Development)** and is designed to help students manage assignments while also providing **location-based navigation** to their classes.

The app allows users to:
- Track assignments and due dates
- Store class locations
- View their position on a map and get guided navigation to a selected classroom

Built fully in **Java** using **Android Studio**, this project demonstrates UI design, persistent storage, and Android location services.

---

## ✨ Features

### ✅ Assignment Tracking
- Add assignments with:
  - Course name
  - Assignment title
  - Due date
  - Optional notes
- View, edit, and delete saved assignments
- Assignments remain stored after app closes (local persistence)

### 📍 Class Location & Mapping
- Save classrooms with building names and coordinates
- View your current location on an interactive map
- Tap a saved class to see the route or direction relative to your position
- Uses **GPS + Google Maps API**

---

## 🧰 Tech Stack

| Component | Purpose |
|---------|---------|
| **Java** | Main application logic |
| **Android SDK** | Activities, UI, permissions, lifecycle |
| **RecyclerView** | Display assignment lists |
| **Firebase** | Persistent local data storage |
| **Google Maps API + Location Services** | GPS + map and routing functionality |

---


