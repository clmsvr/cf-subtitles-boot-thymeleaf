package cms.cf.subtitles.srt.outros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cms.cf.subtitles.dao.impl.VideoWrapDao;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.srt.Srt;

public class SrtDeployFormatedEng
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
            System.err.println("Arqumentos invalidos. Formato Correto:\n        java cms.cf.subtitles.srt.SrtDeployEng 'path to SRT file'");
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

        File engf = new File(file.getParentFile(), file.getName().replace(".srt", ".eng"));
        if (engf.exists() == false)
        {
            System.err.println("Arquivo de legenda em ingl�s, '.eng', n�o encontrado.");
            System.exit(1);
        }

        Srt srt = null;
        try 
        {   
            srt = Srt.readSrtFile(file, "UTF-8");
        }
        catch (Exception e)
        {
           e.printStackTrace();
           System.exit(1);
        }  

        
        try 
        {   
            ArrayList<String> translatedText = readTranslatedFile(engf, "UTF-8");
            //for (int i = 0; i < list.size(); i++){ System.out.println("(%"+(i+1)+")\n"+list.get(i)); }    
            
            Srt translatedSrt = srt.replaceTranslatedText(translatedText);
            //System.out.println(srt);

            Srt[] translatedBlocks = translatedSrt.toBlocks(5);
            
            log("\nID Youtube ["+idYoutube+"] Blocos ["+translatedBlocks.length+"] Size["+translatedBlocks[0].getItens().size()+"] Last ["+translatedBlocks[translatedBlocks.length-1].getItens().size()+"]");

            VideoWrapDao.setTestMode();  //obter conexoes locais via DriverManager          
            VideoWrapDao.insertBlocks(idYoutube, translatedBlocks, Locale.EN);
        }
        catch (Exception e)
        {
           e.printStackTrace();
           System.exit(1);
        }  
        
        System.out.println("OK. Video registrado no banco de dados.");
    }
    
    public static ArrayList<String> readTranslatedFile(File file, String encode) //ex: "UTF8"
    throws Exception
    {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
            
            int legenda = 1;
            int count = 0;
            
            StringBuffer texto = new StringBuffer();
            
            String line;
            while ((line = br.readLine()) != null)
            {
                count++;
                if(line.startsWith("(%") ) 
                {
                    int num = -1;
                    try
                    {
                        //if (pos != 1)Integer.parseInt(line); //pular primeira linha por causa de caracteres especiais ocultos.
                        line = line.trim();
                        num = Integer.parseInt(line.substring(2,line.length()-1));
                    }
                    catch (NumberFormatException e)
                    {
                        throw new NumberFormatException("Arquivo com formato invalido. Numero de sequencia esperado na linha "+ count+ ". Detail: "+e.toString());
                    }
                    //estamos na legenda de numero 'legenda' 
                    if (num != legenda) 
                    {
                        throw new Exception("Erro de formato na linha ["+count+"]. esperado a legenda de numero ["+legenda+"]");
                    }   
                    
                    if (count != 1)
                    {
                        //adicionar texto da legenda anterior acumulada no buffer ao arraylist.
                        list.add(texto.toString());
                        texto = new StringBuffer();
                    }
                    legenda++;
                }
                else if (count == 1)//se for a primeira linha tem de ser um numero de legenda
                {
                    throw new Exception("Erro de formato na linha ["+count+"]. esperado a legenda de numero ["+legenda+"]");
                }
                else if (texto.length() == 0)
                {
                    texto.append(line);
                }   
                else
                {
                    texto.append("\n"+line);
                }
            }
            
            list.add(texto.toString());
            return list;
        }
        finally
        {
            try{br.close();}catch (Exception e){}
        }
    }
}
