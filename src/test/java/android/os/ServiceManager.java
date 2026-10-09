package android.os;
public final class ServiceManager {
    private ServiceManager() { }
    public static IBinder getService(String name) {
        if (!"notification".equals(name)) throw new AssertionError(name);
        return android.app.FakeNotificationManager.INSTANCE;
    }
}
