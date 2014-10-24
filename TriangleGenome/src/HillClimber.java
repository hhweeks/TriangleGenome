import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

/****************************************************************************
 * HillClimber
 *
 * @author Hans Weeks
 * @author Paige Romero
 * 
 ****************************************************************************/
public class HillClimber extends Thread {
	private final Object PAUSE_MONITOR = new Object();
	private volatile boolean pauseThreadFlag = false;
	public Tribe tribe;
	static Random rand = new Random();
	public BufferedImage image;
	public Genome genome;
	boolean repeat;
	boolean doBreed;
	Gene lastGene;
	int maxBound;
	boolean gradientClimb;
	// I need to to define input parameters based on which allele is which.
	int lastAllele;
	int lastShift;
	int nSteps;
	public final int ALLELE_VALUES = 12;

	public HillClimber(BufferedImage img) {
		image = img;
		repeat = false;
		gradientClimb = true;

	}

	/****************************************************************************
	 * Constructor Input: BufferedImage master image, Genome to hillclimb, int
	 * steps, number of times to mutate before calling crossover
	 ****************************************************************************/
	public HillClimber(BufferedImage img, Genome myGenome, int steps) {
		image = img;
		repeat = false;
		gradientClimb = true;
		genome = myGenome;
		nSteps = steps;
	}

	/****************************************************************************
	 * run Input:none Output:none Description: calls climbStep to mutate a genes
	 * in the Genome nSteps times. When climb is done being called, sets a flag
	 * to tell the Tribe to crossover
	 ****************************************************************************/
	public void run() {
		gradientClimb = false;
		for (int i = 0; i < nSteps; i++) {
			climbStep(genome);
			if (tribe != null) {
				if (tribe.imagePanel.drawGenome == genome)
					tribe.imagePanel.triangleWindowUpdate();
				checkForPaused();
			}
		}
		if (tribe != null && doBreed) {
			int sigma = tribe.genomeList.size() / 2;
			tribe.generateFitScores();
			tribe.interCrossRoutine(sigma);
		}
	}

	/****************************************************************************
	 * climbSte Input:Genome to modify Output:boolean did the fitness improve?
	 * Description:selects a Gene to mutate, selects an element "allele" to
	 * mutate, selects an amount to mutate that gene, tests to make sure the
	 * resulting value of the allele would still be in bounds. then calls mutate
	 * and helper functions to either repeat the mutation (if lowered fitness
	 * score) or to undo the mutation if it raised fitness score.
	 ****************************************************************************/
	public boolean climbStep(Genome myGenome) {
		genome = myGenome;
		// mutate random gene at random allele
		if (!gradientClimb) {
			// System.out.println("randomClimb");
			Gene mutateGene = getGene(myGenome);
			int mutateAlleleIndex = getAllele(mutateGene);
			if (mutateAlleleIndex == ALLELE_VALUES - 1)// the change layers
			// index
			{
				Gene mutateGene2 = getGene(myGenome);
				long startScore = assignScore(myGenome);
				switchLayers(mutateGene, mutateGene2);
				long endScore = assignScore(myGenome);
				if (startScore < endScore) {
					switchLayers(mutateGene, mutateGene2);// switch them back if
					// it wasn't an
					// improvement
				}
				return endScore < startScore;
			} else {
				maxBound = getAlleleBounds(myGenome, mutateAlleleIndex);
				int shiftAmount = rand.nextInt(maxBound / 5);// shift by up to
				// 20%
				shiftAmount -= (maxBound / 10);// subtract, to leave shift by
				// +/- 10

				// special case for moving all x or all y vertices, we need
				// mutateAlleleValue to be maxX/minX depending on if shiftAmount
				// is + or -
				int mutateAlleleValue;
				if (mutateAlleleIndex == 10) {
					if (shiftAmount > 0) {
						mutateAlleleValue = GenomeUtilities
								.maxGenesDim(mutateGene.xpoints);
					} else {
						mutateAlleleValue = GenomeUtilities
								.minGenesDim(mutateGene.xpoints);
					}
				} else if (mutateAlleleIndex == 11) {
					if (shiftAmount > 0) {
						mutateAlleleValue = GenomeUtilities
								.maxGenesDim(mutateGene.ypoints);
					} else {
						mutateAlleleValue = GenomeUtilities
								.minGenesDim(mutateGene.ypoints);
					}
				} else {
					mutateAlleleValue = Mutate.getAlleleValue(mutateGene,
							mutateAlleleIndex);
				}

				if (mutateAlleleValue + shiftAmount > maxBound) {
					shiftAmount = maxBound - mutateAlleleValue;// this will set
				}
				if (mutateAlleleValue + shiftAmount < 0) {
					shiftAmount = -mutateAlleleValue;// will set
				}

				long startScoreLocal = assignScore(myGenome);
				Mutate.exposeToRadiation(mutateGene, mutateAlleleIndex,
						shiftAmount);
				long endScoreLocal = assignScore(myGenome);

				lastGene = mutateGene;
				lastAllele = mutateAlleleIndex;
				lastShift = shiftAmount;

				if (endScoreLocal > startScoreLocal) {
					revertGenome(lastGene, lastAllele, lastShift);
				} else if (endScoreLocal < startScoreLocal)
					repeatMutation(myGenome, lastGene, lastAllele, lastShift,
							maxBound, startScoreLocal, endScoreLocal);

				return endScoreLocal < startScoreLocal;
			}
		} else {
			long startTime = System.currentTimeMillis();
			gradientClimb(myGenome);
			long endTime = System.currentTimeMillis();
			System.out.println("gradientStep:" + (endTime - startTime));
			startTime = System.currentTimeMillis();
			long testFit = Statistics.getFitScore(
					GenomeUtilities.getBufferedImage(genome), image);
			endTime = System.currentTimeMillis();

			System.out.println("fullStatStep:" + (endTime - startTime)
					+ "score:" + testFit);

			return false;
		}
	}

