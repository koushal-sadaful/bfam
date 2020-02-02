package marketmaker;

public interface RequestProcessor extends Runnable {
    void stop();

    boolean isAlive();
}
