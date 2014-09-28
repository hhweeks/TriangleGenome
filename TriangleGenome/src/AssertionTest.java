
public class AssertionTest {
	
	
	
	
	public static void main(String[] args){
		
		
		//generate 4 empty genomes
		Genome oneGenome=new Genome(500,500);
		Genome twoGenome=new Genome(500,500);
		Genome threeGenome=new Genome(500,500);
		Genome fourGenome=new Genome(500,500);
		
		//randomize the first two genomes and make a copy with different a different
		//address and identical genes
		GenomeUtilities.setRandomGenome(oneGenome);
		GenomeUtilities.setRandomGenome(twoGenome);
		Genome copyOfGenomeOne=GenomeUtilities.genomeCopy(oneGenome);
		
		//Assertion test of random genome
		isValid(oneGenome);
		CrossOver.breed(oneGenome, twoGenome, threeGenome, fourGenome, 1004);
		//Assertion test of cross overed genomes
		for(int i=0;i<200;i++){
			
			System.out.println(i+"   :"+oneGenome.geneList.get(i));
			System.out.println(i+"   :"+threeGenome.geneList.get(i));
			
		}
		crossOverTest(oneGenome, twoGenome, threeGenome, fourGenome);
		isValid(threeGenome);
		
		
		System.out.println("Hamming score for two random genomes:  "+GenomeUtilities.hammingDistance(oneGenome, twoGenome));
		System.out.println("Hamming score for two identical genomes with diffent addresses:  "+GenomeUtilities.hammingDistance(oneGenome, copyOfGenomeOne));
		System.out.println("Hamming score for a parent and child after a cross over:  "+GenomeUtilities.hammingDistance(oneGenome, threeGenome));

		
	}
	
	
	public static void isValid(Genome myGenome){
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

	}
	
	
	public static void crossOverTest(Genome papaGenome, Genome mamaGenome,
			Genome sonGenome, Genome daughterGenome){
		Genome papaTest=papaGenome;
		Genome mamaTest=mamaGenome;
		Genome sonTest=sonGenome;
		Genome daughterTest=daughterGenome;
				
		//CrossOver.breed(papaGenome, mamaGenome, sonGenome, daughterGenome, 0);
		
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

	}
	
	
	
	

}
