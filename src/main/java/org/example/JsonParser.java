package org.example;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonParser {

    public JsonParser(){
        JSONParser parser = new JSONParser();

//        System.out.println(obj.toJSONString());

        try {
            parser.parse(new FileReader("data/test.json"));
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
//        JSONObject jsonObject = newJSONObject();
    }
}
