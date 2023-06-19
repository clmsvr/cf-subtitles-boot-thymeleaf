package cms.cf.subtitles.dao.vo;

import java.util.ArrayList;

public class VideoWrap
{
    private Video video;
    private ArrayList<Block> blocks;
    
    public VideoWrap(Video video, ArrayList<Block> blocks)
    {
        this.video = video;
        this.blocks = blocks;
    }

    public Video getVideo()
    {
        return video;
    }

    public void setVideo(Video video)
    {
        this.video = video;
    }

    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }
    
    
    
}
