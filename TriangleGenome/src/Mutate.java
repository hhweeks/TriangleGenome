import java.util.Random;

public class Mutate
{
  static Random rand = new Random();
  static final int NUMALLELES = 10;
  static final int NUMCOORD = 6;// number of alleles in a gene that are coord,
                                // rather than color values
  static int maxCoordShift;
  static Genome refGen;

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
    case 10:
      for(int i=0;i<mutateGene.npoints;i++)
      {
        mutateGene.xpoints[i]+= shiftAmount;
      }
      break;
    case 11:
      for(int i=0;i<mutateGene.npoints;i++)
      {
        mutateGene.ypoints[i]+= shiftAmount;
      }
      break;
    case 12:
        for(int i=0;i<mutateGene.npoints;i++)
        {
          mutateGene.ypoints[i]+= shiftAmount;
        }
        break;
    }
  }
  
  //only gets called on cases 0-9
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
}
