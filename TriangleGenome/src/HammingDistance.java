public class HammingDistance
{    
  public static long calcDiff(Genome gen1, Genome gen2)
  {
    long hamD=0;
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
}

