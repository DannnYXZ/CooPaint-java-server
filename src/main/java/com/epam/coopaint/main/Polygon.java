package com.epam.coopaint.main;

import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import com.epam.coopaint.util.StringUtil;

import java.util.List;

public class Polygon {
    public static void main(String[] args) {
        for (int i = 0; i < 12; i++) {
            String hash = Encryptor.generateAlphaNumHash(12);
            System.out.println(hash);
        }

        LangPack pack = LangPack.valueOf("EN");
        int x = 0;

        //Properties props = PropertyLoader.loadProperties("/opt/tomcat/apache-tomcat-9.0.27/webapps/coopaint/WEB-INF/classes/com/epam/coopaint/pool/db.properties");
        //int xx = 10;

        //UUID uuid = UUID.fromString("sadasd");
        //int c = 1;

        String text = "/snapshot/YVxteyc6JqgQ4";
        String regex = "/snapshot/([0-9a-zA-Z]*)";
        List<String> groups = StringUtil.parseGroups(text, regex);
        System.out.println(groups);
    }
}
