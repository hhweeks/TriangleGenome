import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;


public class Genome
{
  public static final int NUM_GENES=200;
  public static final int NPOINTS=3;//triangle
  public static final int NCOLORS=4;//r,g,b,a
  public ArrayList<Gene> geneList=new ArrayList<>();
  
  
  
  public Genome(){
	  for(int i=0;i<NUM_GENES;i++)
	    {
	     
	      Gene myGene=new Gene(); 
	      geneList.add(myGene);
	    }
	  }


}
