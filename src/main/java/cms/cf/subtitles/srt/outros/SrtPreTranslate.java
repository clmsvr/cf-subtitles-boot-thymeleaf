package cms.cf.subtitles.srt.outros;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import cms.cf.subtitles.srt.Srt;

public class SrtPreTranslate
{
    public static void main(String[] args)
    {
        main1(new String[]{"F:\\Caio Fabio - projetos\\Subtitles\\Wd0iYodvXgM.srt"});
        //main1(new String[]{"F:\\Caio Fabio - projetos\\Subtitles\\ZHLJSplQi_c.srt"});
    }
    
    public static void main1(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.SrtPreTranslate 'path to SRT file'");
            System.exit(1);
        }

        File file = new File(args[0]);
        String name = file.getName();
        if (name.endsWith(".srt") == false)
        {
            System.err.println("Argumento precisa ser um arquivo '.srt'");
            System.exit(1);
        }
        
        String idYoutube = name.substring(0,name.indexOf(".srt"));
        if (idYoutube.length() != 11)
        {
            System.err.println("ID do video invalido. Não tem 11 caracteres ["+idYoutube+"].");
            System.exit(1);
        }

        BufferedWriter bw = null;
        try 
        {   
            Srt srt = Srt.readSrtFile(file, "UTF-8");
            String pre = srt.formatToTranslate();
            
            File nf = new File(file.getParent(), file.getName().replace(".srt", ".pre.txt"));
            bw = new BufferedWriter(new FileWriter(nf));  
            bw.write(pre);    
        }
        catch (Exception e)
        {
           e.printStackTrace();
           System.exit(1);
        }  
        finally
        {
            try{bw.close();} catch (Exception e){}
        }            
        
        System.out.println("OK. Legenda preparada.");
    }
}
