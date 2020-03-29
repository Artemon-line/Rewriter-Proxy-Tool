package com.rnd.rewriterproxy;

import com.rewriterproxy.Utils.Validaters;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Artemy
 */
public class ValidatersTest {

    @Test
    public void testHostValidator() {
        String fullDomain = "https://domain.com.it";
        String domainMatched = Validaters.getDomain("https://domain.com.it");
        Assert.assertTrue(Validaters.isContainsDomain(fullDomain));
        Assert.assertEquals("domain.com.it", domainMatched);
    }

    @Test
    public void testIpAddressValidation() {
        Assert.assertTrue(Validaters.validateIp("192.168.0.4"));
    }

}
