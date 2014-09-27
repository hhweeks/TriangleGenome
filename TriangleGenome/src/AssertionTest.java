
public class AssertionTest {
	
	
	
	
	public static void main(String[] args){
		Genome oneGenome=new Genome(500,500);
		Genome twoGenome=new Genome(500,500);
		Genome threeGenome=new Genome(500,500);
		Genome fourGenome=new Genome(500,500);
		GenomeUtilities.setRandomGenome(oneGenome);
		Genome copyOfGenomeOne=GenomeUtilities.genomeCopy(oneGenome);
		
		GenomeUtilities.setRandomGenome(twoGenome);
		GenomeUtilities.setRandomGenome(threeGenome);
		GenomeUtilities.setRandomGenome(fourGenome);
		//oneGenome.geneList.add(new Gene());
		System.out.println(isValid(oneGenome));
		CrossOver.breed(oneGenome, twoGenome, threeGenome, fourGenome, 1004);
		System.out.println(crossOverTest(oneGenome, twoGenome, threeGenome, fourGenome));
		System.out.println(GenomeUtilities.hammingDistance(oneGenome, twoGenome));
		System.out.println(GenomeUtilities.hammingDistance(oneGenome, copyOfGenomeOne));
		System.out.println(threeGenome.geneList.size());
		isValid(threeGenome);
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

			
		}
		
		return true;
	}
	
	
	public static boolean crossOverTest(Genome papaGenome, Genome mamaGenome,
			Genome sonGenome, Genome daughterGenome){
		Genome papaTest=papaGenome;
		Genome mamaTest=mamaGenome;
		Genome sonTest=sonGenome;
		Genome daughterTest=daughterGenome;
				
		CrossOver.breed(papaGenome, mamaGenome, sonGenome, daughterGenome, 1004);
		
		assert papaTest==papaGenome;
		assert mamaTest==mamaGenome;
		assert sonTest==sonGenome;
		assert daughterTest==daughterGenome;
		assert !GenomeUtilities.genomeEqual(papaGenome, mamaGenome);
		assert sonGenome!=papaGenome;
		assert !GenomeUtilities.genomeEqual(sonGenome, papaGenome);
		assert daughterGenome!=papaGenome;
		assert !GenomeUtilities.genomeEqual(daughterGenome, papaGenome);
		assert sonGenome!=mamaGenome;
		assert !GenomeUtilities.genomeEqual(sonGenome, mamaGenome);
		assert daughterGenome!=mamaGenome;
		assert !GenomeUtilities.genomeEqual(daughterGenome, mamaGenome);
		
		
	return true	;
	}
	
	
	
	

}
