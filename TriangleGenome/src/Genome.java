import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Genome implements Comparable<Genome>
{
  public static final int NUM_GENES=200;
  public long fitscore;
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
  
  public Genome(BufferedImage image)
  {
    IMG_WIDTH=image.getTileWidth();
    IMG_HEIGHT=image.getHeight();
    for(int i=0;i<NUM_GENES;i++)
    {
      Gene myGene=new Gene();
      geneList.add(myGene);
    }
  }

public int compareTo(Genome genome) {
	long fitDiff=fitscore-genome.fitscore;
	if(fitDiff<0l)return -1;
	else if(fitDiff==0l) return 0;
	else return 1;
	
}

//@Override
//public int compareTo(Object o) {
//	// TODO Auto-generated method stub
//	return 0;
//}

  
}
