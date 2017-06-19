package com.rnd.rewriterproxy;

import com.rewriterproxy.Model.ProxyModel;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ProxyModelTest {

    static final String TEST_STRING = "testString";
    static Proxy proxy;
    static HttpURLConnection connection;
    SocketAddress addres;
    ProxyModel instance;

    @Before
    public void setUpMethod() throws Exception {

        instance = new ProxyModel();
        instance.startProxy(8745);
        addres = new InetSocketAddress(instance.getAddress(), instance.getPort());
        proxy = new Proxy(Proxy.Type.HTTP, addres);

    }

    private static HttpURLConnection makeConnection(URL url) throws IOException {

        connection = (HttpURLConnection) url.openConnection(proxy);
        connection.setRequestMethod("GET");

        //add request header
        connection.setRequestProperty("User-Agent", "Mizilla 5/0");
        connection.setRequestProperty("TestHeader", "Test");
        connection.connect();

        System.out.println(connection.getResponseCode());
        connection.disconnect();
        return connection;
    }

    @After
    public void tearDownMethod() throws Exception {
        instance.stopProxy();
    }

    /**
     * Test of addReqFilterToProxy method, of class ProxySetup.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAddReqFilterToProxy() throws Exception {
        URL newRequest = new URL("http://aaa");
        URL matchRequest = new URL("http://bbb");

        instance.addReqFilterToProxy(newRequest.toExternalForm(), matchRequest.toExternalForm());

        makeConnection(matchRequest);
        assertEquals(instance.getHarEntry(0).getRequest().getUrl(), newRequest.toExternalForm());
        assertEquals(newRequest.getHost(),
                instance.getHarEntry(0).getRequest().getHeaders()
                .stream()
                .filter(x -> x.getName().equals("Host")).findFirst().get().getValue()
        );
    }

    /**
     * Test of setMatchString method, of class ProxySetup.
     */
    //@Test
    public void testSetMatchString() {
        //TODO
    }

    /**
     * Test of removeReqFilter method, of class ProxySetup.
     */
    @Test
    public void testRemoveReqFilter() throws IOException {
        boolean filterStatus = instance.removeReqFilter();
        assertEquals(instance.getFilterStatus(), !filterStatus);
    }

    /**
     * Test of addSniffer method, of class ProxySetup.
     */
    @Test
    public void testAddSniffer() throws IOException, InterruptedException, Exception {
        instance.addMonitoring();
        assertTrue(instance.isMonitored());
    }

    /**
     * Test of refreshFilter method, of class ProxySetup.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRefreshFilter() throws Exception {
        HashMap<String, String> tableData = new HashMap<>();
        assertTrue(tableData.isEmpty());
        tableData.put("12345", TEST_STRING);
        instance.refreshFilter(tableData);
        assertFalse(tableData.isEmpty());
        assertTrue(instance.isMonitored());
    }

}
