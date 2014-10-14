import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
/****************************************************************************
 *UnitTestMain
 *@author Hans Weeks
 *This method unit tests the Triangle Genome Project when assertions are enabled.
 *This class has its own main() for this purpose. The areas it tests are:
 *1.)Genome objects for valid values
 *2.)Hamming Distance class for correct results
 *3.)Crossover class for correctly crossed-over child genes
 *in addition to assertion tests, DEBUG_OUTPUT prints additional debugging information
 *about the program, DEBUG_CROSSOVER prints child gene vs. parent gene comparisons.
 * 
 ****************************************************************************/
public class UnitTestMain
{
  private boolean DEBUG_OUTPUT=false;
  private boolean DEBUG_CROSSOVER=false;
  private Random rand=new Random();
  
  int imgWidth=500;//consider loading and actual image here
  int imgHeight=500;
  
  
  /****************************************************************************
   * Constructor
   * Input:none
   * Output:none
   * Description: when created, calls the unit tests's methods to test the Triangle
   *  Genome project in 3 major ares: validity of Genome, validity of Hamming test,
   *  and validity of Crossover method.
   ****************************************************************************/
  public UnitTestMain()
  {
    int imgWidth=500;//consider loading and actual image here
    int imgHeight=500;
    
    Genome gen1=new Genome(imgWidth,imgHeight);
    GenomeUtilities.setRandomGenome(gen1);
    Genome gen2=new Genome(imgWidth,imgHeight);
    GenomeUtilities.setRandomGenome(gen2);
    
    genomeTest(gen1);
    hammingTest(gen1, gen2);
    crossoverTestHelper();
    writeTest(gen1);
  }

  /****************************************************************************
   * genomeTest
   * Input:Genome object to validate
   * Output:none
   * Description:validates that the given Genome by testing that each gene for color values that
   *  are above 0 and below 255, all points are within the bounds of the image size and that
   *  each Gene has 3 sets of x and y points.
   ****************************************************************************/
  public void genomeTest(Genome gen)
  {
    for(Gene gene:gen.geneList)
    {
      for(int i=0;i<gene.xpoints.length;i++)
      {
//        System.out.println(gene.xpoints[i]);
        assert (gene.xpoints[i]>=0 && gene.xpoints[i]<gen.IMG_WIDTH);
        assert (gene.ypoints[i]>=0 && gene.ypoints[i]<gen.IMG_HEIGHT);
      }
      assert (gene.r>=0 && gene.r<255);
      assert (gene.g>=0 && gene.g<255);
      assert (gene.b>=0 && gene.b<255);
      assert (gene.a>=0 && gene.a<255);
      assert (gene.npoints==3);
    }
  }
  
  /****************************************************************************
   * hammingTest
   * Input: two Genome objects to test the Hamming distance of
   * Output:  none
   * Description: runs assertion tests to ensure the Hamming distance is measured correctly
   *  on two Genome objects with known Hamming distances. Calls determinedGenome method
   *  to create genomes with known Hamming distances.
   ****************************************************************************/
  public void hammingTest(Genome gen1, Genome gen2)
  {    
      long hamD;
      
      //\\random test
      if(DEBUG_OUTPUT)//there is no easy true/false for assert on random genomes, hamD should ~=1995
      {
        hamD=HammingDistance.calcDiff(gen1, gen2);
        System.out.println("the diff between two random Genomes:"+hamD);
      }      
      
      //\\genome-against-itself test
      hamD=HammingDistance.calcDiff(gen1, gen1);
      if(DEBUG_OUTPUT)System.out.println("the diff between a genome and itself:"+hamD);
      assert (hamD==0);
      if(DEBUG_OUTPUT)System.out.println("the diff between a genome and itself:"+hamD);
      
      //\\different genomes with same values
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 0, 100);
      hamD=HammingDistance.calcDiff(gen1, gen2);
      assert (hamD==0);
      if(DEBUG_OUTPUT)System.out.println("the diff between different genomes with same genes:"+hamD);
      
