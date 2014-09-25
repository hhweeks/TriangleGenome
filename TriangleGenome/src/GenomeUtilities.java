import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

//support utilities for Genome creation, manipulation, and drawing.

public class GenomeUtilities
{
  public static final int NPOINTS=3;// triangle
  public static final int NCOLORS=4;// r,g,b,a

  // generates a completely random genome of 200 genes
  public static void setRandomGenome(Genome genome)
  {
    Random rand=new Random();
    for(Gene myGene:genome.geneList)
    {
      Point[] myVertices=new Point[NPOINTS];
      for(int j=0;j<NPOINTS;j++)
      {
        myVertices[j]=new Point(0, 0);
        myVertices[j].x=rand.nextInt(genome.IMG_WIDTH);
        myVertices[j].y=rand.nextInt(genome.IMG_HEIGHT);
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
    }
  }

  // draw 0-N triangles/genes on myPic
  public static void drawNTriangles(int N,TriangleGenomeGUI.ImagePanel myPic,
      Genome myGenome)
  {
    BufferedImage myIm=myPic.getImage();
    Graphics myGraphics=myIm.getGraphics();
    int height=myIm.getHeight();
    int width=myIm.getWidth();

    myGraphics.setColor(Color.white);
    myGraphics.fillRect(0, 0, width, height);
    for(int i=0;i<N;i++)
    {
      myGraphics.setColor(new Color(myGenome.geneList.get(i).r,
          myGenome.geneList.get(i).g, myGenome.geneList.get(i).b,
          myGenome.geneList.get(i).a));
      myGraphics.fillPolygon(myGenome.geneList.get(i));
    }
    myPic.repaint();
  }
  
  
  public static BufferedImage getBufferedImage(Genome myGenome)
	  {
	    BufferedImage myIm=new BufferedImage(myGenome.IMG_WIDTH, myGenome.IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
	    Graphics myGraphics=myIm.getGraphics();
	    int height=myIm.getHeight();
	    int width=myIm.getWidth();

	    myGraphics.setColor(Color.white);
	    myGraphics.fillRect(0, 0, width, height);
	    for(int i=0;i<myGenome.NUM_GENES;i++)
	    {
	      myGraphics.setColor(new Color(myGenome.geneList.get(i).r,
	          myGenome.geneList.get(i).g, myGenome.geneList.get(i).b,
	          myGenome.geneList.get(i).a));
	      myGraphics.fillPolygon(myGenome.geneList.get(i));
	    }
	    return myIm;
	  }
  
  
  
  

  public static Genome genomeCopy(Genome myGenome)
  {

    Genome genomeOut=new Genome(myGenome.IMG_WIDTH, myGenome.IMG_HEIGHT);

    for(int i=0;i<myGenome.geneList.size();i++)
    {

      genomeOut.geneList.set(i, geneCopy(myGenome.geneList.get(i)));

    }
    return genomeOut;
  }

  public static Gene geneCopy(Gene myGene)
  {
    Gene geneOut=new Gene();
    geneOut.xpoints=myGene.xpoints;
    geneOut.ypoints=myGene.ypoints;
    geneOut.r=myGene.r;
    geneOut.g=myGene.g;
    geneOut.b=myGene.b;
    geneOut.a=myGene.a;
    geneOut.npoints=myGene.npoints;

    return geneOut;

  }

}
