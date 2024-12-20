# WorkRythm  

WorkRythm is a time tracking app designed for Android platforms, built using **Kotlin** and **Jetpack Compose**. The app allows users to effortlessly track their work hours, breaks, and statistics through an intuitive user interface.  

## Features  

### 1. Authentication  
- Users can log in via email or Google account.  
- Implemented using **Firebase Authentication**.  

### 2. Time Tracking  
- Start, pause, resume, and end work sessions with dedicated buttons.  
- Work session data is stored in **Firebase Firestore**:  
  - Start time  
  - Breaks  
  - End time  
- Automatic calculation of total work hours.  

### 3. Work History & Statistics  
- View daily work hour logs.  
- Weekly and monthly summaries of total work hours.  
- Basic statistical insights based on recorded data.  

### 4. Synchronization & User Experience Enhancements  
- Offline data synchronization using **Room** database.  
- Notifications:  
  - Reminder for breaks.  
  - Reminder to wrap up the workday.  
- Thorough testing for a seamless user experience.  

---  

## Technologies & Tools  
- **Programming Language:** Kotlin  
- **UI Framework:** Jetpack Compose  
- **Database:** Firebase Firestore + Room  
- **Authentication:** Firebase Authentication  
- **Development Environment:** Android Studio  

---  

## Installation  

1. Clone the repository:  
   ```bash  
   git clone https://github.com/your-username/WorkRythm.git  
   ```  
2. Open the project in Android Studio.  
3. Set up Firebase:  
   - Download your `google-services.json` file from the Firebase console.  
   - Add it to the `app/` directory.  
4. Run the app on an emulator or a physical device.  

---  

## Development Timeline  

### Week 1: Basics & Firebase Authentication  
- Set up the project and basic navigation.  
- Implement Firebase Authentication for login and logout.  

### Week 2: Time Tracking  
- Develop the main screen with functionality for tracking work sessions.  
- Integrate Firebase Firestore for data storage.  

### Week 3: Work History & Statistics  
- Display work session logs and basic statistics.  

### Week 4: Synchronization & UX Improvements  
- Add offline mode using Room database.  
- Implement notifications for reminders.  

---  

## Future Enhancements  
- Multi-language support.  
- Advanced statistics with charts and visualizations.  
- App themes (light and dark mode).  
- Data export to CSV or PDF.  

---  

## Author  
This application was developed as part of an internship and serves as a practical project for tracking work hours.  

For questions or feedback, feel free to reach out via [bkarahodzi1@etf.unsa.ba](mailto:bkarahodzi1@etf.unsa.ba).  

---  