	/****************************************************************************
	 * reverGenome Input:Gene to undu, allele modified and amount modified by
	 * Output:none Description:helper function to undo the mutation on a Gene
	 * after a bad mutation
	 ****************************************************************************/
	public void revertGenome(Gene mutateGene, int allele, int shiftAmount) {
		Mutate.exposeToRadiation(lastGene, lastAllele, -lastShift);
	}

	/****************************************************************************
	 * repeatMutation Input:requires all the variable used to make the mutation
	 * including the Gene, containing Genome, the allele, how much it was
	 * mutated, its start and end scores, the amount shifted, and the maximum
	 * amount the allele can be shifted before going out of bounds Output:none
	 * Description: mutates the Gene in a loop until the mutation no longer
	 * improves its fitness, then removes the last mutation (which was harmful)
	 ****************************************************************************/
	public void repeatMutation(Genome myGenome, Gene mutateGene, int allele,
			int shiftAmount, int maxBound, long startScore, long endScore) {
		long previousScore = startScore;
		long currentScore = endScore;
		int mutateAlleleValue;

		while (currentScore < previousScore) {
			// ///////////////////////////////////////////////////////////////////////////////////////
			// special case for moving all x or all y vertices, we need
			// mutateAlleleValue to be maxX/minX depending on if shiftAmount is
			// + or -
			if (allele == 10) {
				if (shiftAmount > 0) {
					mutateAlleleValue = GenomeUtilities
							.maxGenesDim(mutateGene.xpoints);
				} else {
					mutateAlleleValue = GenomeUtilities
							.minGenesDim(mutateGene.xpoints);
				}
			} else if (allele == 11) {
				if (shiftAmount > 0) {
					mutateAlleleValue = GenomeUtilities
							.maxGenesDim(mutateGene.ypoints);
				} else {
					mutateAlleleValue = GenomeUtilities
							.minGenesDim(mutateGene.ypoints);
				}
			} else {
				mutateAlleleValue = Mutate.getAlleleValue(mutateGene, allele);
			}
			// ///////////////////////////////////////////////////////////////////////////////////////

			if (mutateAlleleValue + shiftAmount > maxBound) {
				shiftAmount = maxBound - mutateAlleleValue;// this will set
				// mutateAlleleValue to max when mutate is called
			}
			if (mutateAlleleValue + shiftAmount < 0) {
				shiftAmount = -mutateAlleleValue;// will set mutateAlleleValue
				// to 0 when mutate is called
			}
			Mutate.exposeToRadiation(mutateGene, allele, shiftAmount);
			previousScore = currentScore;
			currentScore = assignScore(myGenome);
		}
		revertGenome(lastGene, lastAllele, lastShift);
	}

