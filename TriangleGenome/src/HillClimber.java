import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

public class HillClimber extends Thread
{
  static Random rand = new Random();
  public BufferedImage image;
  public Genome genome;
  boolean repeat;
  Gene lastGene;
  int maxBound;
  int lastAllele;
  int lastShift;

  public HillClimber(BufferedImage img)
  {
    image =img;
    repeat = false;
  }

  public boolean climbStep(Genome myGenome)
  { 
    genome=myGenome;
    // mutate random gene at random allele
    Gene mutateGene = getGene(myGenome);
    int mutateAlleleIndex = getAllele(mutateGene);
    maxBound = getAlleleBounds(myGenome, mutateAlleleIndex);
    int shiftAmount = rand.nextInt(maxBound / 5);// shift by up to 20%
    shiftAmount -= (maxBound / 10);// subtract, to leave shift by +/- 10
    
    //special case for moving all x or all y vertices, we need mutateAlleleValue to be maxX/minX depending on if shiftAmount is + or -
    int mutateAlleleValue;
    if(mutateAlleleIndex==10)
    {
      if(shiftAmount>0)
      {
        mutateAlleleValue=GenomeUtilities.maxGenesDim(mutateGene.xpoints);
      }
      else
      {
        mutateAlleleValue=GenomeUtilities.minGenesDim(mutateGene.xpoints);
      }
    }
    else if(mutateAlleleIndex==11)
    {
      if(shiftAmount>0)
      {
        mutateAlleleValue=GenomeUtilities.maxGenesDim(mutateGene.ypoints);
      }
      else
      {
        mutateAlleleValue=GenomeUtilities.minGenesDim(mutateGene.ypoints);
      }
    }
    else
    {
      mutateAlleleValue = Mutate.getAlleleValue(mutateGene, mutateAlleleIndex);
    }
    
    if (mutateAlleleValue + shiftAmount > maxBound)
    {
      shiftAmount = maxBound - mutateAlleleValue;// this will set mutateAlleleValue to max when mutate is called
    }
    if (mutateAlleleValue + shiftAmount < 0)
    {
      shiftAmount = -mutateAlleleValue;// will set mutateAlleleValue to 0 when mutate is called
    }
    
    int iStep=5;
    if(myGenome.fitscore<10000)
    {
      iStep=3;
    }
    if(myGenome.fitscore<7500)
    {
      iStep=2;
    }
    if(myGenome.fitscore<5000)
    {
      iStep=1;
    }
    
    long startScoreLocal =Statistics.getSmallFitScore(GenomeUtilities.getBufferedImage(genome),image,iStep);
    //long startScoreGlobal =Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),image);
    Mutate.exposeToRadiation(mutateGene, mutateAlleleIndex, shiftAmount);
    long endScoreLocal =Statistics.getSmallFitScore(GenomeUtilities.getBufferedImage(genome),image,iStep);
    
    lastGene = mutateGene;
    lastAllele = mutateAlleleIndex;
    lastShift = shiftAmount;

    if(endScoreLocal > startScoreLocal)
    {
      revertGenome(lastGene, lastAllele, lastShift);
    }
    else if(endScoreLocal<startScoreLocal)repeatMutation(myGenome, lastGene, lastAllele, lastShift, maxBound, startScoreLocal, endScoreLocal,iStep);
    
    //long endScoreGlobal=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),image);//TODO debug
//    if(endScoreLocal<startScoreLocal&&endScoreGlobal>startScoreGlobal)
//    {
//      System.out.println("broken local fit");
//    }
    
    return endScoreLocal < startScoreLocal;
  }
  
  public void revertGenome(Gene mutateGene, int allele, int shiftAmount)
  {
    Mutate.exposeToRadiation(lastGene, lastAllele, -lastShift);
  }
  
  public void repeatMutation(Genome myGenome, Gene mutateGene, int allele, int shiftAmount, int maxBound, long startScore, long endScore,int iStep)
  {
   // long f0score=startScore;
    long previousScore=startScore;
    long currentScore=endScore;
    int mutateAlleleValue;
    
    while(currentScore<previousScore)
    {
      /////////////////////////////////////////////////////////////////////////////////////////
      //special case for moving all x or all y vertices, we need mutateAlleleValue to be maxX/minX depending on if shiftAmount is + or -
      if(allele==10)
      {
        if(shiftAmount>0)
        {
          mutateAlleleValue=GenomeUtilities.maxGenesDim(mutateGene.xpoints);
        }
        else
        {
          mutateAlleleValue=GenomeUtilities.minGenesDim(mutateGene.xpoints);
        }
      }
      else if(allele==11)
      {
        if(shiftAmount>0)
        {
          mutateAlleleValue=GenomeUtilities.maxGenesDim(mutateGene.ypoints);
        }
        else
        {
          mutateAlleleValue=GenomeUtilities.minGenesDim(mutateGene.ypoints);
        }
      }
      else
      {
        mutateAlleleValue = Mutate.getAlleleValue(mutateGene, allele);
      }
      /////////////////////////////////////////////////////////////////////////////////////////
      
      if (mutateAlleleValue + shiftAmount > maxBound)
      {
        shiftAmount = maxBound - mutateAlleleValue;// this will set mutateAlleleValue to max when mutate is called
      }
      if (mutateAlleleValue + shiftAmount < 0)
      {
        shiftAmount = -mutateAlleleValue;// will set mutateAlleleValue to 0 when mutate is called
      }
      Mutate.exposeToRadiation(mutateGene, allele, shiftAmount);      
      previousScore=currentScore;
      //currentScore = Statistics.getFitScore(GenomeUtilities.getBufferedImage(myGenome), image);
      currentScore=Statistics.getSmallFitScore(GenomeUtilities.getBufferedImage(genome),image,iStep);
    }    
    revertGenome(lastGene, lastAllele, lastShift);
  }
  
