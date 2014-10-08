import java.util.Random;

public class Mutate
{
  static Random rand = new Random();
  static final int NUMALLELES = 10;
  static final int NUMCOORD = 6;// number of alleles in a gene that are coord,
                                // rather than color values
  static int maxCoordShift;
  static Genome refGen;

//  public static void exposeToRadiation(Genome myGenome)
//  {
//    refGen = myGenome;
//    Gene mutateGene = myGenome.geneList.get(rand.nextInt(myGenome.NUM_GENES));
//    int mutateWhat = rand.nextInt(NUMALLELES);
//    maxCoordShift = myGenome.IMG_WIDTH / 5;
//
//    switch (mutateWhat)
//    {
//    // mutate coordinates
//    case 0:
//      mutateGene.xpoints[0] = mutateXCoord(mutateGene.xpoints[0]);
//      break;
//    case 1:
//      mutateGene.ypoints[0] = mutateYCoord(mutateGene.ypoints[0]);
//      break;
//    case 2:
//      mutateGene.xpoints[1] = mutateXCoord(mutateGene.xpoints[1]);
//      break;
//    case 3:
//      mutateGene.ypoints[1] = mutateYCoord(mutateGene.ypoints[1]);
//      break;
//    case 4:
//      mutateGene.xpoints[2] = mutateXCoord(mutateGene.xpoints[2]);
//      break;
//    case 5:
//      mutateGene.ypoints[2] = mutateYCoord(mutateGene.ypoints[2]);
//      break;
//    // mutate colors
//    case 6:
//      mutateGene.r = mutateColor(mutateGene.r);
//      break;
//    case 7:
//      mutateGene.g = mutateColor(mutateGene.g);
//      break;
//    case 8:
//      mutateGene.b = mutateColor(mutateGene.b);
//      break;
//    case 9:
//      mutateGene.a = mutateColor(mutateGene.a);
//      break;
//    // translate x/y coordinates
//    }
//  }

  public static void exposeToRadiation(Gene mutateGene, int allele, int shiftAmount)
  {
    switch (allele)
    {
    // mutate coordinates
    case 0:
      mutateGene.xpoints[0] += shiftAmount;
      break;
    case 1:
      mutateGene.ypoints[0] += shiftAmount;
      break;
    case 2:
      mutateGene.xpoints[1] += shiftAmount;
      break;
    case 3:
      mutateGene.ypoints[1] += shiftAmount;
      break;
    case 4:
      mutateGene.xpoints[2] += shiftAmount;
      break;
    case 5:
      mutateGene.ypoints[2] += shiftAmount;
      break;
    // mutate colors
    case 6:
      mutateGene.r += shiftAmount;
      break;
    case 7:
      mutateGene.g += shiftAmount;
      break;
    case 8:
      mutateGene.b += shiftAmount;
      break;
    case 9:
      mutateGene.a += shiftAmount;
      break;
    // translate x/y coordinates
    }
  }
  
  public static int getAlleleValue(Gene mutateGene, int allele)
  {
    switch (allele)
    {
    case 0:
      return mutateGene.xpoints[0];
    case 1:
      return mutateGene.ypoints[0];
    case 2:
      return mutateGene.xpoints[1];
    case 3:
      return mutateGene.ypoints[1];
    case 4:
      return mutateGene.xpoints[2];
    case 5:
      return mutateGene.ypoints[2];
    case 6:
      return mutateGene.r;
    case 7:
      return mutateGene.g;
    case 8:
      return mutateGene.b;
    case 9:
      return mutateGene.a;
    }
    return 0;
  }

  private static int mutateXCoord(int coord)
  {
    int delta = rand.nextInt(maxCoordShift);
    delta -= (maxCoordShift / 2);// delta now positve or negative
    if (coord + delta > refGen.IMG_WIDTH || coord + delta < 0)
    {
      // if new coord would be out of bounds, change the sign of delta
      delta *= (-1);
    }
    return (coord += delta);
  }

  private static int mutateYCoord(int coord)
  {
    int delta = rand.nextInt(maxCoordShift);
    delta -= (maxCoordShift / 2);// delta now positve or negative
    if (coord + delta > refGen.IMG_HEIGHT || coord + delta < 0)
    {
      // if new coord would be out of bounds, change the sign of delta
      delta *= (-1);
    }
    return (coord + delta);
  }

  private static int mutateColor(int color)
  {
    int delta = rand.nextInt(50);// 50 is 1/5th 0-255
    delta -= (50 / 2);// equal chance to be positive or negative
    if (color + delta > 255 || color + delta < 0)
    {
      delta *= (-1);
    }

    return (color + delta);
  }

  private static int mutateTranslation(int coord)// move all x's or y's
  {
    // TODO write this method
    return coord;
  }

}
