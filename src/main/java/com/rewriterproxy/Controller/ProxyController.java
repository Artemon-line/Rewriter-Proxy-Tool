package com.rewriterproxy.Controller;

import com.rewriterproxy.Model.ProxyModel;
import com.rewriterproxy.View.View;
import java.util.HashMap;

public class ProxyController {
    

    private final ProxyModel model;
    private final View view;

    public ProxyController(ProxyModel proxyModel, View viewModel) {
        this.model = proxyModel;
        this.view = viewModel;
    }

    private void init() throws Exception {
        view.setRepoUrl("https://github.com/Artemon-line/Rewriter-Proxy-Tool/");
        view.setReqestMap(new HashMap<>());        
        view.setProxyModel(model);
        view.start();
    }

    /**
     * for test
     *
     * @param port
     * @throws java.lang.Exception
     */
    public void start(final int port) throws Exception {
        model.setProxyTableModel(view.getProxyTableModel());
        model.startProxy(port);
        init();

    }

}
