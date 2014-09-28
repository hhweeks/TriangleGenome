
public class CrossOver {

	public CrossOver() {

	}

	public static void breed(Genome papaGenome, Genome mamaGenome,
			Genome sonGenome, Genome daughterGenome, int crossPoint) {
		
		int genecross = crossPoint / 10;
		int genesplit = crossPoint % 10;

		System.out.println(crossPoint+";"+genecross+";"+genesplit);
		sonGenome.geneList.clear();
		sonGenome.geneList
				.addAll(papaGenome.geneList.subList(0, Math.max(genecross,0)));

		sonGenome.geneList.add(geneSplice(papaGenome.geneList.get(genecross),
				mamaGenome.geneList.get(genecross), genesplit));

		sonGenome.geneList.addAll(mamaGenome.geneList.subList(genecross+1,
				mamaGenome.geneList.size()));

		daughterGenome.geneList.clear();
		daughterGenome.geneList.addAll(mamaGenome.geneList.subList(0,
				Math.max(genecross,0)));

		daughterGenome.geneList.add(geneSplice(
				mamaGenome.geneList.get(genecross),
				papaGenome.geneList.get(genecross), genesplit));

		daughterGenome.geneList.addAll(papaGenome.geneList.subList(
				genecross+1, mamaGenome.geneList.size()));

	}

	public static Gene geneSplice(Gene bottomGene, Gene topGene, int genesplit) {
		Gene gene = new Gene();
		switch (genesplit) {
		case 0:
			return topGene;
		case 1: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.ypoints[0] = topGene.ypoints[0];
			gene.xpoints[1] = topGene.xpoints[1];
			gene.ypoints[1] = topGene.ypoints[1];
			gene.xpoints[2] = topGene.xpoints[2];
			gene.ypoints[2] = topGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 2: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.ypoints[0] = bottomGene.ypoints[0];			
			gene.xpoints[1] = topGene.xpoints[1];
			gene.ypoints[1] = topGene.ypoints[1];
			gene.xpoints[2] = topGene.xpoints[2];
			gene.ypoints[2] = topGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 3: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.ypoints[1] = topGene.ypoints[1];
			gene.xpoints[2] = topGene.xpoints[2];
			gene.ypoints[2] = topGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 4: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.xpoints[2] = topGene.xpoints[2];
			gene.ypoints[2] = topGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 5: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.xpoints[2] = bottomGene.xpoints[2];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.ypoints[2] = topGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 6: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.xpoints[2] = bottomGene.xpoints[2];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.ypoints[2] = bottomGene.ypoints[2];
			gene.r = topGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 7: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.xpoints[2] = bottomGene.xpoints[2];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.ypoints[2] = bottomGene.ypoints[2];
			gene.r = bottomGene.r;
			gene.g = topGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 8: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.xpoints[2] = bottomGene.xpoints[2];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.ypoints[2] = bottomGene.ypoints[2];
			gene.r = bottomGene.r;
			gene.g = bottomGene.g;
			gene.b = topGene.b;
			gene.a = topGene.a;
			break;
		}
		case 9: {
			gene.xpoints[0] = bottomGene.xpoints[0];
			gene.xpoints[1] = bottomGene.xpoints[1];
			gene.xpoints[2] = bottomGene.xpoints[2];
			gene.ypoints[0] = bottomGene.ypoints[0];
			gene.ypoints[1] = bottomGene.ypoints[1];
			gene.ypoints[2] = bottomGene.ypoints[2];
			gene.r = bottomGene.r;
			gene.g = bottomGene.g;
			gene.b = bottomGene.b;
			gene.a = topGene.a;
			break;
		}
		}
		return gene;

	}

}
