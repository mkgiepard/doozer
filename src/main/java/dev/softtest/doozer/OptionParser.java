package dev.softtest.doozer;

import java.util.Map;
import java.util.HashMap;

public class OptionParser {
    // in:                              out:
    // null                             null
    // ''                               {}
    // option                           {default: option}
    // key1=value1,key2=value2          {key1:value1, key2=value2}
    // key1=val\,ue1,key2=value2        {key1:value1, key2=value2}
    // key1=vale\=ue1,key2=value2       {key1:value1, key2=value2}
    
    String in;
    public OptionParser(String in) {
        this.in = in;
    }

    public Map<String, String> parse() throws Exception {
        Map<String, String> options = new HashMap<>();

        if (this.in == null) return null;
        if (this.in.isEmpty()) return options;

        String encoded = encode(in);
        if (!encoded.contains("=")) {
            options.put("default", decode(encoded));
            return options;
        }

        String[] pairs = encoded.split(",");
        for (String p : pairs) {
            String[] option = p.split("=");
            if (option.length != 2) throw new Exception("Cannot parse options!");
            options.put(option[0], decode(option[1]));
        }
        return options;
    }

    private String encode(String s) {
        return s.replace("\\,", "<COMMA>").replace("\\=", "<EQUALS>");
    }

    private String decode(String s) {
        return s.replace("<COMMA>", ",").replace("<EQUALS>", "=");
    }
}