      //\\100% different test
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 1, 200);
      hamD=HammingDistance.calcDiff(gen1, gen2);
      assert (hamD==(gen1.NUM_GENES*gen1.geneList.get(0).NALLELE));//should = 2000
      if(DEBUG_OUTPUT)System.out.println("the diff between totally different genomes:"+hamD);
      
      //\\Hamm distance of 1
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 0, 100);
      gen2.geneList.get(0).a=254;//genomes now differ in 1 allele
      hamD=HammingDistance.calcDiff(gen1, gen2);
      assert (hamD==1);
      if(DEBUG_OUTPUT)System.out.println("the diff between genomes which differ by 1:"+hamD);
      
      //\\Hamm distance of 2, adjacent differences
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 0, 100);
      gen2.geneList.get(0).a=254;
      gen2.geneList.get(0).b=254;//genomes now differ in 2 alleles
      hamD=HammingDistance.calcDiff(gen1, gen2);
      assert (hamD==2);
      if(DEBUG_OUTPUT)System.out.println("the diff between genomes which differ by 2 adjacent:"+hamD);
      
      //\\distributed distance, equal to 1000
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 1, 254);
      for(int i=0;i<gen2.geneList.size();i++)
      {
        if(i%2==0)
        {
          gen2.geneList.set(i,GenomeUtilities.geneCopy(gen1.geneList.get(i)));
        }
      }
      hamD=HammingDistance.calcDiff(gen1, gen2);
      assert(hamD==1000);
      if(DEBUG_OUTPUT)System.out.println("the diff between genomes with distributed differences:"+hamD);
      
      //\\hamm distance between 2 genomes where the second is the child crossed over halfway through
      int crossoverPoint=1500;
      Genome genSon=new Genome(gen1.IMG_WIDTH, gen1.IMG_HEIGHT);
      Genome genDot=new Genome(gen1.IMG_WIDTH, gen1.IMG_HEIGHT);
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 1, 254);
      CrossOver.breed(gen1, gen2, genSon, genDot, crossoverPoint);
      hamD=HammingDistance.calcDiff(gen1, genSon);
      if(DEBUG_OUTPUT)System.out.println("the parent and child with crossover at "+crossoverPoint/10 + ":"+hamD);
      assert(hamD==(2000-crossoverPoint));
      checkParentage(gen1, gen2, genSon, crossoverPoint);      
  }
    
  /****************************************************************************
   * crossoverTestHelper
   * Input:none (unlike the other validations, this one creates its own Genomes to test addresses)
   * Output:none
   * Description: creates 4 Genomes to crossover, places them in an array, then randomizes the
   *  Genomes and calls the crossoverTest method on them. This is done in a loop, to test 6 sets
   *  of 4 random genomes
   ****************************************************************************/
  public void crossoverTestHelper()
  { 
    Genome gen1=new Genome(imgWidth,imgHeight);
    Genome gen2=new Genome(imgWidth,imgHeight);
    Genome gen3=new Genome(imgWidth,imgHeight);
    Genome gen4=new Genome(imgWidth,imgHeight);
    
    Genome[] genArr={gen1,gen2,gen3,gen4};//two parents two children
    int crossoverPoint=rand.nextInt(genArr[0].NUM_GENES*genArr[0].geneList.get(0).NALLELE);//next rand below 2000
    //int crossoverPoint=rand.nextInt(2000);
    
    for(int i=0;i<6;i++)//6 test cases of 
    {
      for(Genome myGenome: genArr)
      {
        GenomeUtilities.setRandomGenome(myGenome);
      }
      crossoverTest(genArr, crossoverPoint);
    }
  }
  
  /****************************************************************************
   * crossoverTest
   * Input: array of 4 genomes and a crossover point
   * Output: none
   * Description: ensures correct array length, then tests to make sure
   *  1.) the Genomes do not have the same address
   *  2.) the Genomes do not have identical values
   *  then assigns to 4 Genomes to the genDad, Mom, Son, Dot. Calls crossover on
   *  these genes at the crossover point the calls the checkParentage method on them
   ****************************************************************************/
  public void crossoverTest(Genome[] genArr, int crossoverPoint)
  {
   long hamD;   
   
   assert(genArr.length==4);
   
   for(int i=0;i<genArr.length;i++)
   {
     for(int j=0;j<genArr.length;j++)
     {
       if(i!=j)//don't compare address of Genome to itself (or else assert will fail)
       {
         assert(genArr[i]!=genArr[j]);//assert the addresses of each genome is different
         assert(0<HammingDistance.calcDiff(genArr[i],genArr[j]));//assert the genomes are not identical         
       }
     }
   }
   
   Genome genDad=genArr[0];
   Genome genMom=genArr[1];
   Genome genSon=genArr[2];
   Genome genDot=genArr[3];
   genSon.geneList.clear();
   genDot.geneList.clear();
   
   CrossOver.breed(genDad, genMom, genSon, genDot, crossoverPoint);
   checkParentage(genDad, genMom, genSon, crossoverPoint);
   checkParentage(genMom, genDad, genDot, crossoverPoint);
  }
  
  /****************************************************************************
   * checkParentage
   * Input: 3 Genomes (2 parents and 1 child in specified order) and the crossover point
   * Output: none
   * Description: topParent refers to the parent whose genes appear first (lower index)
   *  int the child's Genome. genDad is top parent for genSon and genMom is top parent for genDot.
   *  This method checks that the crossover process was correct by assigning the child Genome 3 sections:
   *  1.)the portion identical to the topParent
   *  2.)the Gene which contains both top and bottomParent alleles
   *  3.)the portion identical to the bottomParent
   *  the method asserts that that these sections are the same
   ****************************************************************************/
  public void checkParentage(Genome topParent, Genome bottomParent, Genome child, int crossoverPoint)
  {
    int topGenesToCompare=(crossoverPoint/10);//all genes up until splice    
    
    //assert genomes of the same size
    assert(topParent.geneList.size()==bottomParent.geneList.size()&&topParent.geneList.size()==child.geneList.size());
    
    //(1.)\\assert genes are the same for top genes of child and Top parent
    ///////////////////////////////////////////////////////////////////////
    for(int i=0;i<topGenesToCompare-1;i++)
    {
      for(int j=0;j<topParent.geneList.get(i).NPOINTS;j++)//three points
      {
        assert(topParent.geneList.get(i).xpoints[j]==child.geneList.get(i).xpoints[j]);
        assert(topParent.geneList.get(i).ypoints[j]==child.geneList.get(i).ypoints[j]);        
      }
      assert(topParent.geneList.get(i).r==child.geneList.get(i).r);
      assert(topParent.geneList.get(i).g==child.geneList.get(i).g);
      assert(topParent.geneList.get(i).b==child.geneList.get(i).b);
      assert(topParent.geneList.get(i).a==child.geneList.get(i).a);
    }
    
    //(2.)\\assert that the splice gene is correct part top parent, correct part bottom parent
    //////////////////////////////////////////////////////////////////////////////////////////
    int withinGeneSplit=crossoverPoint%10;
    Genome[] genArr={topParent,bottomParent,child};
    int[][] alleleValue=new int[3][topParent.geneList.get(0).NALLELE];//3*10
    
    //this loop grabs the 10 genes of the three genomes
    for(int i=0;i<genArr.length;i++)// less than 3
    {
      for(int j=0;j<genArr[i].geneList.get(topGenesToCompare).NPOINTS;j++)//get xpoints and ypoints, 3 of each and add them to the array
      {
        alleleValue[i][2*j]=genArr[i].geneList.get(topGenesToCompare).xpoints[j];
        alleleValue[i][(2*j)+1]=genArr[i].geneList.get(topGenesToCompare).ypoints[j];
        if(DEBUG_CROSSOVER)System.out.println(i+"'s x"+j+":"+genArr[i].geneList.get(topGenesToCompare).xpoints[j]);
        if(DEBUG_CROSSOVER)System.out.println(i+"'s y"+j+":"+genArr[i].geneList.get(topGenesToCompare).ypoints[j]);
      }
      alleleValue[i][6]=genArr[i].geneList.get(topGenesToCompare).r;
      alleleValue[i][7]=genArr[i].geneList.get(topGenesToCompare).g;
      alleleValue[i][8]=genArr[i].geneList.get(topGenesToCompare).b;
      alleleValue[i][9]=genArr[i].geneList.get(topGenesToCompare).a;
      if(DEBUG_CROSSOVER)System.out.println(i+"'s r"+":"+genArr[i].geneList.get(topGenesToCompare).r);
      if(DEBUG_CROSSOVER)System.out.println(i+"'s g"+":"+genArr[i].geneList.get(topGenesToCompare).g);
      if(DEBUG_CROSSOVER)System.out.println(i+"'s b"+":"+genArr[i].geneList.get(topGenesToCompare).b);
      if(DEBUG_CROSSOVER)System.out.println(i+"'s a"+":"+genArr[i].geneList.get(topGenesToCompare).a);
      if(DEBUG_CROSSOVER)System.out.print("\n");      
    }
    
    int numberOfGenes=genArr[0].geneList.get(topGenesToCompare).NALLELE;// 10 alleles
    int[] spliceArr=new int[numberOfGenes];
    int binParent=0;
    
    for(int i=0;i<numberOfGenes;i++)// 10 alleles
    {
      if(i==withinGeneSplit){binParent=1;}
      spliceArr[i]=alleleValue[binParent][i];
      if(DEBUG_CROSSOVER)System.out.println("spliceGene's "+i+":"+spliceArr[i]);
    }
    
    for(int i=0;i<numberOfGenes;i++)
    {
      int indexOfChild=2;
      assert(alleleValue[indexOfChild][i]==spliceArr[i]);
    }
    
    //(3.)\\assert genes are the same for bottom of child and bottomParent
    //////////////////////////////////////////////////////////////////////
    for(int i=topGenesToCompare+1;i<bottomParent.geneList.size();i++)
    {
      int topX=topParent.geneList.get(i).r;
      int bottomX=bottomParent.geneList.get(i).r;
      int childX=child.geneList.get(i).r;
      for(int j=0;j<bottomParent.geneList.get(i).NPOINTS;j++)//three points
      {       
        assert(bottomParent.geneList.get(i).xpoints[j]==child.geneList.get(i).xpoints[j]);
        assert(bottomParent.geneList.get(i).ypoints[j]==child.geneList.get(i).ypoints[j]);
      }
      bottomX=bottomParent.geneList.get(i).r;
      childX=child.geneList.get(i).r;
      if(DEBUG_CROSSOVER)System.out.println("bottomParent:"+bottomX+" , child:"+childX);
      assert(bottomParent.geneList.get(i).r==child.geneList.get(i).r);
      assert(bottomParent.geneList.get(i).g==child.geneList.get(i).g);
      assert(bottomParent.geneList.get(i).b==child.geneList.get(i).b);
      assert(bottomParent.geneList.get(i).a==child.geneList.get(i).a);
    }
  }
  
  public void writeTest(Genome gen)
  {
    XMLUtil.writeXML("UnitWriteTest.xml", gen);    
  }
  
  /****************************************************************************
   * determinedGenome
   * Input: Genome object to create deterministic genes in
   * Output: none
   * Description: creates a Genome where all points (x1->3 and y1->3) are pointVal
   *  and all colors (r,g,b, and a) are colorVal for the sake of comparison in the
   *  hammingTest method
   ****************************************************************************/
  public void determinedGenome(Genome gen, int pointVal, int colorVal)
  {
    gen.geneList.clear();
    
    Point[] myVertices=new Point[GenomeUtilities.NPOINTS];
    for(int i=0;i<GenomeUtilities.NPOINTS;i++)
    {
      myVertices[i]=new Point(pointVal, pointVal);
    }
    
    int[] myColors=new int[GenomeUtilities.NCOLORS];
    for(int i=0;i<GenomeUtilities.NCOLORS;i++)
    {
      myColors[i]=colorVal;
    }
    
    for(int i=0;i<gen.NUM_GENES;i++)
    {
      Gene gene=new Gene(myVertices,myColors);
      gen.geneList.add(gene);
    }
  }
  
  /****************************************************************************
   * Main
   * Description: creates a new UnitTestMain objecct
   ****************************************************************************/
  public static void main(String[] args)
  {
    new UnitTestMain();
  }
}