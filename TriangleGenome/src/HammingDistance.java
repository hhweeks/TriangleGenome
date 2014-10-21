   /****************************************************************************
   *HammingDistance
   *@author Hans Weeks
   *This static object has only 1 method, which is to calculate the Hamming distance
   *of 2 Genomes 
   ****************************************************************************/
public class HammingDistance
{    
  
  /****************************************************************************
   * calcDiff
   * Input:2 Genomes to compare Hamming distance of
   * Output:Hamming distance as an int
   * Description: compares each allele (element) of each gene in a Genome. For each
   *  value that differs, 1 is added to hamD. Two identical Genomes yield 0, two Genomes
   *  which differ at every element yield 2000 (10*200 genes)
   ****************************************************************************/
  public static long calcDiff(Genome gen1, Genome gen2)
  {
    int hamD=0;
    if(gen1.geneList.size()!=gen2.geneList.size())
    {
      int gen1Size=gen1.geneList.size();
      int gen2Size=gen2.geneList.size();
      System.out.println("genomes of different size");
      return -1;
    }
    for(int i=0;i<gen1.geneList.size();i++)
    {
      for(int j=0;j<gen1.geneList.get(0).npoints;j++)//should loop 3 times for 3 points
      {
        int gen1x=gen1.geneList.get(i).xpoints[j];
        int gen2x=gen2.geneList.get(i).xpoints[j];
        int gen1y=gen1.geneList.get(i).ypoints[j];
        int gen2y=gen2.geneList.get(i).ypoints[j];
        
        if(gen1x!=gen2x){hamD++;}
        if(gen1y!=gen2y){hamD++;}
      }
      
      int gen1r=gen1.geneList.get(i).r;
      int gen1g=gen1.geneList.get(i).g;
      int gen1b=gen1.geneList.get(i).b;
      int gen1a=gen1.geneList.get(i).a;
      
      int gen2r=gen2.geneList.get(i).r;
      int gen2g=gen2.geneList.get(i).g;
      int gen2b=gen2.geneList.get(i).b;
      int gen2a=gen2.geneList.get(i).a;
      
      if(gen1r!=gen2r){hamD++;}
      if(gen1g!=gen2g){hamD++;}
      if(gen1b!=gen2b){hamD++;}
      if(gen1a!=gen2a){hamD++;}
    }
    return hamD;    
  }
  
  public static int geneDiff(Gene gen1,Gene gen2){
	 int hamD=0;
	  for(int j=0;j<gen1.npoints;j++)//should loop 3 times for 3 points
      {
	  int gen1x=gen1.xpoints[j];
      int gen2x=gen2.xpoints[j];
      int gen1y=gen1.ypoints[j];
      int gen2y=gen2.ypoints[j];
      
      if(gen1x!=gen2x){hamD++;}
      if(gen1y!=gen2y){hamD++;}
    }
    
    int gen1r=gen1.r;
    int gen1g=gen1.g;
    int gen1b=gen1.b;
    int gen1a=gen1.a;
    
    int gen2r=gen2.r;
    int gen2g=gen2.g;
    int gen2b=gen2.b;
    int gen2a=gen2.a;
    
    if(gen1r!=gen2r){hamD++;}
    if(gen1g!=gen2g){hamD++;}
    if(gen1b!=gen2b){hamD++;}
    if(gen1a!=gen2a){hamD++;}
	  
	  return hamD;
	  
	  
  }
  
}

