import java.awt.Point;
import java.awt.image.BufferedImage;

public class UnitTestMain
{
  boolean DEBUG_OUTPUT=true;
  
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
      CrossOver.breed(gen1, gen2, genSon, genDot, 1000);
      hamD=HammingDistance.calcDiff(gen1, genSon);
      if(DEBUG_OUTPUT)System.out.println("the parent and child with crossover at 100:"+hamD);
      assert(hamD==1000);
            
      
      
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
