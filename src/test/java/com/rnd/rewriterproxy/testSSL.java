package com.rnd.rewriterproxy;

import com.rewriterproxy.Model.ProxyModel;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;

public class testSSL {

    private static ProxyModel instance;

    @BeforeClass
    public static void setUpMethod() throws Exception {
        instance = new ProxyModel();

    }

    @AfterClass
    public static void tearDownMethod() throws Exception {
        instance.stopProxy();
    }

    //@Test
    public void testSSLConnection() throws Exception {
//        instance.bmp.setMitmDisabled(false);
//        
//        instance.bmp.setMitmManager(ImpersonatingMitmManager.builder().trustAllServers(true).build()); 
        instance.setSslMonitoring(false);
        instance.startProxy(8745);
        SocketAddress addres = new InetSocketAddress(instance.getAddress(), instance.getPort());
        Proxy proxy = new Proxy(Proxy.Type.HTTP, addres);

        URL obj = new URL("https://google.com/");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);

        con.setRequestProperty("Method", "GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mizilla 5/0");
        con.setRequestProperty("TestHeader", "Test");
        System.out.println(con.getRequestProperties().toString());

        con.connect();

        assertTrue(instance.getProxyModel().getValueAt(0, 1).toString().contains("https"));

    }

}
