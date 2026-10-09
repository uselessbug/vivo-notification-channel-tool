import android.app.FakeNotificationManager;

public final class TestHarness {
    public static void main(String[] args) throws Exception {
        FakeNotificationManager fake = FakeNotificationManager.INSTANCE;
        assertState(3, false);
        VivoNotificationChannelTool.main(new String[0]);
        assertState(3, false);
        if (fake.writes != 0) throw new AssertionError("Default inspect modified the channel");
        VivoNotificationChannelTool.main(new String[]{"inspect"});
        if (fake.writes != 0) throw new AssertionError("Inspect modified the channel");

        VivoNotificationChannelTool.main(new String[]{"disable"});
        assertState(0, true);
        if (fake.writes != 2) throw new AssertionError("Disable must perform exactly 2 updates");

        VivoNotificationChannelTool.main(new String[]{"restore"});
        assertState(3, false);
        if (fake.writes != 3) throw new AssertionError("Restore must perform exactly 1 update");

        System.out.println("PASS: read-only default, protected-channel two-step disable and restore");
    }

    private static void assertState(int importance, boolean blockable) {
        FakeNotificationManager fake = FakeNotificationManager.INSTANCE;
        if (fake.importance() != importance || fake.blockable() != blockable) {
            throw new AssertionError("Unexpected fake channel state");
        }
    }
}
