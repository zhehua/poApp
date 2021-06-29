package org.app;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.mitm.CertificateAndKeySource;
import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.bouncycastle.util.test.Test;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class App {
    public static void main(String[] args) {
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        try {
            InputStream inputStream = App.class.getResourceAsStream("/cer/keystore.p12");
            File file = new File("keystore.p12");//File.createTempFile("keystore", ".p12");
            if(!file.exists()){
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                }
            }
            CertificateAndKeySource existingCertificateSource =
                    new KeyStoreFileCertificateSource("PKCS12", file, "privateKeyAlias", "password9");
            ImpersonatingMitmManager mitmManager = ImpersonatingMitmManager.builder()
                    .rootCertificateSource(existingCertificateSource)
                    .build();

            proxy.setMitmManager(mitmManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        proxy.addRequestFilter((httpRequest, httpMessageContents, httpMessageInfo) -> {
            System.out.println(httpRequest.getUri());
            return null;
        });
        proxy.start(8080);
    }
}
