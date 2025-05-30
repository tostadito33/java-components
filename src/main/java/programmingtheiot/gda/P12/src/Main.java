package programmingtheiot.gda.P12.src;

public class Main {
    public static void main(String[] args) {
        Thread cdaThread = new Thread(() -> CDASimulator.start());
        Thread gdaThread = new Thread(() -> GDA.start());

        cdaThread.start();
        gdaThread.start();
    }
}
