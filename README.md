# Yaddasht_java
 A fully working note taking app with offline and online support written in java

## Description 
This note taking app is developed with **offline first approach** in mind. All the notes will be created in the local **Room** database first and if there is intenet connection available, new and updated notes will be synched to the server.  
**Retrofit** have the duty of sending and receiving data from Rest api.  
Rest api is written in node.js and located in remote server. The data that is received from Rest api is stored to **MongoDB**. This Rest api source code is also available in [this Repo](www.google.com "Yaddasht Rest api").

### Features
- User registration and login
- Synch notes to server
- Synched note accessibe in multiple device
- Add check list and tick items
- Add alarm and get notification at specific time
- persian calendar date picker
- Push notification with [Najva](https://www.najva.com "Najva")