# WiFi Vault

אפליקציית Android לשמירת קבצים דרך Wi-Fi ולגישה אליהם במצב לא מקוון.

## יכולות בגרסה הראשונה
- ממשק עברי ו-RTL.
- בדיקת חיבור Wi-Fi.
- דפדפן WebView בסיסי.
- הורדת קישורים ישירים לקבצים באמצעות Android DownloadManager, מוגבל ל-Wi-Fi.
- ספריית קבצים שמורים.
- יעד Android 11 (API 30), עם תמיכה ממינימום Android 6.

## מגבלות
האפליקציה שומרת קבצים שהורדו במפורש; היא אינה משמרת את חיבור האינטרנט עצמו ואינה מבטיחה שאתרים דינמיים או שירותי סטרימינג יעבדו ללא רשת. אין למלא את כל האחסון: Android ומערכת הקבצים זקוקים למקום פנוי. שמירת דפים שלמים במצב לא מקוון היא יכולת נפרדת שדורשת טיפול במשאבים ובתוכן של כל אתר.

## Build APK
GitHub Actions builds a debug APK on each push to main or master, and on manual workflow dispatch. Open Actions, select Build WiFi Vault APK, and download the WiFi-Vault-APK artifact.
