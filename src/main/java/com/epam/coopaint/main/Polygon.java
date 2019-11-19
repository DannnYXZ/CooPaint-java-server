package com.epam.coopaint.main;

import com.epam.coopaint.util.Encryptor;
import com.epam.coopaint.util.LangPack;
import com.epam.coopaint.util.PropertyLoader;

import java.util.Properties;
import java.util.UUID;

public class Polygon {
    public static void main(String[] args) {
        for (int i = 0; i < 12; i++) {
            String hash = Encryptor.getInstance().generateRandomHash(12);
            System.out.println(hash);
        }

        LangPack pack = LangPack.valueOf("EN");
        int x = 0;

        //Properties props = PropertyLoader.loadProperties("/opt/tomcat/apache-tomcat-9.0.27/webapps/coopaint/WEB-INF/classes/com/epam/coopaint/pool/db.properties");
        //int xx = 10;

        UUID uuid = UUID.fromString("sadasd");
        int c = 1;
    }
}
