package programmingtheiot.gda.P12.src;
import java.util.Random;


public class SystemMonitor {
    public static String generate() {
        Random r = new Random();
        int cpu = 10 + r.nextInt(80);
        int mem = 20 + r.nextInt(70);
        int disk = 5 + r.nextInt(90);
        return "cpu:" + cpu + ",mem:" + mem + ",disk:" + disk;
    }
}
