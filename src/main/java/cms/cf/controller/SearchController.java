package cms.cf.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchController 
{
    public SearchController() 
    {
    }
  
    @RequestMapping(value={"/busca"}, method=RequestMethod.GET)
    public String showHomePage(Map<String, Object> model) 
    {
        return "search"; 
    }
    
    /**
     * Mostra a pagina personalizada de exibicao do video escolhido, com posicionamento dos
     * "matches"
     */
    @RequestMapping(value={"/video/{source}/{videoid}/{matches}"}, method=RequestMethod.GET)
    public String showYouTubeVideo(@PathVariable String source, @PathVariable String videoid, 
                                   @PathVariable String matches, Map<String, Object> model) 
    {
        //System.out.println(matches);
        if (matches.startsWith("favicon")) throw new RuntimeException("favicon....");
        
        //http://localhost:8080/CaioFabioSearch/s/video/youtube/Wd0iYodvXgM/1,11,13,15,20,30,50,80,120,150,200,250,3000
        model.put("matches", "["+matches+"]");
        //model.put("videoid", "Wd0iYodvXgM"); //nao precisa
        
        if (source.trim().equals("youtube")) 
            return "player_youtube";
        else if (source.trim().equals("vimeo")) 
            return "player_vimeo";
        else return "error";          
    }

    /**
     * Exibe a pagina para o item de menu '*Seleção!'
     */
    @RequestMapping(value={"/sel"}, method=RequestMethod.GET)
    public String showSelected(Map<String, Object> model) 
    {
        return "selected"; 
    }  
}
