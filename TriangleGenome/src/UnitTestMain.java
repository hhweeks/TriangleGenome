import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;

public class UnitTestMain
{
  private boolean DEBUG_OUTPUT=true;
  private Random rand=new Random();
  
  int imgWidth=500;//consider loading and actual image here
  int imgHeight=500;
  
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
  }

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
      Genome genSon=new Genome(gen1.IMG_WIDTH, gen1.IMG_HEIGHT);
      Genome genDot=new Genome(gen1.IMG_WIDTH, gen1.IMG_HEIGHT);
      determinedGenome(gen1, 0, 100);
      determinedGenome(gen2, 1, 254);
      CrossOver.breed(gen1, gen2, genSon, genDot, 1005);
      hamD=HammingDistance.calcDiff(gen1, genSon);
      if(DEBUG_OUTPUT)System.out.println("the parent and child with crossover at 100:"+hamD);
//      assert(hamD==1005);
      checkParentage(gen1, gen2, genSon, 1005);
      
      //\\random crossover
      gen1.geneList.clear();
      gen2.geneList.clear();
      genSon.geneList.clear();
      genDot.geneList.clear();
      for(int i=0;i<200;i++)
      {
        gen1.geneList.add(new Gene());
        gen2.geneList.add(new Gene());
      }
      GenomeUtilities.setRandomGenome(gen1);
      GenomeUtilities.setRandomGenome(gen2);
      CrossOver.breed(gen1, gen2, genSon, genDot, 1492);
      checkParentage(gen1, gen2, genSon, 1492);
  }
  
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
  
  public void checkParentage(Genome topParent, Genome bottomParent, Genome child, int crossoverPoint)
  {
    int topGenesToCompare=(crossoverPoint/10);//all genes up until splice
    
    System.out.print("Unit test: the splice gene is at "+topGenesToCompare+", ");
    child.geneList.get(topGenesToCompare).print();
    
    //assert genomes of the same size
    assert(topParent.geneList.size()==bottomParent.geneList.size()&&topParent.geneList.size()==child.geneList.size());
    
    //\\assert genes are the same for top genes of child and Top parent
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
    
    //\\assert that the splice gene is correct part top parent, correct part bottom parent
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
        if(DEBUG_OUTPUT)System.out.println(i+"'s x"+j+":"+genArr[i].geneList.get(topGenesToCompare).xpoints[j]);
        if(DEBUG_OUTPUT)System.out.println(i+"'s y"+j+":"+genArr[i].geneList.get(topGenesToCompare).ypoints[j]);
      }
      alleleValue[i][6]=genArr[i].geneList.get(topGenesToCompare).r;
      alleleValue[i][7]=genArr[i].geneList.get(topGenesToCompare).g;
      alleleValue[i][8]=genArr[i].geneList.get(topGenesToCompare).b;
      alleleValue[i][9]=genArr[i].geneList.get(topGenesToCompare).a;
      if(DEBUG_OUTPUT)System.out.println(i+"'s r"+":"+genArr[i].geneList.get(topGenesToCompare).r);
      if(DEBUG_OUTPUT)System.out.println(i+"'s g"+":"+genArr[i].geneList.get(topGenesToCompare).g);
      if(DEBUG_OUTPUT)System.out.println(i+"'s b"+":"+genArr[i].geneList.get(topGenesToCompare).b);
      if(DEBUG_OUTPUT)System.out.println(i+"'s a"+":"+genArr[i].geneList.get(topGenesToCompare).a);
      if(DEBUG_OUTPUT)System.out.print("\n");      
    }
    
    int numberOfGenes=genArr[0].geneList.get(topGenesToCompare).NALLELE;// 10 alleles
    int[] spliceArr=new int[numberOfGenes];
    int binParent=0;
    
    for(int i=0;i<numberOfGenes;i++)// 10 alleles
    {
      if(i==withinGeneSplit){binParent=1;}
      spliceArr[i]=alleleValue[binParent][i];
      if(DEBUG_OUTPUT)System.out.println("spliceGene's "+i+":"+spliceArr[i]);
    }
    
    for(int i=0;i<numberOfGenes;i++)
    {
      int indexOfChild=2;
      assert(alleleValue[indexOfChild][i]==spliceArr[i]);
    }
    
    //\\assert genes are the same for bottom of child and bottomParent
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
      System.out.println("bottomParent:"+bottomX+" , child:"+childX);
      assert(bottomParent.geneList.get(i).r==child.geneList.get(i).r);
      assert(bottomParent.geneList.get(i).g==child.geneList.get(i).g);
      assert(bottomParent.geneList.get(i).b==child.geneList.get(i).b);
      assert(bottomParent.geneList.get(i).a==child.geneList.get(i).a);
    }
  }
  
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

  public static void main(String[] args)
  {
    new UnitTestMain();
  }
}