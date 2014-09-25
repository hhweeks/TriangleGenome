import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;


public class Genome
{
  public static final int NUM_GENES=200;
  public static final int NPOINTS=3;//triangle
  public static final int NCOLORS=4;//r,g,b,a
  
  private Random rand=new Random();
  public ArrayList<Gene> geneList=new ArrayList<>();
  
  public Genome(){
	  for(int i=0;i<NUM_GENES;i++)
	    {
	     
	      Gene myGene=new Gene(); 
	      geneList.add(myGene);
	    }
	  }
	  
  
  
  
  
  public Genome(int height,int width)
  {
    for(int i=0;i<NUM_GENES;i++)
    {
      Point[] myVertices= new Point[NPOINTS];
      for(int j=0;j<NPOINTS;j++)
      {
        myVertices[j]=new Point(0,0);
        myVertices[j].x=rand.nextInt(width);
        myVertices[j].y=rand.nextInt(height);
      }
      
      int[] myColors=new int[NCOLORS];
      
      for(int j=0;j<NCOLORS;j++)
      {
        myColors[j]=rand.nextInt(255);
      }
      
      Gene myGene=new Gene(myVertices,myColors); 
      geneList.add(myGene);
    }
  }
  
  
  
  
  
  
  public void setRandomGenome(int height,int width){
	  for(Gene myGene: geneList)
	    {
	      Point[] myVertices= new Point[NPOINTS];
	      for(int j=0;j<NPOINTS;j++)
	      {
	        myVertices[j]=new Point(0,0);
	        myVertices[j].x=rand.nextInt(width);
	        myVertices[j].y=rand.nextInt(height);
	      }
	      
	      int[] myColors=new int[NCOLORS];
	      
	      for(int j=0;j<NCOLORS;j++)
	      {
	        myColors[j]=rand.nextInt(255);
	      }
	      
	      myGene.r=myColors[0];
	      myGene.g=myColors[1];
	      myGene.b=myColors[2];
	      myGene.a=myColors[3];
	      myGene.setPoints(myVertices);
	      myGene.print();
	    }
	  
	  	
	  
	  
	  }
	  
	  
	  
  
  
  
  
  
  


}
