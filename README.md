# McPubs
App to notify about Marine Corps MCPEL pub updates.

Download the demo from the Play Store at
[https://play.google.com/store/apps/details?id=com.sababado.mcpubs](https://play.google.com/store/apps/details?id=com.sababado.mcpubs)

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

## Build Requirements
This app requires a backend in order to process when pubs are updated and to send notifications to devices.

The app can be built and tested as is without the need to deploy the backend code to a custom server.

### Backend
+ Android Studio (AS) 2.2.2 or higher
+ Google Cloud with a MySQL database
+ Firebase Project
+ Java 7
+ For local server testing a MAMP server with a MySQL instance is sufficient.

### Android Client
+ Android Studio (AS) 2.2.2 or higher
+ Minimum SDK 19 Kitkat
+ Target SDK 23 Marshmallow
+ Java 7

## Setting up the Backend
### Google Cloud Project and MySQL Database
Before a Google Cloud project can be created, a billing profile needs to be setup.
Visit [https://console.cloud.google.com/billing](https://console.cloud.google.com/billing) and sign in with a google account to get started.

1. Create a new billing account. Under `My Billing Accounts` select `New Billing Account`. Enter the required information.
2. Create a new project and provide a name and the billing account from Step 1. Note the project ID. This will be needed later.
    + Once you click "Create Project" go get a coffee, this may take a minute.
3. Create a Cloud SQL instance.
    + Choose First Generation, provide a name such as `mcpubs-db`. This name will be the instance id.
    + Tier of D0 - 128 MB Ram is enough for this test.
    + No need to configure advanced options unless an IPv4 address is desired.
    + Once you click "Create" go get a second cup of coffee.
    + Open the instance's overview and click on the `Databases` tab. Create a database called `mcpubsdb` or another desired name.
4. Define connection information in the `backend/../StringUtils.java` file.
    + Replace the Project ID with the newly created project id.
    + Replace the DB Instance name with the instance name used in Step 3.
    + Replace the DB Name with the name used for the database at the end of Step 3.
    + Replace the user connection info if necessary.
5. Use any method of connecting to the database, such as MySQL Workbench.
    + Initialize the database using the SQL found in `backend/createDb.sql`.


### Firebase
Create a Firebase application that is linked to the Google Cloud project in order to use push notifications.

1. Visit [https://console.firebase.google.com/](https://console.firebase.google.com/)
2. Click `Import Google Project` and select the project.
3. After the project has been imported successfully you'll see the Overview page. Click `Add Firebase to your Android App`
    + Provide the package name `com.sababado.mcpubs`.
    + Provide an app nickname `McPubs`
    + Click `Add App`
    + Follow the steps to replace the existing `google-services.json` with the new one.
    + Ignore the third step in the wizard. Firebase dependencies have already been added to the build.gradle files.

## Building the Project
Import the project into Android Studio (AS). An internet connection is to build the backend and the app.

If a new backend is created be sure to update references in the following files:

+ `app/src/main/res/values/strings.xml`
+ `backend/src/main/webapp/WEB-INF/appengine-web.xml`
+ `backend/src/main/java/com/sababado/mcpubs/backend/utils/StringUtils.java`

If the app package name changes then update the package name where specified in the `AndroidManifest.xml`.

### Backend
In AS, click `Build` and `Deploy Module to App Engine`. In the popup enter the information as follows:
+ **Module:** backend
+ **Deploy To:** <your_google_code_project_id>
+ **Version:** 1

### Android Client

In AS, select the `app` build configuration and click `Run`.
This will ask which device or emulator to deploy to.
The device or emulator must be running at least Kitkat (SDK 19).

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

## Quick End-To-End Test
Using Google Cloud Console the app can be tested end-to-end without waiting two days for cron job to run.

+ Run the app and add a pub that exists on MCPEL but hasn't been watched by the app yet.
+ Open Google Cloud Console and go to `App Engine -> Task Queues -> Cron Jobs`
+ Find the cron job called `Pub Check`. Click the `Run` button for this cron job.
+ The cron job will finish within a few minutes. As long as the pub exists on MCPEL then a notification will be sent to the device.

## Unit Tests
Junit tests are written for the backend code.