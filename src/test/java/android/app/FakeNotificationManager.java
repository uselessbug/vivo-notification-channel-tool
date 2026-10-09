package android.app;
import android.os.IBinder;

/** Emulate just the protection that requires blockable=true before disabling a system channel. */
public final class FakeNotificationManager implements INotificationManager, IBinder {
    public static final FakeNotificationManager INSTANCE = new FakeNotificationManager();
    private NotificationChannel current = new NotificationChannel(3, false);
    public int reads;
    public int writes;

    private FakeNotificationManager() { }

    public NotificationChannel getNotificationChannel(String caller, int userId, String pkg, String id) {
        if (!"com.android.shell".equals(caller) || userId != 0
                || !"com.vivo.daemonService".equals(pkg) || !"DEVELOPMENT_MODE".equals(id)) {
            throw new AssertionError("Wrong notification channel read target");
        }
        reads++;
        return current.copy();
    }

    public void updateNotificationChannelForPackage(String pkg, int uid, NotificationChannel value) {
        if (!"com.vivo.daemonService".equals(pkg) || uid != 1000) {
            throw new AssertionError("Wrong notification channel write target");
        }
        if (!current.isBlockable() && current.getImportance() != value.getImportance()) {
            throw new AssertionError("Tried changing protected importance before enabling blockable");
        }
        writes++;
        current = value.copy();
    }

    public int importance() { return current.getImportance(); }
    public boolean blockable() { return current.isBlockable(); }
}
