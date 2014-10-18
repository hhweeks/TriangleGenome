import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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
    
    //averagingGenome(genome,new BufferedImage(90, 90, BufferedImage.TYPE_INT_RGB));
  }
  
  public static void  averagingGenome(Genome genome,BufferedImage masterImage){
	  Random rand=new Random();
	  BufferedImage copiedImage= deepCopy(masterImage);
	  int centerx=rand.nextInt(genome.IMG_WIDTH/16)-rand.nextInt(genome.IMG_WIDTH/16)+genome.IMG_WIDTH/2;
	  int centery=rand.nextInt(genome.IMG_HEIGHT/16)-rand.nextInt(genome.IMG_HEIGHT/16)+genome.IMG_HEIGHT/2;
	  
	  
	  Raster masterRaster=masterImage.getRaster();
	  
	  
	  
	  
	  
	  
	  
	  int[] x0={0,0,centerx};
	  int[] y0={0,genome.IMG_HEIGHT,centery};
	  int[] x1={0,centerx,genome.IMG_WIDTH};
	  int[] y1={0,centery,0};
	  int[] x2={genome.IMG_WIDTH,genome.IMG_WIDTH,centerx};
	  int[] y2={0,genome.IMG_HEIGHT,centery};
	  int[] x3={0,centerx,genome.IMG_WIDTH};
	  int[] y3={genome.IMG_HEIGHT,centery,genome.IMG_HEIGHT};
	  genome.geneList.get(0).xpoints=x0;
	  genome.geneList.get(0).ypoints=y0;
	  genome.geneList.get(1).xpoints=x1;
	  genome.geneList.get(1).ypoints=y1;
	  genome.geneList.get(2).xpoints=x2;
	  genome.geneList.get(2).ypoints=y2;
	  genome.geneList.get(3).xpoints=x3;
	  genome.geneList.get(3).ypoints=y3;
	  
	  for(int i=4;i<200;i++){
		  Point[] myVertices=new Point[NPOINTS];
		   for(int j=0;j<NPOINTS;j++)
		      {
		        myVertices[j]=new Point(0, 0);
		        myVertices[j].x=rand.nextInt(genome.IMG_WIDTH);
		        myVertices[j].y=rand.nextInt(genome.IMG_HEIGHT);
		      }
		      genome.geneList.get(i).setPoints(myVertices);
	  }
	  
	  
	  
	  
	  
	 for(Gene myGene:genome.geneList){

      
      int randomAlpha=rand.nextInt(200)+55;
     
   
    
      myGene.a=randomAlpha;
      
      int[] myColors=getAreaColorAvg(myGene,copiedImage);
      myGene.r=myColors[0];
      myGene.g=myColors[1];
      myGene.b=myColors[2];
     // subtractGene(myGene,copiedImage);
    }
  
	  
  }
  
  
  static BufferedImage deepCopy(BufferedImage bi) {
	  ColorModel cm = bi.getColorModel();
	  boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	  WritableRaster raster = bi.copyData(null);
	  return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	 }
  
  
  //subtracts a drawn gene from an image.
  //this will edit the input image.
  public static void subtractGene(Gene gene,BufferedImage image){
	  
	  Raster raster=image.getRaster();
	  int red,blue,green;
	  
	  int minX=Math.min(gene.xpoints[0], Math.min(gene.xpoints[1], gene.xpoints[2]));
	  int maxX=Math.max(gene.xpoints[0], Math.max(gene.xpoints[1], gene.xpoints[2]));
	  int minY=Math.min(gene.ypoints[0], Math.min(gene.ypoints[1], gene.ypoints[2]));
	  int maxY=Math.max(gene.ypoints[0], Math.max(gene.ypoints[1], gene.ypoints[2]));
	 
	  
	  for(int x=minX;x<=maxX;x++){
		  for(int y=minY;y<=maxY;y++){
			  if(gene.contains(x, y)){
				  int[] pixel={0,0,0};
				  raster.getPixel(x, y, pixel);
				  
				  red=Math.max(pixel[0]-gene.r,0);
				  green=Math.max(pixel[1]-gene.g,0);
				  blue=Math.max(pixel[2]-gene.b,0);
				  //System.out.println(red+";"+green+";"+blue);
				  int rgb=new Color(red,green,blue).getRGB();
				  image.setRGB(x, y, rgb);
				  
			  }
				  
		  
		  }
	  
	  
		  }
	  
	  
  }
  
  
  
  
  public static int[] getAreaColorAvg(Gene gene,BufferedImage image){
	  int redCount=0;
	  int blueCount=0;
	  int greenCount=0;
	  int nPixCount=1;
	  Raster raster=image.getRaster();
	  
	  //Find the min and max of the x and y
	  int minX=Math.min(gene.xpoints[0], Math.min(gene.xpoints[1], gene.xpoints[2]));
	  int maxX=Math.max(gene.xpoints[0], Math.max(gene.xpoints[1], gene.xpoints[2]));
	  int minY=Math.min(gene.ypoints[0], Math.min(gene.ypoints[1], gene.ypoints[2]));
	  int maxY=Math.max(gene.ypoints[0], Math.max(gene.ypoints[1], gene.ypoints[2]));
	 
	  
	  for(int x=minX;x<=maxX;x++){
		  for(int y=minY;y<=maxY;y++){
			  if(gene.contains(x, y)){
				  int[] pixel={0,0,0};
				  raster.getPixel(x, y, pixel);
				  
				  redCount+=pixel[0];
				  blueCount+=pixel[1];
				  greenCount+=pixel[2];
				  nPixCount++;
				  
			  }
				  
		  
		  }
	  
	  
		  }
	  int red=redCount/nPixCount;
	  int blue=blueCount/nPixCount;
	  int green=greenCount/nPixCount;
	  
	  int[] pixout={red,blue,green};
	  return pixout;
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
	    	Gene gene=myGenome.geneList.get(i);
	    	checkColorValues(gene);
	    	// gene.print();
	      myGraphics.setColor(new Color(gene.r,
	    		  gene.g, gene.b,
	    		  gene.a));
	      myGraphics.fillPolygon(gene);
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
    	Gene gene=myGenome.geneList.get(i);
    	checkColorValues(gene);
      myGraphics.setColor(new Color(gene.r,gene.g, gene.b,gene.a));
     
      myGraphics.fillPolygon(myGenome.geneList.get(i));
    }
    return myIm;
  }

  
  public static void checkColorValues(Gene gene){
	  if(gene.a>255)gene.a=255;
  	if(gene.a<0)gene.a=0;
  	if(gene.r>255)gene.r=255;
	if(gene.r<0)gene.r=0;
	if(gene.g>255)gene.g=255;
	if(gene.g<0)gene.g=0;
	if(gene.b>255)gene.b=255;
	if(gene.b<0)gene.b=0;
	
	  
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
