import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

public class HillClimber extends Thread
{
  // repeat supersedes revert
  static Random rand = new Random();
  public BufferedImage image;
  public Genome genome;
  boolean repeat;
  Gene lastGene;
  int maxBound;
  // I need to to define input parameters based on which allele is which.
  int lastAllele;
  int lastShift;

  public HillClimber(BufferedImage img)
  {
	image =img;
    repeat = false;
  }

  public void climbLoop(Genome genome, int N)
  {
    for (int i = 0; i < N; i++)
    {
      repeat = climbStep(genome);
     //System.out.println(System.currentTimeMillis());
    }
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

    
    int mutateAlleleValue = Mutate.getAlleleValue(mutateGene, mutateAlleleIndex);
    
   
    
    if (mutateAlleleValue + shiftAmount > maxBound)
    {
      shiftAmount = maxBound - mutateAlleleValue;// this will set mutateAlleleValue to max when mutate is called
    }
    if (mutateAlleleValue + shiftAmount < 0)
    {
      shiftAmount = -mutateAlleleValue;// will set mutateAlleleValue to 0 when mutate is called
    }
    long startScore =getLocalFit(mutateGene, mutateAlleleIndex, Math.abs(shiftAmount)) ;
    Mutate.exposeToRadiation(mutateGene, mutateAlleleIndex, shiftAmount);
    long endScore =getLocalFit(mutateGene, mutateAlleleIndex, Math.abs(shiftAmount)) ;
    //System.out.println(startScore+";"+endScore);
    
    lastGene = mutateGene;
    lastAllele = mutateAlleleIndex;
    lastShift = shiftAmount;

   

    if(endScore > startScore)revertGenome(lastGene, lastAllele, lastShift);
    else if(endScore<startScore)repeatMutation(myGenome, lastGene, lastAllele, lastShift, maxBound, startScore, endScore);
    return endScore < startScore;
  }
  
  public void revertGenome(Gene mutateGene, int allele, int shiftAmount)
  {
    Mutate.exposeToRadiation(lastGene, lastAllele, -lastShift);
  }
  
  public void repeatMutation(Genome myGenome, Gene mutateGene, int allele, int shiftAmount, int maxBound, long startScore, long endScore)
  {
   // long f0score=startScore;
    long previousScore=startScore;
    long currentScore=endScore;
    int mutateAlleleValue;
    
    while(currentScore<previousScore)
    {
      mutateAlleleValue = Mutate.getAlleleValue(mutateGene, allele);
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
      currentScore = Statistics.getFitScore(GenomeUtilities.getBufferedImage(myGenome), image);
    }
    
    revertGenome(lastGene, lastAllele, lastShift);
  }
  
  public Gene getGene(Genome myGenome)
  {
    Gene myGene=myGenome.geneList.get(rand.nextInt(myGenome.NUM_GENES));
    return myGene;
  }
  
  public int getAllele(Gene myGene)
  {
    int myAlleleIndex=rand.nextInt(myGene.NALLELE);// 10 alleles
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
    }
    return maxBound;
  }

//  public Gene getWurstQuadrent(Genome genome)
//  {
//    long tmpscore = 0;
//    long score;
//    int tmp = 0;
//    for (int i = 0; i < 4; i++)
//    {
//      score = getLocalFit(genome.geneList.get(i));
//      if (score > tmpscore)
//      {
//        tmp = i;
//        tmpscore = score;
//      }
//
//    }
//
//    return genome.geneList.get(tmp);
//
//  }
//shiftMagnitude is the absolute value of the shift.
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
