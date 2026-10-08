package fun.modoru.musubi;

@FunctionalInterface
public interface InterruptableRunnable {

    void run() throws InterruptedException;

}
