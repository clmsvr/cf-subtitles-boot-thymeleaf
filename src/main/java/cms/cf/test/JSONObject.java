package cms.cf.test;

import java.util.ArrayList;

import flexjson.JSON;
import flexjson.JSONSerializer;

/**
 * Objeto JSON de exemplo
 */
public class JSONObject
{
    public static enum TYPE {ROOT,VERSION,COMPONENT,PROPERTY,ERROR} 

    protected TYPE type; 
    protected String   text;
    protected boolean  editable;
    protected int      count;

    protected ArrayList<JSONObject> children;

    public JSONObject(String text,TYPE type, boolean editable, int count)
    {
        this.text = text;
        this.type = type;
        this.editable = editable;
        this.count = count;
    }

    public ArrayList<JSONObject> getChildren()
    {
        return children;
    }

    @JSON
    public void setChildren(ArrayList<JSONObject> children)
    {
        this.children = children;
    }

    public TYPE getType()
    {
        return type;
    }

    public void setType(TYPE type)
    {
        this.type = type;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
    
    public static String serialize(JSONObject obj)
    {
//        JSONSerializer serializer = new JSONSerializer(); 
//        serializer = serializer.exclude("*.class").include("children"); 
//        //return serializer.serialize( obj );   
//        return serializer.prettyPrint(obj);  
        
        JSONSerializer serializer = new JSONSerializer().prettyPrint(true); 
        serializer = serializer.exclude("*.class").include("children"); 
        return serializer.serialize( obj );   
    }      
}
