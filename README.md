# SPBU_Timetable
Application for SPBU Students. 300+ downloads
SPBU Timetable is an application for students of St. Petersburg State University, which helps to quickly and conveniently view their class schedule. At the moment, it is used by more than three hundred students.<br />
Technology Stack: Jetpack Compose, Retrofit, Navigation Compose, Firebase, Json, Jsoup, Material 3<br />
Design Patterns: MVVM, Clean Architecture<br />
How the app works: I wrote a separate parser that gets the schedule of all pairs of groups of all faculties for a semester from the official website of St. Petersburg State University. Next, I process this information into a view that is convenient for display. I use Firebase Database to store the schedule. When selecting any date, a request is sent to my database, from where the schedule for that day is pulled.<br /><br />
Watch the video of app working in the Issues
