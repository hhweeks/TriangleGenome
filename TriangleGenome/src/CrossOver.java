import java.util.ArrayList;
import java.util.Random;
/****************************************************************************
 *UnitTestMain
 *@author Paige Romero
 *@author Hans Weeks
 *This static object is  used for single point cross over. The main method (breed)
 *accepts 4 Genomes as arguments, making the first 2 parents and the latter 2 children.
 *Breed copies genes from one parent up to the cross over point, calls geneSplice to create
 *a gene consisting of both parents, then copies the rest of the genes from the second parent.
 ****************************************************************************/
public class CrossOver
{
  /****************************************************************************
   * breed
   * Input:4 genomes, the first 2 parent the second 2 and an integer between 0 and 2000 for 
   *  a crossover TODO make crossover above 10
   * Output:none
   * Description:does single point cross over. Clears the genes of the child nodes
   *  before adding parent genes (meaning children can be to-recycle Genomes)
   ****************************************************************************/
    
	
	public static void multiBreed(Genome papaGenome, Genome mamaGenome,
		      Genome sonGenome, Genome daughterGenome, int NPoints)
		  {
		Random rand=new Random();
		int crossPoint=rand.nextInt(200)+1600;
		breed(papaGenome,mamaGenome,
			      sonGenome,daughterGenome,crossPoint);
			for(int i=0;i<NPoints;i++){
				crossPoint=rand.nextInt(2000);
				breed(GenomeUtilities.genomeCopy(sonGenome),GenomeUtilities.genomeCopy(daughterGenome),
					      sonGenome,daughterGenome,crossPoint);
				
			}
		
		
		
		  }
	
	
	
	
	public static void breed(Genome papaGenome, Genome mamaGenome,
      Genome sonGenome, Genome daughterGenome, int crossPoint)
  {
    int genecross = crossPoint / 10;
    int genesplit = crossPoint % 10;

    sonGenome.geneList.clear();
    sonGenome.geneList.addAll(papaGenome.geneList.subList(0, genecross));
    sonGenome.geneList.add(geneSplice(papaGenome.geneList.get(genecross),
        mamaGenome.geneList.get(genecross), genesplit));
    for (int i = genecross + 1; i < mamaGenome.geneList.size(); i++)
    {
      Gene tempGene = GenomeUtilities.geneCopy(mamaGenome.geneList.get(i));
      sonGenome.geneList.add(tempGene);
    }
    // sonGenome.geneList.addAll(mamaGenome.geneList.subList(genecross,mamaGenome.geneList.size()-1));

    daughterGenome.geneList.clear();
    daughterGenome.geneList.addAll(mamaGenome.geneList.subList(0, genecross));
    daughterGenome.geneList.add(geneSplice(mamaGenome.geneList.get(genecross),
        papaGenome.geneList.get(genecross), genesplit));
    for (int i = genecross + 1; i < papaGenome.geneList.size(); i++)
    {
      Gene tempGene = GenomeUtilities.geneCopy(papaGenome.geneList.get(i));
      daughterGenome.geneList.add(tempGene);
    }
    // daughterGenome.geneList.addAll(papaGenome.geneList.subList(genecross,
    // mamaGenome.geneList.size()-1));

  }
    
  /****************************************************************************
  * breed
  * Input: 3 genomes, the first 2 parents, the last the child gene. Top parent
  *   is the parent whose genes appear first in the child's list (papaGenome is
  *   top parent for son, mamaGenome is top parent for daughter.
  * Output:returns the spliced gene consisting of top and bottom parents' alleles
  * Description:puts the 2 parents alleles (gene data) into a 2D array, builds an
  *   array with the information from both, switching at the withinGeneSplit,
  *   then assigns these values to a new gene, which is then returned.
  ****************************************************************************/
  public static Gene geneSplice(Gene topParent, Gene bottomParent, int crossPoint)
  {

    int topGenesToCompare = (crossPoint / 10);// all genes up until splice

    int withinGeneSplit = crossPoint % 10;
    Gene[] genArr =
    { topParent, bottomParent };
    int[][] alleleValue = new int[2][genArr[0].NALLELE];// 3*10

    // this loop grabs the 10 alleles from each parent
    for (int i = 0; i < genArr.length; i++)// less than 2
    {
      for (int j = 0; j < genArr[i].NPOINTS; j++)// get xpoints and ypoints, 3
                                                 // of each and add them to the
                                                 // array
      {
        alleleValue[i][2 * j] = genArr[i].xpoints[j];
        alleleValue[i][(2 * j) + 1] = genArr[i].ypoints[j];
      }
      alleleValue[i][6] = genArr[i].r;
      alleleValue[i][7] = genArr[i].g;
      alleleValue[i][8] = genArr[i].b;
      alleleValue[i][9] = genArr[i].a;
    }

    int numberOfGenes = genArr[0].NALLELE;// 10 alleles
    int[] spliceArr = new int[numberOfGenes];
    int binParent = 0;
    for (int i = 0; i < numberOfGenes; i++)// 10 alleles
    {
      if (i == withinGeneSplit)
      {
        binParent = 1;
      }
      spliceArr[i] = alleleValue[binParent][i];
    }

    Gene childSplicedGene = new Gene();
    // ///////////////////////////////////
    for (int j = 0; j < childSplicedGene.NPOINTS; j++)// get xpoints and ypoints, 3 of each and add them to the array
    {
      childSplicedGene.xpoints[j] = spliceArr[2 * j];
      childSplicedGene.ypoints[j] = spliceArr[(2 * j) + 1];
    }
    childSplicedGene.r = spliceArr[6];
    childSplicedGene.g = spliceArr[7];
    childSplicedGene.b = spliceArr[8];
    childSplicedGene.a = spliceArr[9];
    // ///////////////////////////////////////
    return childSplicedGene;
  }

}