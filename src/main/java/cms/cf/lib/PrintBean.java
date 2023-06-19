package cms.cf.lib;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class PrintBean
{

    protected static String getSizedString(String value, int fieldLen)
    {
        char[] blanks = new char[fieldLen];
        Arrays.fill(blanks, ' ');
        StringBuffer bf = new StringBuffer();
        bf.append(value);
        bf.append(blanks);
        return bf.toString().substring(0, fieldLen);
    }
    
    public static void print(Object bean, java.io.PrintStream stream) 
    //throws Exception
    {
        try
        {
            BeanInfo info = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] bprops = info.getPropertyDescriptors();

            stream.println("** " + bean.getClass().getSimpleName());

            for (PropertyDescriptor prop : bprops)
            {
                Object value = null;
                String name = prop.getName().trim();
                if (name.equals("class")) continue;

                Method met = prop.getReadMethod();
                if (met != null)
                {
                    value = met.invoke(bean, (Object[]) null);
                }
                stream.println("   "+getSizedString(name, 15)+ ": [" + value + "]");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
