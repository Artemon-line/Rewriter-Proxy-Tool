package com.rewriterproxy.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Validaters {

    private static final Logger LOG = LoggerFactory.getLogger(Validaters.class);
    private static final String DOMAIN_PATTERN = "(https?://)([^:^/]*)(:\\\\d*)?(.*)?";
    private static final String IPADDRESS_PATTERN
            = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static boolean isContainsDomain(String newRequest) {
        return newRequest.matches(DOMAIN_PATTERN);
    }

    public static String getDomain(String newRequest) {
        String newHostHeader = null;
        Pattern domainPattern = Pattern.compile(DOMAIN_PATTERN);
        Matcher m = domainPattern.matcher(newRequest);
        if (m.find()) {
            newHostHeader = m.group(2).replace("www.", "");
        }
        return newHostHeader;
    }

    public static boolean validateIp(final String ip) {
        Pattern ipAddressPAttern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = ipAddressPAttern.matcher(ip);
        return matcher.matches();
    }

    public static int validatePort(String portString) {
        return Integer.parseInt(portString);
    }
}
