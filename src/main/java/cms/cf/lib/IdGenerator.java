package cms.cf.lib;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


/**
 * Classe respons�vel pela gera��o de n�meros sequencias para 
 * identifica��o �nica.
 * 
 * Formado das transa��es:  yyMMddHHmmssRR0000  Number(18)
 * 
 * RR -> random number: 0 to 99
 * 0000 -> numero sequencial: 0 a 9999
 */
public class IdGenerator 
{
    private long 		      lastId_ = 0;
    /** random number: 0 to 99 */
    private int               RR_ = 0; 
	private SimpleDateFormat  formatter_ = null;

	public IdGenerator() 
	{		
		formatter_ = new SimpleDateFormat("yyMMddHHmmss");
		formatter_.setTimeZone(TimeZone.getTimeZone("GMT-3"));
		RR_ = new Random().nextInt(100);
	}
    public IdGenerator(int RR) 
    {       
        formatter_ = new SimpleDateFormat("yyMMddHHmmss");
        formatter_.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        if (RR >= 0 && RR < 100)
            RR_ = RR;
        else
            RR_ = new Random().nextInt(100);
    }	

	public long nextValue()
	{
		//formato:  yyMMddHHmmssRR0000
        //                       10000	    
		//                     1000000
		//         1000000000000000000
		long newTransId = 0;

		synchronized(this) //sincroniza��o para cria��o de novo valor de transas��o
		{
		    lastId_++;
            if (lastId_ > 9999 )
            {
                lastId_ = 1; 
            }	    
			newTransId = lastId_;	
		}
		long h = new Long(formatter_.format(new Date())).longValue();

		return (h * 1000000) + (RR_*10000) + newTransId;		       
	}

	public static void main(String[] args) 
	{
		IdGenerator gen = new IdGenerator();
        for (int i = 0; i < 1000000; i++) 
        {
            if (i%100 == 0)
                 System.out.println("id: "+ gen.nextValue());
            else gen.nextValue();
        }     
	}
}


