# Yaddasht_java
 A fully working note taking app with offline and online support written in java

## Description 
This note taking app is developed with **offline first approach** in mind. All the notes will be created in the local **Room** database first and if there is internet connection available, new and updated notes will be synched to the server.  
**Retrofit** have the duty of sending and receiving data from Rest api.  
Rest api is written in node.js and located in remote server. The data that is received from Rest api is stored to **MongoDB**. This Rest api source code is also available in [this repository](www.google.com "Yaddasht Rest api").

### Features
- Persian language ui
- User registration and login
- Synch notes to server
- Synched note accessibe in multiple device
- Add check list and tick items
- Add alarm and get notification at specific time
- Search in notes
- persian calendar date picker
- Push notification 
- Pin your notes to top of the list
- Customize your notes with different colors
- Barcode Scanner

## Screenshots

<img src="/preview.jpg"/>

## Download
This app is published in cafebazaar.ir and can be downloaded from the link below  
    [Download Yaddasht](https://cafebazaar.ir/app/ir.yaddasht.yaddasht)


## Components used in the application
- Room
- Retrofit 2
- ViewModel
- [SunDatePicker](https://github.com/alirezaafkar/SunDatePicker)
    - Persian date picker
- [JalaliCalendar](https://github.com/razeghi71/JalaliCalendar)
    - Jalali calendar
- [Najva](https://www.najva.com/)
    - Push notification service
- Lottie 
- [zxing-android-embedded](https://github.com/journeyapps/zxing-android-embedded)
    - Barcode scanner
- LiveData
- [TinyDB](https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo)
- etc

## Upcoming feature
- Add image
- Voice memo
- Responsive design for tablet

# Licence

    Copyright 2021 Hamed Vakhide
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.