	/****************************************************************************
	 * gradientClimb Input:Genome to modify Output:none Description:unused in
	 * turned-in version. This method served to calculate how quickly fitness
	 * improved with modifications to each gene, and use that to direct mutation
	 ****************************************************************************/
	public void gradientClimb(Genome myGenome) {
		BufferedImage scaledImage = GenomeUtilities.scaleImage(image, 4);
		int count = 0;
		long startScore = Statistics.getFitScore(
				GenomeUtilities.getBufferedImage(myGenome), image);

		long startScaleScore = Statistics.getFitScore(
				GenomeUtilities.getScaledBufferedImage(myGenome, 4),
				scaledImage);

		// gradient vector
		int[] gradient = new int[2000];
		Gene currentGene = new Gene();
		int allele;

		int startIndex = 0;// rand.nextInt(1900);
		int endIndex = 2000;// startIndex+500;

		for (int reali = startIndex; reali < endIndex; reali++) {
			int i = reali % 2000;

			currentGene = myGenome.geneList.get(i / 10);

			allele = i % 10;
			// if(allele==0){geneTest=GenomeUtilities.geneCopy(currentGene);shiftcount=0;}
			shiftGene(currentGene, allele, 1);

			GenomeUtilities.checkValues(currentGene, image.getWidth(),
					image.getHeight());
			// todo randomly choose up or down first.
			long endupScaleScore = Statistics.getFitScore(
					GenomeUtilities.getScaledBufferedImage(myGenome, 4),
					scaledImage);
			if (endupScaleScore < startScaleScore) {
				gradient[i] = 1;

			}
			// else{gradient[i]=0;}

			// System.out.println("shiftUp  "+i+"->end:"+endupScaleScore+" start:"+startScaleScore+" gradient:"+gradient[i]);

			shiftGene(currentGene, allele, -2);
			GenomeUtilities.checkValues(currentGene, image.getWidth(),
					image.getHeight());
			long endDownScaleScore = Statistics.getFitScore(
					GenomeUtilities.getScaledBufferedImage(myGenome, 4),
					scaledImage);
			if ((endDownScaleScore < startScaleScore)
					&& (endDownScaleScore < endupScaleScore)) {
				gradient[i] = -1;
			}

			shiftGene(currentGene, allele, 1);
			GenomeUtilities.checkValues(currentGene, image.getWidth(),
					image.getHeight());
			// System.out.println("shiftDown"+i+"->end:"+endDownScaleScore+" start:"+startScaleScore+" gradient:"+gradient[i]);
			// if(HammingDistance.geneDiff(currentGene, geneTest)!=0){
			// System.out.println("geneDiff:"+allele+"i:"+i+"shiftcount"+shiftcount);
			// }

		}

		// after the gradient has been measured for the scaled Image we
		// move the image along the gradient until no more benefit is gained
		startScore = -1;
		long endScore = -1;
		count = 0;
		while (startScore > endScore || startScore == -1) {

			if (endScore == -1) {
				startScore = Statistics.getFitScore(
						GenomeUtilities.getBufferedImage(myGenome), image);
			} else {
				startScore = endScore;
			}

			for (int i = 0; i < 2000; i++) {
				currentGene = myGenome.geneList.get(i / 10);
				allele = i % 10;

				if (gradient[i] != 0) {
					shiftGene(currentGene, allele, gradient[i]);
					GenomeUtilities.checkValues(currentGene, image.getWidth(),
							image.getHeight());
					count++;

				}

			}

			// long startTime = System.currentTimeMillis();
			endScore = Statistics.getFitScore(
					GenomeUtilities.getBufferedImage(myGenome), image);
			// System.out.println(System.currentTimeMillis() - startTime);

			// System.out.println("gradient step improvment:"+(startScore -
			// endScore));

		}
		if (startScore != -1) {
			for (int i = 0; i < 2000; i++) {
				currentGene = myGenome.geneList.get(i / 10);
				allele = i % 10;
				if (gradient[i] != 0) {
					shiftGene(currentGene, allele, -gradient[i]);
					GenomeUtilities.checkValues(currentGene, image.getWidth(),
							image.getHeight());
				}
			}
		}
		System.out.println("N corrections" + count);
	}

