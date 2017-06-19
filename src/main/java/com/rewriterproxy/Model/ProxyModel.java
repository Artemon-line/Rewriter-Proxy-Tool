package com.rewriterproxy.Model;

import com.rewriterproxy.Utils.RewriteRules;
import com.rewriterproxy.Utils.Validaters;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyModel {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyModel.class);

    private Map<String, String> customHeadersMap, rewriteMap;
    private boolean monitoring;
    private DefaultTableModel proxyTableModel;
    private Har har;
    private int counter = 0;
    private int index;
    private boolean isChProxy = false;
    private HarLog harLog = new HarLog();
    private String chProxyAddres;
    private int chProxyPort;
    private boolean sslMonitor;

    private BrowserMobProxyServer bmp;
    private int proxyPort;

    public ProxyModel() {

        customHeadersMap = new HashMap();
        rewriteMap = new HashMap<>();
        har = new Har();
        sslMonitor = false;
        initProxy();  // init proxy        

    }

    private void initProxy() {

        bmp = new BrowserMobProxyServer();

        bmp.setMitmDisabled(!isSslMonitored());  // SLL --- do not change this
        LOG.info("Ssl monitoring status:" + !bmp.isMitmDisabled());
        bmp.setTrustAllServers(true);
    }

    /**
     * add new request filter to Proxy if argument newRequest contains different
     * domain add header host with new domain
     *
     * @param newRequest
     * @param matchRequest
     */
    public void addReqFilterToProxy(final String newRequest, final String matchRequest) {

        LOG.info("\naddReqFilter \nmatch: "
                + matchRequest + "\nnew: " + newRequest);

        final String newHostHeader = Validaters.getDomain(newRequest);

        bmp.addRequestFilter((HttpRequest hr, HttpMessageContents hmc, HttpMessageInfo hmi) -> {
            try {
                if (!newRequest.isEmpty() && hmi.getOriginalRequest().getUri().contains(matchRequest)) {

                    //Rewrite request
                    hr.setUri(RewriteRules.rewriteMe(
                            hmi.getOriginalRequest().getUri(),
                            newRequest.replaceAll("\\s", ""),
                            matchRequest.replaceAll("\\s", "")));

                    //Replace Host header
                    if (null != newHostHeader) {
                        LOG.info("Host matched: " + newHostHeader);
                        hr.headers().set("Host", newHostHeader);
                    }
                }

            } catch (InterruptedException ex) {
                System.out.println("\n--- failed to replace\n" + ex.getMessage());
            }
            return null;
        });

    }

    /**
     * Remove all previously added filters. Also remove Custom headers. <br>
     * Set sniffer status false
     *
     * @return filterList status
     * @throws java.io.IOException
     * @see net.lightbody.bmp.BrowserMobProxyServer#getFilterFactories()
     */
    public boolean removeReqFilter() throws IOException {
        bmp.getFilterFactories().clear();
        LOG.info("\nremoveReqFilter: " + bmp.getFilterFactories().isEmpty());
        setMonitoring(false);
        return bmp.getFilterFactories().isEmpty();

    }

    public HarLog addHarWriter() {

        bmp.newHar();

        bmp.setHarCaptureTypes(
                CaptureType.REQUEST_HEADERS,
                CaptureType.REQUEST_CONTENT,
                CaptureType.REQUEST_COOKIES,
                CaptureType.REQUEST_BINARY_CONTENT,
                CaptureType.RESPONSE_BINARY_CONTENT,
                CaptureType.RESPONSE_CONTENT,
                CaptureType.RESPONSE_COOKIES,
                CaptureType.RESPONSE_HEADERS);
        har = bmp.getHar();

        if (harLog.getEntries().isEmpty()) {
            harLog = har.getLog();
        } else {
            har.setLog(harLog);
        }
        return har.getLog();
    }

    /**
     * Method addReq two monitoring filters (RequestFilter, ResponseFilter).
     *
     * <p>
     * RequestFilter out >> " + hr.getMethod() + " " + hr.getUri().<br>
     * ResponseFilter out << " + response.getProtocolVersion() +
     * response.getStatus() + response.getDecoderResult(). @see
     * BrowserMobProxyServer @throws IOException @throws java.io.IOException
     */
    public void addMonitoring() {

        if (!isMonitored()) {
            addHarWriter();

            bmp.addRequestFilter((HttpRequest hr, HttpMessageContents hmc, HttpMessageInfo hmi) -> {
                System.out.println(">> " + hr.getMethod() + " " + hmi.getUrl());
                return null;
            });

            bmp.addResponseFilter((HttpResponse response, HttpMessageContents contents, HttpMessageInfo hmi) -> {
                System.out.println("<<" + response.getProtocolVersion() + " " + response.getStatus() + " " + response.getDecoderResult());

                //write har data and index to table
                index = har.getLog().getEntries().size();
                while (index != counter) {
                    proxyTableModel.addRow(new Object[]{
                        har.getLog().getEntries().get(counter).getRequest().getMethod(),
                        har.getLog().getEntries().get(counter).getRequest().getUrl(),
                        har.getLog().getEntries().get(counter).getResponse().getStatusText(),
                        counter
                    });
                    counter++;
                }
            });

            this.monitoring = true;

        }
    }

    /**
     * start the server: addMonitoring -- added new monitoring;
     *
     * @param port
     */
    public void startProxy(int port) {
        this.proxyPort = port;
        try {
            bmp.start(proxyPort, InetAddress.getLocalHost());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(-1);
        }
        addMonitoring();
    }

    /**
     * Stop the server, if it already started.
     */
    public void stopProxy() {
        if (bmp.isStarted()) {
            bmp.stop();
            LOG.info("server stoped");
        } else {
            LOG.info("stoped");
        }
    }

    private void restartProxy(int port) {
        stopProxy();
        initProxy();
        if (isChProxy) {
            bmp.setChainedProxy(new InetSocketAddress(chProxyAddres, chProxyPort));
        }

        if (!customHeadersMap.isEmpty()) {
            addCustomHeaders(customHeadersMap);
        }
        setMonitoring(false);
        startProxy(port);
    }

    /**
     * Add custom headers from "headersMap" to all Requests
     *
     * @param headersMap
     */
    public void addCustomHeaders(Map<String, String> headersMap) {
        if (!headersMap.isEmpty()) {
            this.customHeadersMap = headersMap;
            bmp.addHeaders(customHeadersMap);
            LOG.info("\naddCHdrs: " + customHeadersMap);
        }
    }

    /**
     * Remove all custom headers from headersMap, previously added.
     *
     */
    public void removeCustomHeaders() {
        customHeadersMap.clear();
        bmp.removeAllHeaders();
    }

    /**
     * Method to refresh filters, from actual values. remove filter addSniffer
     * addReq new Filter
     *
     * @param tableData
     */
    public void refreshFilter(final Map<String, String> tableData) {
        restartProxy(getPort());

        if (!tableData.isEmpty()) {
            tableData.entrySet().stream()
                    .filter((entrySet)
                            -> (!entrySet.getValue().isEmpty() && !entrySet.getKey().isEmpty()))
                    .forEach((entrySet) -> {
                        addReqFilterToProxy(entrySet.getValue(), entrySet.getKey());
                    });
        }
    }

    public void setChainedProxy(String proxyToChain) {
        LOG.info("isChProxy: " + isChProxy);
        String host;
        int port;
        if (proxyToChain.isEmpty()) {
        } else if (isChProxy && proxyToChain.isEmpty()) {
            setChProxy(false);
            restartProxy(getPort());
        } else {
            if (proxyToChain.contains(":")) {
                String[] split = proxyToChain.split(":");
                host = split[0];
                port = Integer.parseInt(split[1]);
            } else {
                host = proxyToChain.trim();
                port = 8080;
            }
            this.chProxyAddres = host;
            this.chProxyPort = port;
            setChProxy(true);
            restartProxy(getPort());
            LOG.info("Chained Proxy set to: " + proxyToChain);
        }

    }

    public HarEntry getHarEntry(int index) {
        return har.getLog().getEntries().get(index);
    }

    public boolean isMonitored() {
        return this.monitoring;
    }

    public void setMonitoring(boolean mon) {
        this.monitoring = mon;
    }

    public int getPort() {
        return this.proxyPort;
    }

    public boolean getStatus() {
        return bmp.isStarted();
    }

    public DefaultTableModel getProxyModel() {
        return proxyTableModel;
    }

    private void setChProxy(boolean b) {
        this.isChProxy = b;
    }

    public String getProxyStatusLine() {
        return "Proxy started on " + bmp.getClientBindAddress().getHostAddress() + ":"
                + bmp.getPort();
    }

    public void setSslMonitoringWithRestart(boolean sslMon) throws Exception {
        this.sslMonitor = sslMon;
        LOG.info("setSslMonitoring: " + this.sslMonitor);
        restartProxy(getPort());
    }

    public boolean isSslMonitored() {
        return sslMonitor;
    }

    public void setSslMonitoring(boolean sslMon) {
        this.sslMonitor = sslMon;
    }

    public InetAddress getAddress() {
        return this.bmp.getClientBindAddress();
    }

    public boolean getFilterStatus() {
        return !bmp.getFilterFactories().isEmpty();
    }

    public void setProxyPort(int port) {
        this.proxyPort = port;
    }

    public DefaultTableModel getProxyTableModel() {
        return proxyTableModel;
    }

    public void setProxyTableModel(DefaultTableModel proxyTableModel) {
        this.proxyTableModel = proxyTableModel;
    }

    public String getChainedProxyAddres() {
        return (bmp.getChainedProxy() != null) ? bmp.getChainedProxy().getAddress().getHostAddress() : "";
    }

}
