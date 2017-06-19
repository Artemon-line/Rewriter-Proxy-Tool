package com.rewriterproxy.View;

import javax.swing.table.DefaultTableModel;

abstract class RequestTable extends DefaultTableModel {
    
    {
        setColumnCount(2);
        toString();
        addColumn("Original");
        addColumn("Modified");
        insertRow(0, new Object[]{"", ""});
    }
    
    @Override
    public String toString(){
        return "Request table:\n"
                +"c:"+getColumnCount()+"\n"
                +"r:"+getRowCount()+"\n";
    };
    
}
