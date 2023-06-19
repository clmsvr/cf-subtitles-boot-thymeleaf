package cms.cf.subtitles.srt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import cms.cf.subtitles.dao.impl.BlockDao;
import cms.cf.subtitles.dao.impl.VideoDao;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.dao.vo.Video;

/**
 * Gera dois arquivos STR (EN e PT) para um video gravado no banco
 */
public class PrintSrt
{ 
    public static void main(String[] args)
    {
        //main1(new String[]{"eJkQ6G-NUUE", "F:\\Caio Fabio - projetos\\Subtitles\\"});
        //main1(new String[]{"Wd0iYodvXgM", "F:\\Caio Fabio - projetos\\Subtitles\\"});
        main1(new String[]{"U9qEgMIBQ3M", "F:\\Caio Fabio - projetos\\Subtitles\\"});
    }
    
    public static void main1(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.PrintSrt 'Youtube video ID'  'DIR'");
            System.exit(1);
        }

        String idYoutube = args[0];
        String dir = args[1];
        
        if (idYoutube.length() != 11)
        {
            System.err.println("ID do video invalido. N�o tem 11 caracteres ["+idYoutube+"].");
            System.exit(1);
        }
        
        StringBuffer text = new StringBuffer();
        BufferedWriter bw = null;
        try
        {
        	VideoDao.setTestMode();  //obter conexoes locais via DriverManager
            Video v = VideoDao.getByYouTubeID(idYoutube);
            
            System.out.println("###### PT #######");
            
            ArrayList<Block> list = BlockDao.listByVideo(v.getId(), Locale.PT);
            if (list.size() > 0)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    Block block = (Block) list.get(i);
                    text.append(block.getSubtitleUpdate());
                }
                Srt ns = new Srt(text.toString()).removeEmptyItens(); //verificar formato e remover textos vazios ou com ponto apenas.

                //ns = ns.breakWideItens(8,4); //quebra todos os itens com mais de 8 segundos em subitens de 4 seguntos.
                
                File nf = new File(dir, idYoutube+".pt.txt");
                bw = new BufferedWriter(new FileWriter(nf));  //The constructors of this class assume that the default character encoding and the default byte-buffer size are acceptable. To specify these values yourself, construct an OutputStreamWriter on a FileOutputStream.
                //bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nf), "UTF-8"));
                
                //bw.write(text.toString());  
                bw.write(ns.toString());
                try{bw.close();} catch (Exception e){}
            }
            else
            {
                System.out.println("LEGENDA PT n�o encontrada.");
            }
            
            System.out.println("###### EN #######");
            
            list = BlockDao.listByVideo(v.getId(), Locale.EN);
            if (list.size() > 0)
            {
                text = new StringBuffer();
                for (int i = 0; i < list.size(); i++)
                {
                    Block block = (Block) list.get(i);
                    text.append(block.getSubtitleUpdate());
                }
                Srt ns = new Srt(text.toString()).removeEmptyItens(); //verificar formato e remover textos vazios ou com ponto apenas.

                File nf = new File(dir, idYoutube+".en.txt");
                bw = new BufferedWriter(new FileWriter(nf));  
                //bw.write(text.toString());      
                bw.write(ns.toString());
                try{bw.close();} catch (Exception e){}                
            }
            else
            {
                System.out.println("LEGENDA EN n�o encontrada.");
            }  
            
            System.out.println("FIM");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);            
        } 
        finally {
            try{bw.close();} catch (Exception e){}
        }
    }
}
