package com.rnd.rewriterproxy;

import com.rewriterproxy.Utils.RewriteRules;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class RewriteRulesTest {

    /**
     * Test of rewriteMe method, of class RewriteRules.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRewriteMe_3args_2() throws Exception {
        System.out.println("testRewriteMe_3args_2");
        String sourceURI = "http://www.test.com/testMask?Qa=sdsd";
        String uriMasck = "testMask?Qa";
        String newUri = "NEWtestNEWMask?NEWQa";
        String result = RewriteRules.rewriteMe(sourceURI, newUri, uriMasck);
        assertTrue(result.contains(newUri));
    }

    @Test
    public void testRewriteMeWithDomain() throws Exception {
        System.out.println("testRewriteMe_testRewriteMe_Domain");
        String sourceURI = "http://www.test.com/testMask?Qa=sdsd";
        String uriMasck = "test.com/testMask?Qa=sdsd";
        String newUri = "west.com/testMask?Qa=sdsd";
        String result = RewriteRules.rewriteMe(sourceURI, newUri, uriMasck);
        assertTrue(result.contains(newUri));
    }

}
