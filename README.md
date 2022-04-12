# Movies List Demo App

Movie List, is an App  that provides list/detail view of movie items.

# App features

showing list of movies with pagination using [API service](https://api.themoviedb.org/3/discover/movie).
search  with pagination using [API service](https://api.themoviedb.org/3/search/movie).
mark the movies as favorite and save the user mark to local DB.
showing details of the movie item in details screen using [API service](https://api.themoviedb.org/3/movie/$movieId).

# technologies and libraries:
- Kotlin
- HttpURLConnection: load images and get API service responses
- RoomDatabase: caching user favorites
- Kotlin-coroutines / Flow / StateFlow / SharedFlow 
- Paging3
- Navigation component
- View Binding and Data Binding


# App Demo Video

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/pHhLXSFs-Zs/0.jpg)](https://www.youtube.com/watch?v=pHhLXSFs-Zs)