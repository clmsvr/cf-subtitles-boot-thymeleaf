package cms.cf.subtitles.srt.outros;

import java.io.File;

import cms.cf.subtitles.dao.impl.VideoWrapDao;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.srt.Srt;
import cms.cf.subtitles.srt.YouTube;

public class SrtDeployEmptyText
{

    private static void log(String str) 
    {
        System.out.println(str);
    }
    
   
    public static void main(String[] args)
    {
        main1(new String[]{"F:\\Caio Fabio - projetos\\Subtitles\\Wd0iYodvXgM.srt"});
        //main1(new String[]{"F:\\Caio Fabio - projetos\\Subtitles\\ZHLJSplQi_c.srt"});
        
    }
    
    public static void main1(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.SrtDeployEmptyText 'path to SRT file'");
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
            System.err.println("ID do video invalido. N�o tem 11 caracteres ["+idYoutube+"].");
            System.exit(1);
        }
        
        System.out.println("Inicio: Buscando Titulo do video ["+idYoutube+"] no YouTube...");
        String title = null;
        try
        {
            title =YouTube.getVideoTitle(idYoutube);    
        }
        catch(Exception e)
        {
            System.err.println("Falha no acesso ao YouTube para obter o titulo do Video.");
            System.exit(1); 
        }
        System.out.println("Video Title: '"+title+"'");
        try 
        {   
            Srt srt = Srt.readSrtFile(file, "UTF-8");
            srt = srt.cleanupSubtitleText(); //inserir no banco legenda com texto vazio.
            Srt[] blocks = srt.toBlocks(5);
            
            log("\n Inserindo textos vazios para Portugues e Ingles.");
            log("\nID Youtube ["+idYoutube+"] Blocos ["+blocks.length+"] Size["+blocks[0].getItens().size()+"] Last ["+blocks[blocks.length-1].getItens().size()+"]");

            VideoWrapDao.setTestMode();  //obter conexoes locais via DriverManager          
            //VideoWrapDao.insertVideo(idYoutube, title, blocks, Locale.PT);
            VideoWrapDao.insertBlocks(idYoutube, blocks, Locale.EN);
        }
        catch (Exception e)
        {
           e.printStackTrace();
           System.exit(1);
        }  
        
        System.out.println("OK. Video registrado no banco de dados.");
    }
}
