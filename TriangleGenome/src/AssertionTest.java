
public class AssertionTest {
	
	
	
	
	public static void main(String[] args){
		Genome oneGenome=new Genome(500,500);
		Genome twoGenome=new Genome(500,500);
		Genome threeGenome=new Genome(500,500);
		Genome fourGenome=new Genome(500,500);
		GenomeUtilities.setRandomGenome(oneGenome);
		//oneGenome.geneList.add(new Gene());
		System.out.print(isValid(oneGenome));
		CrossOver.breed(oneGenome, twoGenome, threeGenome, fourGenome, 1004);
		
		
	}
	
	
	public static boolean isValid(Genome myGenome){
		assert myGenome.geneList.size()==200;
		Gene gene;
		for(int i=0;i<200;i++){
			gene=myGenome.geneList.get(i);
			
			assert gene.npoints==3;
			assert gene.r>=0&&gene.r<=255;
			assert gene.g>=0&&gene.g<=255;
			assert gene.b>=0&&gene.b<=255;
			assert gene.a>=0&&gene.a<=255;
			
			//System.out.println("a in the Arr is:"+gene.elementArr[9]);
			gene.print();
			
		}
		
		return true;
	}
	
	

}
