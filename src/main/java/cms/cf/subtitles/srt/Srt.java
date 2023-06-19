package cms.cf.subtitles.srt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import cms.cf.subtitles.dao.impl.VideoWrapDao;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.VideoWrap;

public class Srt
{
    private ArrayList<SrtItem> itens = new ArrayList<>();    
    
    public Srt()
    {
    }
    
    public Srt(ArrayList<SrtItem> itens)
    {
        this.itens = itens;
    }
    
    public Srt(String subtitleText) 
    throws Exception
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new StringReader(subtitleText.trim()));
            String line;
            
            int pos = 0;
            while ((line = br.readLine()) != null)
            {
                pos++;
                //verificar numero de sequencia
                SrtItem item = new SrtItem();
                try
                {
                    //if (pos != 1)Integer.parseInt(line); //pular primeira linha por causa de caracteres especiais ocultos.
                    item.setIndex(Integer.parseInt(line));
                }
                catch (NumberFormatException e)
                {
                    throw new NumberFormatException("Arquivo com formato invalido. Numero de sequencia esperado na linha "+ pos+ ". Detail: "+e.toString());
                }
                
                //verificar Time Code
                line = br.readLine();
                if (line == null || line.length() < 29) throw new Exception("Esperado Time code na linha "+(pos+1)+" :"+line);
                try
                {
                    int h =  Integer.parseInt(line.substring(0, 2));
                    int m =  Integer.parseInt(line.substring(3, 5));
                    int s =  Integer.parseInt(line.substring(6, 8));
                    int ms = Integer.parseInt(line.substring(9, 12));
                    
                    item.setBegin(h*60*60*1000 + m*60*1000 + s*1000 + ms);
                    
                    h =  Integer.parseInt(line.substring(17, 19));
                    m =  Integer.parseInt(line.substring(20, 22));
                    s =  Integer.parseInt(line.substring(23, 25));
                    ms = Integer.parseInt(line.substring(26));
                    
                    item.setEnd(h*60*60*1000 + m*60*1000 + s*1000 + ms);                  
                }
                catch (NumberFormatException e)
                {
                    throw new NumberFormatException("Invalido Formato Time code na linha "+(pos+1)+" :"+line + ". Detail: "+e.toString());
                }
                
                //Ler as linhas de legenda ate a linha em branco ou fim de arquivo
                //se tiver linha em branco no meio do texto da legenda eh erro de formato
                String text = "";
                while ((line = br.readLine()) != null)
                {
                    if (line.trim().equals(""))
                    {
                        break;
                    }
                    else 
                    {
                        text = text + line + "\n";       
                    }
                }
                item.setText(text.trim());
                itens.add(item);
            }
        }
        finally
        {
            try{br.close();}catch (Exception e){}
        }
    }
    
    public static Srt readSrtFile(File file, String encode) //ex: "UTF8"
    throws Exception
    {
        String text;
        
        BufferedReader br = null;
        try
        {
            StringBuilder sb = new StringBuilder();
            //br = new BufferedReader(new FileReader(file));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
            String line;
            
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            
            text = sb.toString();
            return new Srt(text);
        }
        finally
        {
            try{if (br != null)br.close();}catch (Exception e){}
        }
    }

    public static Srt readSbvFile(File file, String encode) //ex: "UTF8"
    throws Exception
    {
        String text;
        
        BufferedReader br = null;
        try
        {
            StringBuilder sb = new StringBuilder();
            //br = new BufferedReader(new FileReader(file));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
            String line;
            
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            
            text = sb.toString();
            return parseSbvFormat(text);
        }
        finally
        {
            try{if (br != null)br.close();}catch (Exception e){}
        }
    }
    
    private static Srt parseSbvFormat(String sbvSubtitleText) 
    throws Exception
    {
        ArrayList<SrtItem> itens = new ArrayList<>();
        
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new StringReader(sbvSubtitleText.trim()));
            String line;
            
            int pos = 0;
            while ((line = br.readLine()) != null)
            {
                pos++;
                //verificar numero de sequencia
                SrtItem item = new SrtItem();
                
                item.setIndex(pos);
                
                line = line.trim();
                
                if (line.length() != 23)
                    throw new Exception("Erro de formato: esperado TIMESTAMP na linha:"+ line);
                
                //verificar Time Code
                try
                {
                    int h =  Integer.parseInt(line.substring(0, 1));
                    int m =  Integer.parseInt(line.substring(2, 4));
                    int s =  Integer.parseInt(line.substring(5, 7));
                    int ms = Integer.parseInt(line.substring(8, 11));
                    
                    item.setBegin(h*60*60*1000 + m*60*1000 + s*1000 + ms);
                    
                    h =  Integer.parseInt(line.substring(12, 13));
                    m =  Integer.parseInt(line.substring(14, 16));
                    s =  Integer.parseInt(line.substring(17, 19));
                    ms = Integer.parseInt(line.substring(20));
                    
                    item.setEnd(h*60*60*1000 + m*60*1000 + s*1000 + ms);                  
                }
                catch (NumberFormatException e)
                {
                    throw new NumberFormatException("Invalido Formato Time code na linha "+(pos+1)+" :"+line + ". Detail: "+e.toString());
                }
                
                //Ler as linhas de legenda ate a linha em branco ou fim de arquivo
                String text = "";
                while ((line = br.readLine()) != null)
                {
                    if (line.trim().equals(""))
                    {
                        break;
                    }
                    else 
                    {
                        text = text + line + "\n";       
                    }
                }
                item.setText(text.trim());
                itens.add(item);
            }
            
            return new Srt(itens);
        }
        finally
        {
            try{br.close();}catch (Exception e){}
        }
    }
 
    /**
     * Cria um novo objeto Srt a partir deste com o texto de todos os itens == ".";
     */ 
    public Srt cleanupSubtitleText()
    {
        ArrayList<SrtItem> list = new ArrayList<>();
        for (SrtItem item : itens)
        {
            SrtItem i = new SrtItem();
            i.setBegin(item.getBegin());
            i.setEnd(item.getEnd());
            i.setIndex(item.getIndex());
            i.setText(".");
            list.add(i);
        }
        return new Srt(list);
    }
    
    /**
     * Cria um novo objeto Srt a partir deste com os items de texto = '.' , '',  removidos;
     */ 
    public Srt removeEmptyItens()
    {
        ArrayList<SrtItem> list = new ArrayList<>();
        int index = 1;
        for (SrtItem item : itens)
        {
            String text = item.getText().trim();
            if (text.equals(".") == false && text.length() > 0)
            {
                SrtItem i = new SrtItem();
                i.setBegin(item.getBegin());
                i.setEnd(item.getEnd());
                i.setIndex(index);
                i.setText(item.getText());
                list.add(i);
                index++;
            }
        }
        return new Srt(list);
    }
    
    public ArrayList<SrtItem> getItens()
    {
        return itens;
    }

    public long getBegin()
    {
        if(itens.size() == 0) return -1;
        return itens.get(0).getBegin();
    }
    
    public long getEnd()
    {
        if(itens.size() == 0) return -1;
        return itens.get(itens.size()-1).getEnd();
    }
    
    public void updateSubtitleItem(int index, String text)
    throws Exception
    {
        if(index < 0 || index >= itens.size()) throw new Exception("indice fora do intervalo");
        itens.get(index).setText(checkSubtitleFormat(text));
    }
    
    public void updateAllSubtitleItem(ArrayList<String> nitens)
    throws Exception
    {
        if(itens.size() != nitens.size()) 
            throw new Exception("Numero de legendas nao corresponde ao numero de itens do bloco.");
        
        for (int i = 0; i < nitens.size(); i++)
        {
            String text = (String) nitens.get(i);
            itens.get(i).setText(checkSubtitleFormat(text));
        }
    }
    
    private String checkSubtitleFormat(String subtitleText) 
    throws Exception
    {
        String newText = "";
        BufferedReader br = null;
        
        subtitleText = subtitleText.trim();
        if(subtitleText.equals("")) 
            return ".";
        
        try
        {
            br = new BufferedReader(new StringReader(subtitleText));
            String line;
            int l = 0;
            while ((line = br.readLine()) != null)
            {
                //eliminar linhas em branco
                //se tiver linha em branco no meio do texto da legenda eh erro de formato
                if (line.trim().equals(""))
                {
                    continue;
                }
                else 
                {
                    if(l > 0) 
                        newText = newText+ "\n" + line;
                    else
                        newText = newText + line ;
                    l++;
                }
            }
            return newText;
        }
        finally
        {
            try{br.close();}catch (Exception e){}
        }
    }
     
    public String toString()
    { 
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i < itens.size(); i++)
        {
            SrtItem it = itens.get(i);
            buf.append(it.getIndex());
            buf.append("\n");
            buf.append(miliToString(it.getBegin(),it.getEnd()));
            buf.append("\n");
            buf.append(it.getText());
            buf.append("\n");
            buf.append("\n");
        }
        return buf.toString();
    }

    public String formatToTranslate()
    { 
        StringBuffer buf = new StringBuffer();
        
        for (int i = 0; i < itens.size(); i++)
        {
            SrtItem it = itens.get(i);
            buf.append("(%"+it.getIndex()+")");
            buf.append("\r\n");
            buf.append(it.getText());
            buf.append("\r\n");
        }
        return buf.toString();
    }
    
    private static String miliToString(long begin, long end)
    {
        return miliToString(begin) + " --> " + miliToString(end); 
    }
    

    private static String miliToString(long time)
    {
        long ms = time % 1000;
        long s = time / 1000;
        long m = s / 60;
        s = s % 60;
        long h = m / 60;
        m = m % 60;
        
        return format2digit(h)+":"+format2digit(m)+":"+format2digit(s)+","+format3digit(ms);
    }
    
    private static String format2digit(long number)
    {
        String t = "00"+number;
        return t.substring(t.length()-2, t.length());
    }
    
    private static String format3digit(long number)
    {
        String t = "000"+number;
        return t.substring(t.length()-3, t.length());
    }
    
    public Srt[]  toBlocks(int sizeInMinutes)
    throws Exception
    {
        ArrayList<Srt> result = new ArrayList<>();
        
        int size = itens.size(); 
        if (size == 0) return new Srt[0];

        long duration = itens.get(size - 1).getEnd();
        int numBlocks = (int)  (duration / (sizeInMinutes*60*1000)  + 1);
        int blockItens = (int) (size / numBlocks); // o ultimo ter� mais itens.

        for (int i = 0; i < numBlocks; i++)
        {
            int pi = i*blockItens; 
            int pf = pi + blockItens -1;
            if (i == numBlocks-1)  pf = size-1;
            
            ArrayList<SrtItem> block = new ArrayList<>();
            for (int j = pi; j <= pf; j++)
            {
                block.add(itens.get(j));
            }
            result.add(new Srt(block));
        }
        return (Srt[])result.toArray(new Srt[0]);
    }   
    
    public Srt replaceTranslatedText(ArrayList<String> translatedText) 
    throws Exception
    {
        Srt nsrt = new Srt();
        
        if(translatedText.size() != itens.size())
        {
            throw new Exception("Tamanho do texto traduzido nao bate com o arquivo .srt");
        }
        
        for (int i = 0; i < translatedText.size(); i++)
        {
            SrtItem it = itens.get(i).copy();
            nsrt.itens.add(it);
            it.setText(translatedText.get(i));
        }
        
        return nsrt;
    }   
    
    /**
     * eliminar intervalos entre itens de legenda.
     */
    public Srt joinTimeBorders()
    {
        ArrayList<SrtItem> list = new ArrayList<>();
        for (SrtItem item : itens)
        {
            SrtItem i = new SrtItem();
            i.setBegin(item.getBegin());
            i.setEnd(item.getEnd());
            i.setIndex(item.getIndex());
            i.setText(item.getText());
            list.add(i);
        }
        for (int i = 0; i < list.size()-1; i++)
        {
            SrtItem it1 = (SrtItem) list.get(i);
            SrtItem it2 = (SrtItem) list.get(i+1);
            long newTime = (it1.getEnd() + it2.getBegin())/2;
            it1.setEnd(newTime);
            it2.setBegin(newTime);
        }
        return new Srt(list);
    }
    
    /**
     * quebra todos os itens com mais de 'timeLimit' segundos em subitens de 'newTimeItens' seguntos.
     */
    public Srt breakWideItens(int timeLimit, int newTimeItens)
    {
        ArrayList<SrtItem> list = new ArrayList<>();
        int index = 1;
        for (SrtItem item : itens)
        {
            long time = item.getEnd() - item.getBegin();
            
            if (time/1000 >= timeLimit)
            {
                SrtItem[] subitens = breakItem(item,newTimeItens);
                for (int j = 0; j < subitens.length; j++)
                {
                    SrtItem i = new SrtItem();
                    i.setBegin(subitens[j].getBegin());
                    i.setEnd(subitens[j].getEnd());
                    i.setIndex(index);
                    if (j==0) i.setText(item.getText()); // o texto n�o � dividido.
                    else      i.setText(".");
                    list.add(i);
                    index++;
                }
            }
            else
            {
                SrtItem i = new SrtItem();
                i.setBegin(item.getBegin());
                i.setEnd(item.getEnd());
                i.setIndex(index);
                i.setText(item.getText());
                list.add(i);
                index++;
            }
        }
        for (int i = 0; i < list.size()-1; i++)
        {
            SrtItem it1 = (SrtItem) list.get(i);
            SrtItem it2 = (SrtItem) list.get(i+1);
            long newTime = (it1.getEnd() + it2.getBegin())/2;
            it1.setEnd(newTime);
            it2.setBegin(newTime);
        }
        return new Srt(list);
    }   
    
    //newTimeItens em segundos
    private SrtItem[] breakItem(SrtItem item, int newTimeItens)
    {
        //tem que ter pelo menos 2 subitens para gerar, sen�o retorna o proprio item.
        
        long numItens = (item.getEnd() - item.getBegin()) / (newTimeItens*1000);
        if (numItens < 2)
        {
            SrtItem[] resp = new SrtItem[1];
            resp[0] = item;
            return resp;
        }
        
        long timeToAdd = (item.getEnd() - item.getBegin()) % (newTimeItens*1000);
        timeToAdd = timeToAdd / numItens;
        
        SrtItem[] resp = new SrtItem[(int)numItens];
        for (int i = 0; i < resp.length; i++)
        {
            if (i == 0)
            {
                resp[i] = new SrtItem();
                resp[i].setBegin(item.getBegin());
                resp[i].setEnd(item.getBegin()+(newTimeItens*1000)+timeToAdd);
                resp[i].setText(item.getText());
            }
            else if (i == resp.length -1)
            {
                resp[i] = new SrtItem();
                resp[i].setBegin(resp[i-1].getEnd());
                resp[i].setEnd(item.getEnd());
                resp[i].setText(".");
            }
            else
            {
                resp[i] = new SrtItem();
                resp[i].setBegin(resp[i-1].getEnd());
                resp[i].setEnd(resp[i-1].getEnd()+(newTimeItens*1000)+timeToAdd);
                resp[i].setText(".");
            }
        }
        return resp;
    }

    public static void main1(String[] args)
    {
        File f = new File("F:\\Caio Fabio - projetos\\Subtitles\\Wd0iYodvXgM.srt");//ZHLJSplQi_c.srt");
        
        if (f.exists() == false )
        {
            System.err.println("Arquivo naoo exite. ");
            System.exit(1);
        }

        try
        {
            Srt srt =readSrtFile(f,"UTF8");
            System.out.println(srt.toString());
            Srt[] blocks = srt.toBlocks(5);
            
            for (int i = 0; i < blocks.length; i++)
            {
                System.out.println("\n\n############################# BLOCO "+i+"############################");
               
                System.out.println(blocks[i].toString());
            }
            
            
            System.out.println("\nBlocos ["+blocks.length+"] Size["+blocks[0].getItens().size()+"] Last ["+blocks[blocks.length-1].getItens().size()+"]");
            System.out.println("FIM.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(4);
        }
        finally
        {
        }
    } 
    
    public static void main(String[] args)
    {
        try
        {
            VideoWrapDao.setTestMode();
            VideoWrap vw = VideoWrapDao.get(6, Block.Locale.PT );
            String subtitleText = vw.getBlocks().get(0).getSubtitleOri();
            Srt srt = new Srt(subtitleText);            
            
            
            ArrayList<SrtItem> itens = srt.getItens();
            for (int k = 0; k <itens.size(); k++)
            {
                SrtItem it = itens.get(k);
                System.out.println("############ ITEM "+ k + "["+it.getStrBegin()+"]["+it.getStrEnd()+"]:"+ it.getText());
            }            
            
            System.out.println("FIM.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(4);
        }
        finally
        {
        }
    }
    
}