  public Gene getGene(Genome myGenome)
  {
    int randomGeneInt;    
    Gene myGene;
    randomGeneInt=rand.nextInt(myGenome.NUM_GENES);    
    myGene=myGenome.geneList.get(randomGeneInt);
//    do
//    {
//      randomGeneInt=rand.nextInt(myGenome.NUM_GENES);    
//      myGene=myGenome.geneList.get(randomGeneInt);
//    }while(getGeneHelper(myGene));
    
    return myGene;
  }
  
  public boolean getGeneHelper(Gene myGene)
  {
    boolean findAnotherGene=true;
    
    int xmax=Math.max(myGene.xpoints[0],myGene.xpoints[1]);
    xmax=Math.max(myGene.xpoints[2], xmax);
    
    int xmin=Math.min(myGene.xpoints[0], myGene.xpoints[1]);
    xmin=Math.min(myGene.xpoints[2], xmin);
    
    int ymax=Math.max(myGene.ypoints[0],myGene.ypoints[1]);
    ymax=Math.max(myGene.ypoints[2], ymax);
    
    int ymin=Math.min(myGene.ypoints[0], myGene.ypoints[1]);
    ymin=Math.min(myGene.ypoints[2], ymin);
    
    int[] xsample=new int[5];//change this to a variable amount
    int[] ysample=new int[5];
    
    for(int i=0;i<5;i++)
    {
      do
      {
        xsample[i]=rand.nextInt(xmax-xmin)+xmin;
        ysample[i]=rand.nextInt(ymax-ymin)+ymin;
      }while(myGene.contains(xsample[i], ysample[i]));
    }
    
    return findAnotherGene;
  }
  
  public int getAllele(Gene myGene)
  {
    int myAlleleIndex=rand.nextInt(myGene.NALLELE+2);// 10 alleles+2 for move all x and move all y
    return myAlleleIndex;
  }

  public int getAlleleBounds(Genome myGenome, int allele)
  {
    int maxBound = 0;
    int colorMax = 255;
    switch (allele)
    {
    case 0:
      maxBound = myGenome.IMG_WIDTH;
      break;
    case 1:
      maxBound = myGenome.IMG_HEIGHT;
      break;
    case 2:
      maxBound = myGenome.IMG_WIDTH;
      break;
    case 3:
      maxBound = myGenome.IMG_HEIGHT;
      break;
    case 4:
      maxBound = myGenome.IMG_WIDTH;
      break;
    case 5:
      maxBound = myGenome.IMG_HEIGHT;
      break;
    case 6:
      maxBound = colorMax;
      break;
    case 7:
      maxBound = colorMax;
      break;
    case 8:
      maxBound = colorMax;
      break;
    case 9:
      maxBound = colorMax;
      break;
    case 10:
      maxBound = myGenome.IMG_WIDTH;
      break;
    case 11:
      maxBound = myGenome.IMG_HEIGHT;
      break;
    }
    return maxBound;
  }

  public long getLocalFit(Gene gene, int allele, int shiftMagnitude)
  {
	  
	  BufferedImage genomeImage=GenomeUtilities.getBufferedImage(genome);
	  Raster genomeRaster=genomeImage.getRaster();
    Raster raster = image.getRaster();
    int red, blue, green;
    
    long sum = 0;

    int minX = Math.min(gene.xpoints[0],
        Math.min(gene.xpoints[1], gene.xpoints[2]));
    int maxX = Math.max(gene.xpoints[0],
        Math.max(gene.xpoints[1], gene.xpoints[2]));
    int minY = Math.min(gene.ypoints[0],
        Math.min(gene.ypoints[1], gene.ypoints[2]));
    int maxY = Math.max(gene.ypoints[0],
        Math.max(gene.ypoints[1], gene.ypoints[2]));
    
    //adds to boundaries based on affected variables
    //
   if(allele<6&&shiftMagnitude>0){
	   if(allele%2==0) {
		   minX=Math.max(minX-shiftMagnitude,0);
		   maxX=Math.min(minX+shiftMagnitude,image.getWidth());
		  // System.out.println("xchange");

	   }
	   else{
		   minY=Math.max(minY-shiftMagnitude,0);
		   maxY=Math.min(maxX+shiftMagnitude,image.getHeight());
		  // System.out.println("ychange");   
	   }
	   
	   
	   
   }
  
   //prevent streight lines
   //System.out.println(shiftMagnitude+"->"+allele+";"+maxX+";"+minX+";"+maxY+";" + minY);
   
   if(minX==maxX){minX--;maxX++;}
   if(minY==maxY){minY--;maxY++;}
    
    
    long runsum=0;
    
    for (int x = minX; x < maxX; x++)
    {
      for (int y = minY; y < maxY; y++)
      {
        
          int[] pixel =
          { 0, 0, 0 };
          int[] genomePixel =
              { 0, 0, 0 };
          //System.out.println(x+";"+y);
          raster.getPixel(x, y, pixel);
          genomeRaster.getPixel(x, y, genomePixel);

          red = (pixel[0] - genomePixel[0]);
          red = red * red;
          green = pixel[1] - genomePixel[1];
          green = green * green;
          blue = pixel[2] - genomePixel[2];
          blue = blue * blue;
          
          sum = red + green + blue;
          runsum+=sum;
           //System.out.println(red+";"+green+";"+blue);

        

      }

    }
//returns euclidian distance.
     return runsum / ((maxX - minX) * (maxY - minY));
  }

}
