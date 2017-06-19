package com.rewriterproxy;

import com.rewriterproxy.Controller.ProxyController;
import com.rewriterproxy.Model.ProxyModel;
import com.rewriterproxy.View.View;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class AppLoader {

    private static ProxyController controller;
    private static ProxyModel proxyModel;
    private static View viewModel;

    public static void main(String[] args) throws IOException, UnknownHostException, InterruptedException, Exception {

        proxyModel = new ProxyModel();
        viewModel = new View();
        controller = new ProxyController(proxyModel, viewModel);

        if (args.length != 0) {
            System.err.println(Arrays.asList(args));

            for (String arg : args) {
                if (!arg.isEmpty() && arg.contains("port:")) {
                    int port = Integer.parseInt(arg.replace("port:", ""));
                    if (port > 1024 && port < 65535) {
                        controller.start(port);
                    }
                }
            }
        } else {
            //Start Server on default port 8745
            controller.start(8745);
        }
    }
}
