package cms.cf.controller;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cms.cf.conf.CleanUp;
import cms.cf.controller.exceptions.BadRequestException;
import cms.cf.controller.exceptions.InternalErrorException;
import cms.cf.subtitles.dao.DaoException;
import cms.cf.subtitles.dao.NotFoundException;
import cms.cf.subtitles.dao.impl.BlockDao;
import cms.cf.subtitles.dao.impl.VideoDao;
import cms.cf.subtitles.dao.vo.Block;
import cms.cf.subtitles.dao.vo.Block.Locale;
import cms.cf.subtitles.dao.vo.Video;
import cms.cf.subtitles.srt.Srt;
import cms.cf.subtitles.srt.SrtItem;
import cms.cf.util.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SubtitlesWorkerControler 
{
    private Logger logger = Logger.getLogger(this.getClass());
    public SubtitlesWorkerControler() 
    {
    }
    
    @RequestMapping(value={"/legendas/worker/participate"}, method=RequestMethod.POST)
    public String participate(Model model, HttpServletRequest request) 
    {
        String email = request.getUserPrincipal().getName();
        
        String subtitling_pt = request.getParameter("subtitling-pt");
//        String subtitling_eng = request.getParameter("subtitling-eng");

        Locale locale = Locale.EN;
        if (subtitling_pt != null)
        {
            locale = Locale.PT;
        }
        
        Block allocated = null;
        try
        {
            //verificar se o usuario possui bloco alocado
            int idBlock = BlockDao.hasAllocatedBlock(email);
           
            if (idBlock > 0)
            {
                //enviar mensagem de erro
                return "redirect:/s/legendas?msg_alocado=1";
            }
            
            //alocar um bloco
            ArrayList<Block> blocks = BlockDao.listAvailableBlocks(locale);
            for (int i = 0; i < blocks.size(); i++)
            {
                try
                {
                    BlockDao.allocateBlock(blocks.get(i).getId(), email);
                    allocated = blocks.get(i);
                    allocated.setAllocateTime(new Date());
                    break; // ja alocou.
                }
                catch (NotFoundException e)
                {
                    //ja alocado. tentar outro
                }
            }            
        }
        catch (Exception e)
        {
            throw new InternalErrorException(e);
        }
        
        if (allocated == null)
        {
            //retornar aa home com mensagem de falha, e tentar novamente
            if (locale == Locale.EN)
                return "redirect:/s/legendas?no_block_en=1";
            else
                return "redirect:/s/legendas?no_block_pt=1";
        }
        
        try
        {
            //Video v = VideoDao.get(allocated.getIdVideo());
            //prepareModel(model, allocated, v);
            //return "tool";
            return "redirect:/s/legendas/worker/alocatedBlock?block="+allocated.getId();
        }
        catch (Exception e)
        {
            throw new InternalErrorException(e);
        }
    }
    
    /**
     * recuperar bloco j� alocado
     */
    @RequestMapping(value={"/legendas/worker/alocatedBlock"}, method=RequestMethod.GET)
    public String alocatedBlock(Model model, HttpServletRequest request) 
    {
        String email = request.getUserPrincipal().getName();

        String toedit = request.getParameter("toedit");
        String idBlock = request.getParameter("block");
        
        if (idBlock == null)
        {
            logger.warn("parametro block null ao tentar mostrar bloco alocado do usuario");
            throw new BadRequestException();
        }
        int intIdblock;
        try
        {
            intIdblock = Integer.parseInt(idBlock);
        }
        catch (Exception e)
        {
            logger.warn("parametro block invalido ["+idBlock+"] ao tentar mostrar bloco alocado do usuario");
            throw new BadRequestException();
        }
        
        Block block = null;
        try
        {
            //verificar se o usuario possui bloco alocado
            block = BlockDao.get(intIdblock);
        }
        catch (NotFoundException e)
        {
            logger.warn("block nao encontrado ["+idBlock+"] ao tentar mostrar bloco alocado do usuario");
            throw new BadRequestException();
        }
        catch (Exception e)
        {
            logger.error("Erro ao tentar mostrar bloco alocado do usuario",e);
            throw new InternalErrorException(e);
        }
        
        //a expiracao por tempo nao deveria ser tratada aqui, mas em um controle geral separado.
        if (block.getStatus() == Block.Status.FINISHED  &&  
                (block.getEmail() == null || block.getEmail().equals(email) == false) )
        {
            logger.warn("Tentativa INVALIDA de mostrar bloco alocado NAO alocado ["+block.getEmail()+"]");
            throw new BadRequestException();
        }
        else if (block.getStatus() != Block.Status.FINISHED && 
                 (block.getAllocateTime() == null || block.getEmail() == null ||
                  block.getEmail().equals(email) == false )     )
        {
            logger.warn("Tentativa INVALIDA de mostrar bloco alocado NAO alocado ["+block.getEmail()+"]");
            throw new BadRequestException();
        }
        
        try
        {
            Video v = VideoDao.get(block.getIdVideo());
            prepareModel(model, block, v);

            if (toedit != null)        
                model.addAttribute("maxAllocationTime", -1);
            else
                model.addAttribute("maxAllocationTime", CleanUp.MAX_ALLOCATION_TIME);
            return "tool";
        }
        catch (Exception e)
        {
            throw new InternalErrorException(e);
        }        
    }
    
    private void prepareModel(Model model, Block block, Video v) 
    throws Exception 
    {
        Srt srt = new Srt(block.getSubtitleUpdate());
        
        model.addAttribute("startTime", block.getAllocateTime() == null?System.currentTimeMillis():block.getAllocateTime().getTime());
        model.addAttribute("videoid", v.getIdYoutube());
        model.addAttribute("blockid", block.getId());
        model.addAttribute("videoTitle", v.getTitle());
        model.addAttribute("subtitles", srt.getItens());
        model.addAttribute("locale", block.getLocale().toString());
        
        StringBuffer subtitlesBegin = new StringBuffer("[");
        StringBuffer subtitlesEnd = new StringBuffer("[");
        
        ArrayList<SrtItem> itens = srt.getItens();
        for (int i = 0; i < itens.size(); i++)
        {
            subtitlesBegin.append(itens.get(i).getBegin());
            subtitlesEnd.append(itens.get(i).getEnd());
            if (i == itens.size() -1)
            {
                subtitlesBegin.append(" ]");
                subtitlesEnd.append(" ]");
            }
            else
            {
                subtitlesBegin.append(", ");
                subtitlesEnd.append(", ");
            }
        }
        model.addAttribute("subtitlesBegin", subtitlesBegin.toString());
        model.addAttribute("subtitlesEnd", subtitlesEnd.toString());
    }
    
    @RequestMapping(value={"/legendas/worker/tool-finalize"}, method=RequestMethod.POST)
    public String toolFinalize(Model model, HttpServletRequest request) 
    {
        String sBlockid = request.getParameter("blockid");
        if (sBlockid == null)
        {
            logger.warn("Parametro 'blockid' n�o encontrado.");
            throw new BadRequestException();
        }
        int blockid = -1;
        try
        {
            blockid = Integer.parseInt(sBlockid);
        }
        catch (Exception e)
        {
            logger.warn("Parametro 'blockid' n�o � inteiro ["+sBlockid+"]");
            throw new BadRequestException();
        }
        Block block;
        try
        {
            block = BlockDao.get(blockid);
        }
        catch (NotFoundException e)
        {
            logger.warn("Bloco inesistente ["+sBlockid+"]");
            throw new BadRequestException();
        }
        catch (DaoException e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
        String email = request.getUserPrincipal().getName();
        if (email.equals(block.getEmail()) == false)
        {
            logger.warn("Bloco ["+sBlockid+"] n�o pertence ao usuario locado ["+email+"]");
            throw new BadRequestException();
        }
        
        String quit = request.getParameter("quit");
        try
        {
            if (quit != null)
            {
                BlockDao.cleanUpBlock(blockid);
                return "redirect:/s/legendas?msg_quit=1";
            }
        }
        catch (Exception e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
        
        String subtitle = request.getParameter("subtitle");
        if (subtitle == null)
        {
            logger.warn("Parametro 'subtitle' n�o encontrado.");
            throw new BadRequestException();
        }
        
        try
        {
            //System.out.println(subtitle);
            
            ArrayList<String> itens = new ArrayList<>();
            
            JsonReader rdr = Json.createReader(new StringReader(subtitle));
            JsonArray results = rdr.readArray();
            for (JsonValue result : results) 
            {
                String tmp = result.toString(); // possui aspas (") no inicio e fim
                tmp = tmp.substring(1, tmp.length()-1); //removendo as aspoas
                tmp = JSONUtil.unescape(tmp);
                itens.add(tmp);
            } 
            
            Srt srt = new Srt(block.getSubtitleUpdate());
            srt.updateAllSubtitleItem(itens);   
            
            BlockDao.updateSubtitle(blockid, srt.toString(), email);
            if (block.getStatus() != Block.Status.FINISHED)
            {    
                BlockDao.finalizeBlock(blockid);
            }
            return "redirect:/s/legendas?msg_finalized=1";
        }
        catch (Exception e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
    }
    
    @RequestMapping(value={"/legendas/worker/saveBlockItem"}, method=RequestMethod.POST)
    public String saveBlockItem(Model model, HttpServletRequest request) 
    {
        String sBlockid = request.getParameter("blockid");
        if (sBlockid == null)
        {
            logger.warn("Parametro 'blockid' n�o encontrado.");
            throw new BadRequestException();
        }
        int blockid = -1;
        try
        {
            blockid = Integer.parseInt(sBlockid);
        }
        catch (Exception e)
        {
            logger.warn("Parametro 'blockid' n�o � inteiro ["+sBlockid+"]");
            throw new BadRequestException();
        }
        Block block;
        try
        {
            block = BlockDao.get(blockid);
        }
        catch (NotFoundException e)
        {
            logger.warn("Bloco inesistente ["+sBlockid+"]");
            throw new BadRequestException();
        }
        catch (DaoException e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
        
        String email = request.getUserPrincipal().getName();
        if (email.equals(block.getEmail()) == false)
        {
            logger.warn("Bloco ["+sBlockid+"] n�o pertence ao usuario locado ["+email+"]");
            throw new BadRequestException();
        }
        
        String sIndex = request.getParameter("index");
        if (sIndex == null)
        {
            logger.warn("Parametro 'index' n�o encontrado.");
            throw new BadRequestException();
        }
        int index = -1;
        try
        {
            index = Integer.parseInt(sIndex);
        }
        catch (Exception e)
        {
            logger.warn("Parametro 'index' n�o � inteiro ["+index+"]");
            throw new BadRequestException();
        }
        
        String text = request.getParameter("text");
        if (text == null)
        {
            logger.warn("Parametro 'text' n�o encontrado.");
            throw new BadRequestException();
        }
        
        try
        {
            Srt srt = new Srt(block.getSubtitleUpdate());
            srt.updateSubtitleItem(index,text);   
            
            BlockDao.updateSubtitle(blockid, srt.toString(), email);
            return "json_ok";
        }
        catch (Exception e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
    }
    
    /**
     * recebe como parametro o id de um bloco em ingles
     * e recupera o bloco equivalente em portuques.
     */
    @RequestMapping(value={"/legendas/worker/pt-block"}, method=RequestMethod.GET)
    public String ptBlock(Model model, HttpServletRequest request) 
    {
        String sBlockid = request.getParameter("blockid");
        if (sBlockid == null)
        {
            logger.warn("Parametro 'blockid' n�o encontrado.");
            throw new BadRequestException();
        }
        int blockid = -1;
        try
        {
            blockid = Integer.parseInt(sBlockid);
        }
        catch (Exception e)
        {
            logger.warn("Parametro 'blockid' n�o � inteiro ["+sBlockid+"]");
            throw new BadRequestException();
        }
        Block block;
        try
        {
            block = BlockDao.getPtBlock(blockid);
        }
        catch (NotFoundException e)
        {
            logger.warn("Bloco inesistente ["+blockid+"]");
            throw new BadRequestException();
        }
        catch (DaoException e)
        {
            logger.warn("Erro interno.",e);
            throw new InternalErrorException(e);
        }
        
        String srt = block.getSubtitleUpdate();
        model.addAttribute("text", srt);
        return "text";
    }    
    
}
