package android.app;
import android.os.IBinder;
public interface INotificationManager {
    NotificationChannel getNotificationChannel(String caller, int userId, String pkg, String channelId);
    void updateNotificationChannelForPackage(String pkg, int uid, NotificationChannel channel);
    abstract class Stub {
        public static INotificationManager asInterface(IBinder binder) {
            return (INotificationManager) binder;
        }
    }
}
