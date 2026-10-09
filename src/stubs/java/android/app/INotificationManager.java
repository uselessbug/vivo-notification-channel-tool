package android.app;

import android.os.IBinder;

/** Compile-only declarations of Android hidden APIs. NEVER ship this class in the DEX. */
public interface INotificationManager {
    NotificationChannel getNotificationChannel(
            String callingPackage, int userId, String packageName, String channelId);

    void updateNotificationChannelForPackage(
            String packageName, int uid, NotificationChannel channel);

    abstract class Stub {
        public static INotificationManager asInterface(IBinder service) {
            throw new UnsupportedOperationException("Compile-time stub only");
        }
    }
}
