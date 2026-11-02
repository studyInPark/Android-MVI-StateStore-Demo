# 🚀 Android MVI Architecture Pattern with MviStateStore

[![YouTube Video](https://img.shields.io/badge/YouTube-Watch%20Tutorial-red?style=for-the-badge&logo=youtube)](https://www.youtube.com/watch?v=q40BGa5N_GM)
[![Channel](https://img.shields.io/badge/YouTube-StudyInPark-blue?style=for-the-badge&logo=youtube)](https://www.youtube.com/@studyinpark)

A clean, modern Android repository demonstrating **Model-View-Intent (MVI)** architecture built with **Jetpack Compose**, **Kotlin Coroutines / Flow**, and a centralized **`MviStateStore`**.

🎥 **Watch Full Video Tutorial on YouTube**: [Android MVI Architecture Pattern with MviStateStore](https://www.youtube.com/watch?v=q40BGa5N_GM)

This project also includes an interactive side-by-side comparison between **MVVM** and **MVI** flows.

---

## 📌 Architecture Highlights

```
    ┌────────────────────────────────────────────────────────┐
    │                                                        │
    │   User Actions                 Processes Action        │
    │  ┌────────────┐   Intents    ┌──────────────────┐      │
    │  │    VIEW    │ ───────────> │    VIEW MODEL    │      │
    │  │ (Compose)  │              │ (Business Logic) │      │
    │  └────────────┘              └──────────────────┘      │
    │        ▲                               │               │
    │        │          New State            │  Reduces      │
    │        └───────────────────────────────┘  State        │
    │                   (StateFlow)                          │
    └────────────────────────────────────────────────────────┘
```

- **Unidirectional Data Flow (UDF):** Predictable, single-loop data pipeline.
- **Single Source of Truth:** 100% of the screen state resides in one immutable `UiState` object.
- **Pure Reducer Functions:** State updates are computed by pure functions:  
  `Reducer = (Current State + Event) ➔ Output(New State, Optional Effect)`
- **One-Time Side Effects:** Handled via buffered `Channel` flows for single-fire events (Navigation, Toasts, Snackbars).

---

## 🎨 The 3 Pillars of MVI

| Component | Class | Responsibility |
| :--- | :--- | :--- |
| ⚡ **Intent / Event** | `LoginEvent` | Represents user actions (`LoginClicked`, `EmailChanged`) and async results (`LoginSucceeded`, `LoginFailed`). |
| ⚙️ **Model** | `LoginUiState` | Single immutable snapshot of the entire screen state. |
| 🚀 **Effect** | `LoginUiEffect` | Single-shot events that do not persist in UI state (e.g., `NavigateToHome`). |
| 🔄 **Reducer** | `MviLoginReducer` | Pure function that computes state transitions. |

---

## 📁 Project Structure

```
app/src/main/java/com/learn/
├── domain/
│   └── LoginUseCase.kt          # Business logic & fake authentication service
├── mvi/
│   ├── core/
│   │   └── MviCore.kt           # UiState, Event, UiEffect, Reducer & MviStateStore
│   └── ui/mvi/
│       ├── MviLoginContract.kt  # State, Events, Effects definition
│       ├── MviLoginReducer.kt   # Pure Reducer function
│       ├── MviLoginViewModel.kt # ViewModel holding MviStateStore
│       └── MviLoginScreen.kt    # Stateless Jetpack Compose Screen
├── mvvm/
│   └── ui/
│       ├── MvvmLoginViewModel.kt# MVVM ViewModel with separate StateFlows
│       └── MvvmLoginScreen.kt   # MVVM Compose Screen
└── MainActivity.kt              # App entry point & navigation host
```

---

## 🛠️ Build Requirements & Setup

- **Android Studio:** 2024.1+ / 2026.2+
- **JDK:** Java 17
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Gradle Version:** 8.11.1
- **Android Gradle Plugin:** 8.7.3

### Running the App:
1. Clone this repository:
   ```bash
   git clone https://github.com/studyInPark/Android-MVI-StateStore-Demo.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and press **Run ▶** on an emulator or physical device.

---

## ⚖️ MVVM vs MVI Comparison

| Feature | MVVM | MVI |
| :--- | :--- | :--- |
| **State Management** | Multiple independent `StateFlow` / `LiveData` fields | **Single unified `UiState`** immutable data class |
| **State Updates** | Updated directly in ViewModel methods | Funneled through a **central Reducer** |
| **Side Effects** | Handled via state flags or shared flows | Handled via **`UiEffect` Channels** |
| **Unit Testing** | Requires coroutine test scopes & dispatchers | Testable by calling **`reducer.reduce()`** directly |

---

## 📄 License
Licensed under the [MIT License](LICENSE).
