package com.khedmap.khedmap.Utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.khedmap.khedmap.Order.View.AnnouncementActivity;
import com.khedmap.khedmap.Order.View.OrdersManagementActivity;
import com.khedmap.khedmap.Order.View.VerifyFinishOrderActivity;
import com.khedmap.khedmap.R;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.fcm.MessagingService;

public class NotificationsMessagingService extends MessagingService {

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //if Data Cleared UnSubscribe from Pusher interest (user mobileNumber) for PushNotification
        if (getApiTokenSharedPref(this).equals("")) {
            PushNotifications.clearDeviceInterests();
            PushNotifications.addDeviceInterest("all");
            return;
        }


        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {

            switch (remoteMessage.getData().get("type")) {

                case "orderFinish":

                    Intent orderFinishIntent = new Intent(this, VerifyFinishOrderActivity.class);

                    orderFinishIntent.putExtra("orderId", remoteMessage.getData().get("order"))
                            .putExtra("subcategory", remoteMessage.getData().get("subcategory"))
                            .putExtra("finalPrice", Integer.parseInt(remoteMessage.getData().get("finalPrice")))
                            .putExtra("factorPicture", remoteMessage.getData().get("factorPicture"));

                    sendNotificationOrderFinish(orderFinishIntent, Integer.parseInt(remoteMessage.getData().get("notification_id"))
                            , remoteMessage.getData().get("title"), remoteMessage.getData().get("text"));
                    break;

                case "verifiedOrderFinish":

                    cancelNotification(Integer.parseInt(remoteMessage.getData().get("notification_id")));
                    break;

                case "rejectOrderByExpert":

                    Intent rejectedOrderIntent = new Intent(this, OrdersManagementActivity.class);

                    rejectedOrderIntent.putExtra("orderId", remoteMessage.getData().get("order"))
                            .putExtra("isFromRejectedNotification", "true");

                    sendNotificationRejectedOrder(rejectedOrderIntent, Integer.parseInt(remoteMessage.getData().get("notification_id"))
                            , remoteMessage.getData().get("title"), remoteMessage.getData().get("text"));
                    break;
                case "announcements":
                    Intent announcementIntent = new Intent(this, AnnouncementActivity.class);
                    announcementIntent.putExtra(AnnouncementActivity.KEY_TITLE, remoteMessage.getData().get("title"));
                    announcementIntent.putExtra(AnnouncementActivity.KEY_TEXT, remoteMessage.getData().get("text"));
                    sendNotificationAnnouncements(announcementIntent, Integer.parseInt(remoteMessage.getData().get("notification_id"))
                            , remoteMessage.getData().get("title"), remoteMessage.getData().get("text"));
                    break;

                default:
            }


        }


    }


    private void sendNotificationOrderFinish(Intent intent, int notificationID, String title, String text) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //set notificationID as Unique RequestId for Pending Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.khedmap_logo)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "KhedMap Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(notificationID, notificationBuilder.build());
        }
    }


    private void sendNotificationRejectedOrder(Intent intent, int notificationID, String title, String text) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //set notificationID as Unique RequestId for Pending Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.khedmap_logo)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "KhedMap Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(notificationID, notificationBuilder.build());
        }
    }


    private void sendNotificationAnnouncements(Intent intent, int notificationID, String title, String text) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //set notificationID as Unique RequestId for Pending Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.khedmap_logo)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "KhedMap Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(notificationID, notificationBuilder.build());
        }
    }


    public void cancelNotification(int notificationID) {

        //Because if notification with this id doesn't exist application crashed
        try {
            notificationManager.cancel(notificationID);
        } catch (NullPointerException ignored) {
        }

    }


    public String getApiTokenSharedPref(Context context) {

        //to make sharedPrefManager object
        SharedPrefManager sharedPrefManager = new SharedPrefManager(context);

        return sharedPrefManager.getApiToken();
    }

}