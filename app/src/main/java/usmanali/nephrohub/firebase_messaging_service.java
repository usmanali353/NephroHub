package usmanali.nephrohub;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by HelloWorldSolution on 5/17/2018.
 */

public class firebase_messaging_service extends FirebaseMessagingService {
    Bitmap bitmap;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
                 super.onMessageReceived(remoteMessage);
                 if(remoteMessage.getNotification()!=null) {
                     String title = remoteMessage.getNotification().getTitle();
                      String body = remoteMessage.getNotification().getBody();
                        sendNotification(body,title);
                    }else if(remoteMessage.getData().size()>0){
                       String title=remoteMessage.getData().get("title");
                       String imageUrl=remoteMessage.getData().get("imageUrl");
                        String body= remoteMessage.getData().get("body");
                      bitmap=getBitmapfromUrl(imageUrl);
                       send_big_notification(title,body,bitmap);
                    }




           }
     private void sendNotification(String messageBody,String title) {
               Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                                 PendingIntent.FLAG_ONE_SHOT);


                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ico4)
                       .setTicker("New Notification from Kidney Center Gujrat")
                       .setContentTitle(title)
                         .setContentText(messageBody)
                        .setAutoCancel(true)
                       .setSound(defaultSoundUri)
                       .setContentIntent(pendingIntent);


               NotificationManager notificationManager =
                               (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                 notificationManager.notify(0, notificationBuilder.build());
    }
   private void send_big_notification(String title, String body, Bitmap image){
              Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                              PendingIntent.FLAG_ONE_SHOT);
               Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                         .setLargeIcon(image)
                      .setTicker("New notification from Kidney Center Gujrat")
                       .setSmallIcon(R.drawable.ico4)
                      .setContentTitle(title)
                        .setContentText(body)
                         .setStyle(new NotificationCompat.BigPictureStyle()
                                         .bigPicture(image))
                        .setAutoCancel(true)
                         .setSound(defaultSoundUri)
                         .setContentIntent(pendingIntent);
                 NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


               notificationManager.notify(0, notificationBuilder.build());
          }
    public Bitmap getBitmapfromUrl(String imageUrl) {
                try {
                         URL url = new URL(imageUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                         Bitmap bitmap = BitmapFactory.decodeStream(input);
                     return bitmap;


        } catch (Exception e) {
                       e.printStackTrace();
                        return null;


                  }
            }
   @Override
     public void onDeletedMessages() {
                 super.onDeletedMessages();


             }
 }


