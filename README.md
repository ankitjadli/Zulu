# Zulu

Zulu is an open source web based Chat aplication developed using Firebase features.
UI referened from : https://www.invisionapp.com/inside-design/design-resources/chat/

## Features 

![featuresused 2x](https://user-images.githubusercontent.com/37221963/42370593-c8d3dba6-812a-11e8-8495-a2fcb7a8ecc0.png)


## UI
![1-1 login 2x](https://user-images.githubusercontent.com/37221963/42367936-57fa583a-8123-11e8-9d85-087f586abbe3.png)


## Notifications

Zulu sends notifications using Firebase Functions and Firebase Notifications.It supports Friends request notifications for now and soon messages notification code wiLl be added.


#### JS code for Friend Request Notification to the Requested user :


```
'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

 exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change, context) =>
 {
   const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  console.log('We have a notification from : ', user_id);
  const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

return fromUser.then(fromUserResult => {

  const from_user_id = fromUserResult.val().from;

  console.log('You have new notification from  : ', from_user_id);
  const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
 const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

 return Promise.all([userQuery, deviceToken]).then(result =>   {

   const userName = result[0].val();
   const token_id = result[1].val();

           const payload = {
             notification: {
               title : "Friend Request",
              body: `${userName} has sent you request`,
               icon: "default",
             click_action : "com.example.ankit.supbruh_TARGET_NOTIFICATION"
           },
             data : {
               from_user_id : from_user_id
             }

           };
           return admin.messaging().sendToDevice(token_id, payload).then(response => {

      return console.log('This was the notification Feature');

    });

  });

});
});


```

NEVER GIVE UP NEVER STOP CODING 


#### Developed by Ankit Jadli June-July 2018


