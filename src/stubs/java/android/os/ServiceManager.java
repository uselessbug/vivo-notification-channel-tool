package android.os;

/** Compile-only declaration of the hidden framework class. */
public final class ServiceManager {
    private ServiceManager() { }
    public static IBinder getService(String name) {
        throw new UnsupportedOperationException("Compile-time stub only");
    }
}
