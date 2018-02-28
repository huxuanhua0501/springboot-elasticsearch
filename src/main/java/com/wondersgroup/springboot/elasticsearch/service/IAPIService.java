package com.wondersgroup.springboot.elasticsearch.service;

import javax.swing.plaf.PanelUI;
import java.io.IOException;

public interface IAPIService {
    public String insertUser();

    public String insertUser2() throws IOException;

    public long delete1();

    public long delete2();


    public void pulkInsert() throws IOException;

    public void pulkText() throws IOException;

    public String searchRresponse();

}