	/****************************************************************************
	 * shiftGene Input:currentGene, allele to mod in Gene and how much to shift
	 * Output:none Description:helper function of Gradient to modify alleles
	 ****************************************************************************/
	private void shiftGene(Gene currentGene, int allele, int shiftAmount) {
		switch (allele) {
		// mutate coordinates
		case 0:
			currentGene.xpoints[0] += shiftAmount;
			break;
		case 1:
			currentGene.ypoints[0] += shiftAmount;
			break;
		case 2:
			currentGene.xpoints[1] += shiftAmount;
			break;
		case 3:
			currentGene.ypoints[1] += shiftAmount;
			break;
		case 4:
			currentGene.xpoints[2] += shiftAmount;
			break;
		case 5:
			currentGene.ypoints[2] += shiftAmount;
			break;
		// mutate colors
		case 6:
			currentGene.r += shiftAmount;
			break;
		case 7:
			currentGene.g += shiftAmount;
			break;
		case 8:
			currentGene.b += shiftAmount;
			break;
		case 9:
			currentGene.a += shiftAmount;
			break;
		}
	}

	/****************************************************************************
	 * getGene Input:Genome to select from Output:Gene selected
	 * Description:selects a gene from the Genome at random
	 ****************************************************************************/
	public Gene getGene(Genome myGenome) {
		int randomGeneInt;
		Gene myGene;
		randomGeneInt = rand.nextInt(Genome.NUM_GENES);
		myGene = myGenome.geneList.get(randomGeneInt);

		return myGene;
	}

	// /****************************************************************************
	// * getGeneHelper
	// * Input:Gene
	// * Output:
	// * Description:
	// ****************************************************************************/
	// public boolean getGeneHelper(Gene myGene)
	// {
	// boolean findAnotherGene = true;
	//
	// int xmax = Math.max(myGene.xpoints[0], myGene.xpoints[1]);
	// xmax = Math.max(myGene.xpoints[2], xmax);
	//
	// int xmin = Math.min(myGene.xpoints[0], myGene.xpoints[1]);
	// xmin = Math.min(myGene.xpoints[2], xmin);
	//
	// int ymax = Math.max(myGene.ypoints[0], myGene.ypoints[1]);
	// ymax = Math.max(myGene.ypoints[2], ymax);
	//
	// int ymin = Math.min(myGene.ypoints[0], myGene.ypoints[1]);
	// ymin = Math.min(myGene.ypoints[2], ymin);
	//
	// int[] xsample = new int[5];// change this to a variable amount
	// int[] ysample = new int[5];
	//
	// for (int i = 0; i < 5; i++)
	// {
	// do
	// {
	// xsample[i] = rand.nextInt(xmax - xmin) + xmin;
	// ysample[i] = rand.nextInt(ymax - ymin) + ymin;
	// } while (myGene.contains(xsample[i], ysample[i]));
	// }
	//
	// return findAnotherGene;
	// }

	/****************************************************************************
	 * getAllele Input:current Gene to select from Output:int, represents
	 * element selected Description:selects random element to change in the
	 * allele
	 ****************************************************************************/
	public int getAllele(Gene myGene) {
		int myAlleleIndex = rand.nextInt(Gene.NALLELE + 2);// 10 alleles+2 for
															// move all x and
															// move all y
		return myAlleleIndex;
	}

	/****************************************************************************
	 * switchLayers Input:2 genes to switch Output:none Description:switches the
	 * values of 2 genes by copying their data
	 ****************************************************************************/
	public void switchLayers(Gene gene1, Gene gene2) {
		Gene holder = GenomeUtilities.geneCopy(gene1);
		gene1 = GenomeUtilities.geneCopy(gene2);
		gene2 = GenomeUtilities.geneCopy(holder);
	}

	/****************************************************************************
	 * getAlleleBounds Input:Genome and int allele Output:int max value
	 * Description:returns max value allowed for that allele
	 ****************************************************************************/
	public int getAlleleBounds(Genome myGenome, int allele) {
		int maxBound = 0;
		int colorMax = 255;
		switch (allele) {
		case 0:
			maxBound = myGenome.IMG_WIDTH;
			break;
		case 1:
			maxBound = myGenome.IMG_HEIGHT;
			break;
		case 2:
			maxBound = myGenome.IMG_WIDTH;
			break;
		case 3:
			maxBound = myGenome.IMG_HEIGHT;
			break;
		case 4:
			maxBound = myGenome.IMG_WIDTH;
			break;
		case 5:
			maxBound = myGenome.IMG_HEIGHT;
			break;
		case 6:
			maxBound = colorMax;
			break;
		case 7:
			maxBound = colorMax;
			break;
		case 8:
			maxBound = colorMax;
			break;
		case 9:
			maxBound = colorMax;
			break;
		case 10:
			maxBound = myGenome.IMG_WIDTH;
			break;
		case 11:
			maxBound = myGenome.IMG_HEIGHT;
			break;
		}
		return maxBound;
	}

