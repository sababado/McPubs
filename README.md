# McPubs
App to notify about Marine Corps pub updates.

## Requirements
This app requires a backend in order to process when pubs are updated and to send notifications to devices.

### Backend
+ Android Studio (AS) 2.2.2 or higher
+ Google Cloud with a MySQL database
+ Firebase Project
+ Java 7

### Android Client
+ Android Studio (AS) 2.2.2 or higher
+ Minimum SDK 19 Kitkat
+ Target SDK 23 Marshmallow
+ Java 7

## Setting up the Backend
### Google Cloud Project
### Firebase
### MySQL Database


## Building the App
Import the project into Android Studio (AS). An internet connection is to build the backend and the app.

### Backend
In AS, click `Build` and `Deploy Module to App Engine`. In the popup enter the information as follows:
+ **Module:** backend
+ **Deploy To:** <your_google_code_project_id>
+ **Version:** 1

TODO Screenshot of popup

![](/readme_screnshots/build_deploy_backend.png "popup")

![](/readme_screnshots/deploy_backend.png "popup")

TODO Show console output

### Android Client

In AS, select the `app` build configuration and click `Run`.

TODO Screenshot of config and run button
![](/readme_screnshots/app_run_config.png "App Config")

This will ask which device or emulator to deploy to. The device or emulator must be running at least Kitkat (SDK 19).

## Using the App
On the android client app tap the add button to watch a pub. The app supports watching specific types of pubs.
Select the type of pub and then enter the pub number. For example `MCO P1020.34G` can be entered in as that or as `MCO P1020.34`.
Once saved the server will check for the most up-to-date version on record and return any relevant information.
The server will automatically check for updates to pubs every two days. The server will only check pubs
that haven't been updated recently and are being watched by a user.

If updates are found then the server will send a notification to the devices that are watching that pub.
The notification will update the device with any new information and also provide a status to the user.
The pub may be updated or it may be deleted. The status will show on the user's list until an action is
taken by the user. This will serve as a reminder that some action needs to be taken.

Users can stop watching pubs at any time by tapping on the pub and deleting it from their list.

## Technical Details
### Firebase Cloud Messaging (FCM)
Firebase Cloud Messaging makes sending notifications very easy compared to previous implementations of
push notifications on Android. In FCM, messages can be sent to all devices, to single devices, or to topics.
This app makes heavy use of the topic option. Each pub is treated as a topic. When a device adds a pub
the server will subscribe to the pub (topic) for the device. When updates are made to that pub the server
will send a message to that topic on FCM. This will then reach all devices subscribing to that pub.

When a user saves a pub on the app it is sent to the server. It is saved as a new pub if it is new and a new
`PubDevice` record is created to indicate that this pub is now being watched by the calling device. Either way
the server will subscribe to a FCM topic on the device's behalf.

When a user deletes a pub on the app the request is sent to the server. The `PubDevice` associated with
this pub and device is deleted. The server then unsubscribes to the topic on the behalf of the device.

### Cron Jobs
The server portion of this app runs a cron job every two days to check for updates to a set of pubs.
This set of pubs consists of pubs that are being currently watched by a device and also hasn't been
updated in the last month. The cron job screen-scrapes the public MCPEL website. It will search for
pubs for each distinct root code (for example `1020`) and compare the scraped pubs against what is
saved in the database. It will check for an updated status and for updated versions.

Every week another cron job runs to clean up the database and remove any pubs
that are no longer being watched by devices.