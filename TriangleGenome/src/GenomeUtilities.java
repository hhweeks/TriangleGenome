import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

/****************************************************************************
 *GenomeUtilities
 *@author Hans Weeks
 *@author Paige Romero
 *This static object contains methods useful for manipulating Genomes and Genes
 *which we choose not to add to the Genome or Gene class as to make those objects
 *as lite as possible. 
 ****************************************************************************/
public class GenomeUtilities
{
  public static final int NPOINTS=3;// points of a triangle
  public static final int NCOLORS=4;// r,g,b,a

  /****************************************************************************
   * setRandomGenome
   * Input:Genome to randomize
   * Output:none
   * Description:when a Genome is initialized, it has 200 genes with values of 0.
   *  This method initialized random values for each gene. TODO currently totally random,
   *  no assurance of covering the background.
   ****************************************************************************/
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

  /****************************************************************************
   * drawNTriangles
   * Input:int N (0-200), imagePanel where triangles are being drawn, and Genome being drawn
   * Output:none
   * Description:draws triangles in layers 0-200. Is called by the slider on the GUI
   *  to allow us to see the triangles that may be entirely covered up otherwise. TODO correct?
   ****************************************************************************/
  public static void drawNTriangles(int N,
	      TriangleGenomeGUI.ImagePanel myPic, Genome myGenome) {
	    BufferedImage myIm = myPic.getImage();
	    Graphics myGraphics = myIm.getGraphics();
	    int height = TriangleGenomeGUI.imageWindow.image.getHeight();
	    int width = TriangleGenomeGUI.imageWindow.image.getWidth();
	    //clear previous geneome from display
	    ////////////////////
	    int y = myIm.getHeight();
	    int x = myIm.getWidth();
	    myGraphics.setColor(new Color(238, 238, 238));
	    myGraphics.fillRect(0, 0, x, y);
	    ////////////////////

	    myGraphics.setColor(Color.white);
	    myGraphics.fillRect(0, 0, width, height);
	    for (int i = 0; i < N; i++) {
	      myGraphics.setColor(new Color(myGenome.geneList.get(i).r,
	          myGenome.geneList.get(i).g, myGenome.geneList.get(i).b,
	          myGenome.geneList.get(i).a));
	      myGraphics.fillPolygon(myGenome.geneList.get(i));
	    }
	    myPic.repaint();
	  }

  /****************************************************************************
   * getBufferedImage
   * Input:Genome
   * Output:Buffered Image of that Genome
   * Description:draws the Genome as a BufferedImage
   ****************************************************************************/
  public static BufferedImage getBufferedImage(Genome myGenome)
  {
    BufferedImage myIm=new BufferedImage(myGenome.IMG_WIDTH,
        myGenome.IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
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

  /****************************************************************************
   * genomeEqual
   * Input:two Genomes to compare
   * Output:boolean
   * Description:compares each of 200 genes in a Genome by calling geneEquals
   *  on them.
   ****************************************************************************/
  public static boolean genomeEqual(Genome oneGenome,Genome twoGenome)
  {

    for(int i=0;i<oneGenome.geneList.size();i++)
    {
      if(!geneEqual(oneGenome.geneList.get(i), twoGenome.geneList.get(i)))
        return false;
    }
    return true;
  }

  /****************************************************************************
   * geneEqual
   * Input:Genes to compare
   * Output:boolean
   * Description:compares 10 alleles (base-pairs?) in a gene
   ****************************************************************************/
  public static boolean geneEqual(Gene gene1,Gene gene2)
  {

    return (gene1.xpoints[0]==gene2.xpoints[0]
        &&gene1.xpoints[1]==gene2.xpoints[1]
        &&gene1.ypoints[0]==gene2.ypoints[0]
        &&gene1.ypoints[1]==gene2.ypoints[1]&&gene1.r==gene2.r
        &&gene1.g==gene2.g&&gene1.b==gene2.b&&gene1.a==gene2.a);
  }

  /****************************************************************************
   * genomeCopy
   * Input:Genome
   * Output:new Genome with identical genes
   * Description:deep copy of genome, calls gene copy
   ****************************************************************************/
  public static Genome genomeCopy(Genome myGenome)
  {

    Genome genomeOut=new Genome(myGenome.IMG_WIDTH, myGenome.IMG_HEIGHT);
    genomeOut.geneList.clear();
    for(int i=0;i<myGenome.geneList.size();i++)
    {

      genomeOut.geneList.add(geneCopy(myGenome.geneList.get(i)));

    }
    return genomeOut;
  }

  /****************************************************************************
   * geneCopy
   * Input:Gene
   * Output:new Gene with same alleles (base-pairs)
   * Description:deep copy of gene
   ****************************************************************************/
  public static Gene geneCopy(Gene myGene)
  {
    Gene geneOut=new Gene();
    geneOut.xpoints[0]=myGene.xpoints[0];
    geneOut.ypoints[0]=myGene.ypoints[0];
    geneOut.xpoints[1]=myGene.xpoints[1];
    geneOut.ypoints[1]=myGene.ypoints[1];
    geneOut.xpoints[2]=myGene.xpoints[2];
    geneOut.ypoints[2]=myGene.ypoints[2];
    geneOut.r=myGene.r;
    geneOut.g=myGene.g;
    geneOut.b=myGene.b;
    geneOut.a=myGene.a;
    geneOut.npoints=myGene.npoints;

    return geneOut;
  }

  /****************************************************************************
   * hammingDistance
   * Input:
   * Output:
   * Description:TODO redundant, hamming distance now its own class
   ****************************************************************************/
  public static int hammingDistance(Genome genome1,Genome genome2)
  {
    int hScore=0;

    for(int i=0;i<genome1.geneList.size();i++)
    {

      hScore+=hammingGeneHelper(genome1.geneList.get(i),
          genome2.geneList.get(i));

    }
    return hScore;

  }

  /****************************************************************************
   * hammingDistanceHeler
   * Input:
   * Output:
   * Description:TODO redundant, hamming distance now its own class
   ****************************************************************************/
  private static int hammingGeneHelper(Gene gene1,Gene gene2)
  {
    int count=0;
    if(gene1.xpoints[0]!=gene2.xpoints[0])
      count++;
    if(gene1.xpoints[1]!=gene2.xpoints[1])
      count++;
    if(gene1.xpoints[2]!=gene2.xpoints[2])
      count++;
    if(gene1.ypoints[0]!=gene2.ypoints[0])
      count++;
    if(gene1.ypoints[1]!=gene2.ypoints[1])
      count++;
    if(gene1.ypoints[2]!=gene2.ypoints[2])
      count++;
    if(gene1.r!=gene2.r)
      count++;
    if(gene1.g!=gene2.g)
      count++;
    if(gene1.b!=gene2.b)
      count++;
    if(gene1.a!=gene2.a)
      count++;
    return count;
  }
}
