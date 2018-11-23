package org.elsa.filemanager.common.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author valor
 * @date 2018-11-22 18:59
 */
public class XStreams {

    private volatile static XStream magicApi = null;

    private XStreams() { }

    public static XStream getMagicApi() {
        if (null == magicApi) {
            synchronized (XStreams.class) {
                if (null == magicApi) {
                    magicApi = new XStream();
                    // 只允许转换成map
                    XStream.setupDefaultSecurity(magicApi);
                    magicApi.allowTypeHierarchy(Map.class);

                    magicApi.registerConverter(new MapEntryConverter());
                    magicApi.alias("project", Map.class);
                }
            }
        }
        return magicApi;
    }

    private static class MapEntryConverter implements Converter {

        @Override
        public boolean canConvert(Class clazz) {
            return AbstractMap.class.isAssignableFrom(clazz);
        }

        @Override
        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

            AbstractMap map = (AbstractMap) value;
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode("type");
                writer.addAttribute("value", entry.getKey().toString());
                Object val = entry.getValue();
                if ( null != val ) {
                    writer.setValue(val.toString());
                }
                writer.endNode();
            }

        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Map<Object, Object> map = new HashMap<>();

            while (reader.hasMoreChildren()) {
                reader.moveDown();

                String value = reader.getAttribute("value");
                String key = reader.getValue();
                map.put(key, value);

                reader.moveUp();
            }
            return map;
        }

    }
}
