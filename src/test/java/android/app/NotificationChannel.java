package android.app;
public final class NotificationChannel {
    private int importance;
    private boolean blockable;

    public NotificationChannel(int importance, boolean blockable) {
        this.importance = importance;
        this.blockable = blockable;
    }
    public int getImportance() { return importance; }
    public void setImportance(int importance) { this.importance = importance; }
    public boolean isBlockable() { return blockable; }
    public void setBlockable(boolean blockable) { this.blockable = blockable; }
    public NotificationChannel copy() { return new NotificationChannel(importance, blockable); }
}
