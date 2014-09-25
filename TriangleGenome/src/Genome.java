import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Genome
{
  public static final int NUM_GENES=200;
  public final int IMG_WIDTH;
  public final int IMG_HEIGHT;
  public ArrayList<Gene> geneList=new ArrayList<>();

  public Genome(int width,int height)
  {
    IMG_WIDTH=width;
    IMG_HEIGHT=height;
    for(int i=0;i<NUM_GENES;i++)
    {
      Gene myGene=new Gene();
      geneList.add(myGene);
    }
  }

}