	/****************************************************************************
	 * assignScore Input:Genome to score Output:fitness score, long
	 * Description:calls scaled scoring on the genome, which ocurrs in many
	 * places in this class and so is centralized here.
	 ****************************************************************************/
	public long assignScore(Genome myGenome) {
		// BufferedImage genomeImage=GenomeUtilities.getBufferedImage(genome);
		// long score=Statistics.getFitScore(genomeImage,masterImage);

		int scale = 4;

		BufferedImage scaledImage = GenomeUtilities.scaleImage(image, scale);
		long score = Statistics.getFitScore(
				GenomeUtilities.getScaledBufferedImage(myGenome, 4),
				scaledImage);

		return score;
	}

	/****************************************************************************
	 * getLocalFit Input:Gene, allele and amount shifted Output:long fitness
	 * score Description:used to calculate fitness score of a small peice of the
	 * image not used in the completed version of the project
	 ****************************************************************************/
	public long getLocalFit(Gene gene, int allele, int shiftMagnitude) {
		BufferedImage genomeImage = GenomeUtilities.getBufferedImage(genome);
		Raster genomeRaster = genomeImage.getRaster();
		Raster raster = image.getRaster();
		int red, blue, green;

		long sum = 0;

		int minX = Math.min(gene.xpoints[0],
				Math.min(gene.xpoints[1], gene.xpoints[2]));
		int maxX = Math.max(gene.xpoints[0],
				Math.max(gene.xpoints[1], gene.xpoints[2]));
		int minY = Math.min(gene.ypoints[0],
				Math.min(gene.ypoints[1], gene.ypoints[2]));
		int maxY = Math.max(gene.ypoints[0],
				Math.max(gene.ypoints[1], gene.ypoints[2]));

		// adds to boundaries based on affected variables
		//
		if (allele < 6 && shiftMagnitude > 0) {
			if (allele % 2 == 0) {
				minX = Math.max(minX - shiftMagnitude, 0);
				maxX = Math.min(minX + shiftMagnitude, image.getWidth());
				// System.out.println("xchange");

			} else {
				minY = Math.max(minY - shiftMagnitude, 0);
				maxY = Math.min(maxX + shiftMagnitude, image.getHeight());
				// System.out.println("ychange");
			}

		}

		// prevent streight lines
		// System.out.println(shiftMagnitude+"->"+allele+";"+maxX+";"+minX+";"+maxY+";"
		// + minY);

		if (minX == maxX) {
			minX--;
			maxX++;
		}
		if (minY == maxY) {
			minY--;
			maxY++;
		}

		long runsum = 0;

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {

				int[] pixel = { 0, 0, 0 };
				int[] genomePixel = { 0, 0, 0 };
				// System.out.println(x+";"+y);
				raster.getPixel(x, y, pixel);
				genomeRaster.getPixel(x, y, genomePixel);

				red = (pixel[0] - genomePixel[0]);
				red = red * red;
				green = pixel[1] - genomePixel[1];
				green = green * green;
				blue = pixel[2] - genomePixel[2];
				blue = blue * blue;

				sum = red + green + blue;
				runsum += sum;
				// System.out.println(red+";"+green+";"+blue);

			}

		}
		// returns euclidian distance.
		return runsum / ((maxX - minX) * (maxY - minY));
	}

	/****************************************************************************
	 * checkForPaused
	 ****************************************************************************/
	private void checkForPaused() {
		synchronized (PAUSE_MONITOR) {
			while (pauseThreadFlag) {
				try {
					PAUSE_MONITOR.wait();
				} catch (Exception e) {
				}
			}
		}
	}

	/****************************************************************************
	 * pauseThread
	 ****************************************************************************/
	public void pauseThread() throws InterruptedException {
		synchronized (PAUSE_MONITOR) {
			pauseThreadFlag = true;
		}
	}

	/****************************************************************************
	 * resumeThread
	 ****************************************************************************/
	public void resumeThread() {
		synchronized (PAUSE_MONITOR) {
			pauseThreadFlag = false;
			PAUSE_MONITOR.notify();
		}
	}
}
