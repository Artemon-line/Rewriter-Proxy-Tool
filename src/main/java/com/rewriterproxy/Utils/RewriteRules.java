package com.rewriterproxy.Utils;

public class RewriteRules {

    private static final String REQUEST_REWRITED = "\nREQUEST REWRITED: ";

    private static String oldString, newString;

    /**
     * This method rewrite matched string.
     *
     * @param sourceURI
     * @param newUri
     * @param matchRequest
     * @return String if "sourceURI" contain "uriMasck"<br>
     * "uriMasck" will be replaced to "new newUri"
     * @throws InterruptedException
     */
    public static String rewriteMe(String sourceURI, String newUri, String matchRequest) throws InterruptedException {

        if (sourceURI.contains(matchRequest)) {
            setOldString(sourceURI);
            sourceURI = sourceURI.replace(matchRequest, newUri);
            setNewString(sourceURI);
            System.out.println(REQUEST_REWRITED + getOldString()
                    + " ===> " + sourceURI
                    + " by mask: " + matchRequest);
        }

        return sourceURI;

    }

    private static String getOldString() {
        return oldString;
    }

    private static void setOldString(String oldString) {
        RewriteRules.oldString = oldString;
    }

    private static void setNewString(String newString) {
        RewriteRules.newString = newString;
    }

}
