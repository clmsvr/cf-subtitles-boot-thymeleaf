package cms.cf.test;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class JsonTest
{
    
/*
 
{
        "data" : [
           { "from" : {  "name" : "xxx", ... }, "message" :  "yyy", ... },
           { "from" : {  "name" : "ppp", ... }, "message" :  "qqq", ... },
           ...
        ],
        ...
}

URL url = new  URL("https://graph.facebook.com/search?q=java&type=post");
   try (InputStream is = url.openStream();
        JsonReader rdr = Json.createReader(is)) {
        JsonObject obj = rdr.readObject();
        JsonArray results = obj.getJsonArray("data");
        for (JsonObject result : results.getValuesAs(JsonObject.class)) {
            System.out.print(result.getJsonObject("from").getString("name"));
            System.out.print(": ");
            System.out.println(result.getString("message", ""));
            System.out.println("-----------");
     
        }
   } 
 
*/
    
    public static void main(String[] args)
    {
        String subtitleText = "[\"teste1\n1\ndot\",\"teste2 2 2\",\"testo 3gfghfghfgbbfjhgghghgjhgjhgjhghgjhghjghjghjghj\",\"claudio ms\",\".\",\".\"]";

        //System.out.println(subtitleText);
        subtitleText = subtitleText.replace("\n", "@@");
        
        JsonReader rdr = Json.createReader(new StringReader(subtitleText.trim()));
        JsonArray results = rdr.readArray();
        for (JsonValue result : results) {
            System.out.println(result.toString().replace("@@", "\n"));
        } 
    }
    
    public static void main3(String[] args)
    {
        String subtitleText = "\"teste1\"";

        JsonReader rdr = Json.createReader(new StringReader(subtitleText.trim()));
        JsonObject r = rdr.readObject();
        System.out.println(r.toString() );

    }
    
    public static void main2(String[] args) {
        String personJSONData = 
            "  {" +
            "   \"name\": \"Jack\", " +
            "   \"age\" : 13, " +
            "   \"isMarried\" : false, " +
            "   \"address\": { " +
            "     \"street\": \"#1234, Main Street\", " +
            "     \"zipCode\": \"123456\" " +
            "   }, " +
            "   \"phoneNumbers\": [\"011-111-1111\", \"11-111-1111\"] " +
            " }";
         
        JsonReader reader = Json.createReader(new StringReader(personJSONData));
         
        JsonObject personObject = reader.readObject();
         
        reader.close();
         
        System.out.println("Name   : " + personObject.getString("name"));
        System.out.println("Age    : " + personObject.getInt("age"));
        System.out.println("Married: " + personObject.getBoolean("isMarried"));

        JsonObject addressObject = personObject.getJsonObject("address");
        System.out.println("Address: ");
        System.out.println(addressObject.getString("street"));
        System.out.println(addressObject.getString("zipCode"));
         
        System.out.println("Phone  : ");
         JsonArray phoneNumbersArray = personObject.getJsonArray("phoneNumbers");
        for (JsonValue jsonValue : phoneNumbersArray) {
            System.out.println(jsonValue.toString());
        }
    }    
}
