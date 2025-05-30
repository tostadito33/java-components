package programmingtheiot.gda.P12.src;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocketFactory;


public class SSLUtil {
    public static SSLSocketFactory getSocketFactory() throws Exception {
        String caCrtFile = "certificates/ca.crt";
        String crtFile = "certificates/client.crt";
        String keyFile = "certificates/client.key";

        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c",
                "openssl pkcs12 -export -in " + crtFile + " -inkey " + keyFile + " -out client.p12 -name mqtt-client -CAfile " + caCrtFile + " -caname root -passout pass:password"});
        p.waitFor();

        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream("client.p12"), "password".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "password".toCharArray());

        KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
        ts.load(null);
        ts.setCertificateEntry("ca-certificate", CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(caCrtFile)));

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return ctx.getSocketFactory();
    }
}
