package cms.cf.subtitles.srt;

import java.util.ArrayList;

import cms.cf.subtitles.dao.impl.BlockDao;
import cms.cf.subtitles.dao.impl.VideoDao;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.dao.vo.Video;

/**
 * Imprime no console uma legenda PT gravada no banco
 * sem as linhas de indice e marca��o de tempo.
 */
public class PrintSubText
{ 
    public static void main(String[] args)
    {
        main1(new String[]{"U9qEgMIBQ3M"});        
    }
    
    public static void main1(String[] args)
    {
        if (args.length < 1)
        {
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.PrintSubText 'Youtube video ID'");
            System.exit(1);
        }

        String idYoutube = args[0];
        
        if (idYoutube.length() != 11)
        {
            System.err.println("ID do video invalido. N�o tem 11 caracteres ["+idYoutube+"].");
            System.exit(1);
        }
        
        try
        {
            VideoDao.setTestMode();  //obter conexoes locais via DriverManager
            Video v = VideoDao.getByYouTubeID(idYoutube);
            ArrayList<Block> list = BlockDao.listByVideo(v.getId(), Locale.PT);
            
            StringBuffer buff = new StringBuffer();
            
            for (int i = 0; i < list.size(); i++)
            {
                Block block = (Block) list.get(i);
                String text = block.getSubtitleUpdate();
                Srt srt = new Srt(text);
                ArrayList<SrtItem> itens = srt.getItens();
                for (int j = 0; j < itens.size(); j++)
                {
                    SrtItem it = (SrtItem) itens.get(j);
                    //System.out.println(it.getText());
                    buff.append(it.getText()+' ');
                }
            }
            
            String text = buff.toString();
            int p = text.indexOf('.');
            while (p >=0)
            {
                System.out.println(text.substring(0, p+1).trim());
                if(text.length() >= p+1)
                {
                    text = text.substring(p+1);
                    p = text.indexOf('.');
                }
                else
                {
                    text = "";
                    p = -1;
                }
            }
            System.out.println(text.trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);            
        } 
    }
}
