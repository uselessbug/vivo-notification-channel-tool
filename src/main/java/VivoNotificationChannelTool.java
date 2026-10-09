import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.os.ServiceManager;

/**
 * Manage only the Vivo developer-mode notification channel via the Android shell UID.
 *
 * This program intentionally uses hidden framework APIs. It is loaded with app_process,
 * not installed as an Android application. The runtime framework supplies these classes;
 * src/stubs provides compile-time signatures only and MUST NOT be included in the DEX.
 */
public final class VivoNotificationChannelTool {
    private static final String PACKAGE_NAME = "com.vivo.daemonService";
    private static final String CHANNEL_ID = "DEVELOPMENT_MODE";
    private static final String CALLING_PACKAGE = "com.android.shell";
    private static final int USER_ID = 0;
    private static final int PACKAGE_UID = 1000;
    private static final int IMPORTANCE_NONE = 0;
    private static final int ORIGINAL_IMPORTANCE = 3;

    private VivoNotificationChannelTool() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            usage();
            System.exit(2);
            return;
        }
        String operation = args.length == 0 ? "inspect" : args[0];
        if (!"inspect".equals(operation)
                && !"disable".equals(operation)
                && !"restore".equals(operation)) {
            usage();
            System.exit(2);
            return;
        }

        INotificationManager manager = INotificationManager.Stub.asInterface(
                ServiceManager.getService("notification"));
        if (manager == null) {
            throw new IllegalStateException("Notification service unavailable");
        }
        NotificationChannel channel = requireChannel(manager);
        System.out.println("Before importance: " + channel.getImportance());
        System.out.println("Before blockable: " + channel.isBlockable());

        if ("inspect".equals(operation)) {
            System.out.println("Inspection only. No settings changed.");
            return;
        }
        if ("disable".equals(operation)) {
            // Protected system channels cannot normally be set to importance NONE.
            // First make the *existing* channel blockable; then update importance.
            channel.setBlockable(true);
            manager.updateNotificationChannelForPackage(PACKAGE_NAME, PACKAGE_UID, channel);
            channel = requireChannel(manager);
            System.out.println("After enabling blockable: " + channel.isBlockable());
            if (!channel.isBlockable()) {
                throw new IllegalStateException("Cannot enable blockable: importance unchanged");
            }
            channel.setImportance(IMPORTANCE_NONE);
            manager.updateNotificationChannelForPackage(PACKAGE_NAME, PACKAGE_UID, channel);
            channel = requireChannel(manager);
            System.out.println("After disabling channel: importance=" + channel.getImportance());
            if (channel.getImportance() != IMPORTANCE_NONE) {
                throw new IllegalStateException("Channel importance did not become 0");
            }
            System.out.println("SUCCESS: DEVELOPMENT_MODE channel is disabled.");
            return;
        }

        // Best-effort restoration of the two fields modified by `disable`.
        // Android may retain user-lock metadata, so this is not a full state reset.
        channel.setImportance(ORIGINAL_IMPORTANCE);
        channel.setBlockable(false);
        manager.updateNotificationChannelForPackage(PACKAGE_NAME, PACKAGE_UID, channel);
        channel = requireChannel(manager);
        System.out.println("After restoration: importance=" + channel.getImportance());
        System.out.println("After restoration: blockable=" + channel.isBlockable());
        if (channel.getImportance() != ORIGINAL_IMPORTANCE || channel.isBlockable()) {
            throw new IllegalStateException("Restore incomplete; inspect current channel state");
        }
        System.out.println("SUCCESS: DEVELOPMENT_MODE settings restored.");
    }

    private static NotificationChannel requireChannel(INotificationManager manager)
            throws Exception {
        NotificationChannel channel = manager.getNotificationChannel(
                CALLING_PACKAGE, USER_ID, PACKAGE_NAME, CHANNEL_ID);
        if (channel == null) {
            throw new IllegalStateException("Target notification channel not found: "
                    + PACKAGE_NAME + "/" + CHANNEL_ID);
        }
        return channel;
    }

    private static void usage() {
        System.err.println("Usage: VivoNotificationChannelTool [inspect|disable|restore]");
        System.err.println("Default: inspect (read-only)");
    }
}
