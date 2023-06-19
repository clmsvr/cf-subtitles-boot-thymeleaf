package cms.cf.subtitles.srt;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import cms.cf.subtitles.dao.impl.VideoWrapDao;
import cms.cf.subtitles.dao.vo.Block.Locale;

/**
 * Carga de legendas no formato SRT.
 * Carrega as legendas em Branco, com '.', para portugu�s e Ingl�s.
 * 
 * O video n�o pode existir no banco de dados
 */
public class SrtDeploy
{

    private static void log(String str) 
    {
        System.out.println(str);
    }
    
   
    public static void main(String[] args)
    {
        main1(new String[]{"F:\\Caio Fabio - projetos\\Subtitles\\U9qEgMIBQ3M.srt"}); 
    }
    
    public static void main1(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.SrtDeploy 'path to SRT file'");
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
        
        Connection conn = null;        
        String title = null;
        try
        {
            VideoWrapDao.setTestMode();  //obter conexoes locais via DriverManager
            conn = VideoWrapDao.getConnection();
            
            Srt srt = Srt.readSrtFile(file, "UTF-8");
            srt = srt.joinTimeBorders(); //eliminar intervalos entre itens de legenda.
            srt = srt.breakWideItens(8,4); //quebra todos os itens com mais de 8 segundos em subitens de 4 seguntos.
            srt = srt.cleanupSubtitleText(); //inserir no banco legenda com texto vazio.
            Srt[] blocks = srt.toBlocks(5);
            log("\nID Youtube ["+idYoutube+"] Blocos ["+blocks.length+"] Size["+blocks[0].getItens().size()+"] Last ["+blocks[blocks.length-1].getItens().size()+"]");

            System.out.println("Inicio: Buscando Titulo no YouTube...");
            title =YouTube.getVideoTitle(idYoutube);  
            
            System.out.println("Inserindo blocos de legenda vazios no banco para PT e EN...");
            VideoWrapDao.insertVideo(conn,idYoutube, title, blocks, Locale.PT);
            VideoWrapDao.insertBlocks(conn,idYoutube, blocks, Locale.EN);            
            
            conn.commit();
            
            System.out.println("OK. Video Registrado: '"+title+"'");
        }
        catch(Exception e)
        {
            try{if (conn != null)conn.rollback();}catch (SQLException e1){}
            e.printStackTrace();
            System.exit(1);            
        }
        finally {
            if (conn != null) try{VideoWrapDao.closeConnection(conn);}catch(Exception e){}
        }
    }
}
