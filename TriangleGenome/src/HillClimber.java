import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

public class HillClimber extends Thread
{
  // repeat supersedes revert
  static Random rand = new Random();
  public BufferedImage image;
  boolean repeat;
  Gene lastGene;
  // I need to to define input parameters based on which allele is which.
  int lastAllele;
  int lastShift;

  public HillClimber(BufferedImage img)
  {
    image = img;
    repeat = false;
  }

  public void climbLoop(Genome genome, int N)
  {
    for (int i = 0; i < N; i++)
    {
      repeat = climbStep(genome);
      System.out.println(System.currentTimeMillis());
    }
  }

  public boolean climbStep(Genome myGenome)
  {
    long startScore = Statistics.getFitScore(GenomeUtilities.getBufferedImage(myGenome), image);
    int maxBound;

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

    Mutate.exposeToRadiation(mutateGene, mutateAlleleIndex, shiftAmount);

    lastGene = mutateGene;
    lastAllele = mutateAlleleIndex;
    lastShift = shiftAmount;

    long endScore = Statistics.getFitScore(GenomeUtilities.getBufferedImage(myGenome), image);

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
    long f0score=startScore;
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

  public Gene getWurstQuadrent(Genome genome)
  {
    long tmpscore = 0;
    long score;
    int tmp = 0;
    for (int i = 0; i < 4; i++)
    {
      score = getLocalFit(genome.geneList.get(i));
      if (score > tmpscore)
      {
        tmp = i;
        tmpscore = score;
      }

    }

    return genome.geneList.get(tmp);

  }

  public long getLocalFit(Gene gene)
  {

    Raster raster = this.image.getRaster();
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

    for (int x = minX; x <= maxX; x++)
    {
      for (int y = minY; y <= maxY; y++)
      {
        if (gene.contains(x, y))
        {
          int[] pixel =
          { 0, 0, 0 };
          raster.getPixel(x, y, pixel);

          red = (pixel[0] - gene.r);
          red = red * red;
          blue = pixel[0] - gene.b;
          blue = blue * blue;
          green = pixel[0] - gene.g;
          green = green * green;
          sum = red + green + blue;

          // System.out.println(red+";"+green+";"+blue);

        }

      }

    }

    return sum / ((maxX - minX) * (maxY - minY));
  }

}
