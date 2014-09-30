import java.util.ArrayList;


public class CrossOver {

	public CrossOver() {

	}

	public static void breed(Genome papaGenome, Genome mamaGenome,Genome sonGenome, Genome daughterGenome, int crossPoint)
	{
		int genecross = crossPoint / 10;
		int genesplit = crossPoint % 10;

		sonGenome.geneList.clear();
		sonGenome.geneList.addAll(papaGenome.geneList.subList(0, genecross));
		sonGenome.geneList.add(geneSplice(papaGenome.geneList.get(genecross),mamaGenome.geneList.get(genecross), genesplit));
		for(int i=genecross+1;i<mamaGenome.geneList.size();i++)
		{
		  Gene tempGene=GenomeUtilities.geneCopy(mamaGenome.geneList.get(i));
		  sonGenome.geneList.add(tempGene);
		}
//		sonGenome.geneList.addAll(mamaGenome.geneList.subList(genecross,mamaGenome.geneList.size()-1));

		daughterGenome.geneList.clear();
		daughterGenome.geneList.addAll(mamaGenome.geneList.subList(0,genecross));
		daughterGenome.geneList.add(geneSplice(mamaGenome.geneList.get(genecross),papaGenome.geneList.get(genecross), genesplit));
		for(int i=genecross+1;i<papaGenome.geneList.size();i++)
    {
      Gene tempGene=GenomeUtilities.geneCopy(papaGenome.geneList.get(i));
      daughterGenome.geneList.add(tempGene);
    }
//		daughterGenome.geneList.addAll(papaGenome.geneList.subList(genecross, mamaGenome.geneList.size()-1));

	}

public static Gene geneSplice(Gene topParent, Gene bottomParent, int crossPoint) {
    
    int topGenesToCompare=(crossPoint/10);//all genes up until splice
    
    int withinGeneSplit=crossPoint%10;
    Gene[] genArr={topParent,bottomParent};
    int[][] alleleValue=new int[2][genArr[0].NALLELE];//3*10
    
    //this loop grabs the 10 alleles from each parent
    for(int i=0;i<genArr.length;i++)// less than 2
    {
      for(int j=0;j<genArr[i].NPOINTS;j++)//get xpoints and ypoints, 3 of each and add them to the array
      {
        alleleValue[i][2*j]=genArr[i].xpoints[j];
        alleleValue[i][(2*j)+1]=genArr[i].ypoints[j];
      }
      alleleValue[i][6]=genArr[i].r;
      alleleValue[i][7]=genArr[i].g;
      alleleValue[i][8]=genArr[i].b;
      alleleValue[i][9]=genArr[i].a;      
    }
    
    int numberOfGenes=genArr[0].NALLELE;// 10 alleles
    int[] spliceArr=new int[numberOfGenes];
    int binParent=0;
    for(int i=0;i<numberOfGenes;i++)// 10 alleles
    {
      if(i==withinGeneSplit){binParent=1;}
      spliceArr[i]=alleleValue[binParent][i];
    }
    
    Gene childSplicedGene=new Gene();
    /////////////////////////////////////
    for(int j=0;j<childSplicedGene.NPOINTS;j++)//get xpoints and ypoints, 3 of each and add them to the array
    {
      childSplicedGene.xpoints[j]=spliceArr[2*j];
      childSplicedGene.ypoints[j]=spliceArr[(2*j)+1];
    }
    childSplicedGene.r=spliceArr[6];
    childSplicedGene.g=spliceArr[7];
    childSplicedGene.b=spliceArr[8];
    childSplicedGene.a=spliceArr[9]; 
    /////////////////////////////////////////    
    return childSplicedGene;
  }

}