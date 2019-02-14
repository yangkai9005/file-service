package org.elsa.filemanager.self;

import com.thoughtworks.xstream.XStream;
import org.elsa.filemanager.common.utils.Encrypt;
import org.elsa.filemanager.common.utils.XStreams;

import java.util.HashMap;
import java.util.Map;

/**
 * @author valor
 * @date 2018-11-22 18:33
 */
public class XStreamTest {

    public static void main(String[] args) {

        Map<String, String> map = new HashMap<>();
        map.put("xml", "3c3f786d");
        map.put("txt", "4a656e6b");

        XStream magicApi = XStreams.getMagicApi();

        String xml = magicApi.toXML(map);
        System.out.println("Result of tweaked XStream toXml()");
        System.out.println(xml);

        Map<String, String> extractedMap = (Map<String, String>) magicApi.fromXML(xml);
        System.out.println(extractedMap);

        System.out.println(Encrypt.sha256AndBase64("valord577@gmail.com"));
    }

}
