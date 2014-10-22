import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

public class HillClimber extends Thread {
	private final Object PAUSE_MONITOR = new Object();
	private volatile boolean pauseThreadFlag = false;
	public Tribe tribe;
	static Random rand = new Random();
	public BufferedImage image;
	public Genome genome;
	boolean repeat;
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

	public HillClimber(BufferedImage img, Genome myGenome, int steps) {
		image = img;
		repeat = false;
		gradientClimb = true;
		genome = myGenome;
		nSteps = steps;

	}

	public void run() {
		// gradientClimb=true;
		// climbStep(genome);
		gradientClimb = false;
		for (int i = 0; i < nSteps; i++) {
			climbStep(genome);
			if (tribe != null) {
				if(tribe.imagePanel.drawGenome==genome)tribe.imagePanel.triangleWindowUpdate();
				tribe.checkForPaused();
			}
		}
		if (tribe != null) {
			int sigma=tribe.genomeList.size()/2;
	        tribe.generateFitScores();
			tribe.interCrossRoutine(sigma);
		}
	}

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
																// mutateAlleleValue
																// to max when
																// mutate is
																// called
				}
				if (mutateAlleleValue + shiftAmount < 0) {
					shiftAmount = -mutateAlleleValue;// will set
														// mutateAlleleValue to
														// 0 when mutate is
														// called
				}

				// double scale=.7;
				//
				// BufferedImage
				// scaledImage=GenomeUtilities.copyImage(masterImage);
				// int w=(int) (scaledImage.getWidth()*scale);
				// int h=(int) (scaledImage.getHeight()*scale);
				// Image tempIm=scaledImage.getScaledInstance(w, h, 0);
				// myGenome.scaledImage=new BufferedImage(w, h,
				// BufferedImage.TYPE_INT_RGB);
				// myGenome.scaledImage.getGraphics().drawImage(tempIm, 0, 0,
				// null);

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
			// gradient hill climbing
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

	public void revertGenome(Gene mutateGene, int allele, int shiftAmount) {
		Mutate.exposeToRadiation(lastGene, lastAllele, -lastShift);
	}

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
															// mutateAlleleValue
															// to max when
															// mutate is called
			}
			if (mutateAlleleValue + shiftAmount < 0) {
				shiftAmount = -mutateAlleleValue;// will set mutateAlleleValue
													// to 0 when mutate is
													// called
			}
			Mutate.exposeToRadiation(mutateGene, allele, shiftAmount);
			previousScore = currentScore;
			int w = image.getWidth();
			int h = image.getHeight();
			BufferedImage scaledImage = new BufferedImage((int) (w / 1.5),
					(int) (h / 1.5), BufferedImage.TYPE_INT_RGB);
			currentScore = assignScore(myGenome);
		}
		revertGenome(lastGene, lastAllele, lastShift);
	}

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

	public Gene getGene(Genome myGenome) {
		int randomGeneInt;
		Gene myGene;
		randomGeneInt = rand.nextInt(myGenome.NUM_GENES);
		myGene = myGenome.geneList.get(randomGeneInt);
		// do
		// {
		// randomGeneInt=rand.nextInt(myGenome.NUM_GENES);
		// myGene=myGenome.geneList.get(randomGeneInt);
		// }while(getGeneHelper(myGene));

		return myGene;
	}

	public boolean getGeneHelper(Gene myGene) {
		boolean findAnotherGene = true;

		int xmax = Math.max(myGene.xpoints[0], myGene.xpoints[1]);
		xmax = Math.max(myGene.xpoints[2], xmax);

		int xmin = Math.min(myGene.xpoints[0], myGene.xpoints[1]);
		xmin = Math.min(myGene.xpoints[2], xmin);

		int ymax = Math.max(myGene.ypoints[0], myGene.ypoints[1]);
		ymax = Math.max(myGene.ypoints[2], ymax);

		int ymin = Math.min(myGene.ypoints[0], myGene.ypoints[1]);
		ymin = Math.min(myGene.ypoints[2], ymin);

		int[] xsample = new int[5];// change this to a variable amount
		int[] ysample = new int[5];

		for (int i = 0; i < 5; i++) {
			do {
				xsample[i] = rand.nextInt(xmax - xmin) + xmin;
				ysample[i] = rand.nextInt(ymax - ymin) + ymin;
			} while (myGene.contains(xsample[i], ysample[i]));
		}

		return findAnotherGene;
	}

	public int getAllele(Gene myGene) {
		int myAlleleIndex = rand.nextInt(myGene.NALLELE + 2);// 10 alleles+2 for
																// move all x
																// and move all
																// y
		return myAlleleIndex;
	}

	public void switchLayers(Gene gene1, Gene gene2) {
		Gene holder = GenomeUtilities.geneCopy(gene1);
		gene1 = GenomeUtilities.geneCopy(gene2);
		gene2 = GenomeUtilities.geneCopy(holder);
	}

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

	public long assignScore(Genome myGenome)// calls statistics, program calls
											// this 5 times, and it is
											// frequently rewritten in 5 places
											// if not centralized here//TODO
	{
		// BufferedImage genomeImage=GenomeUtilities.getBufferedImage(genome);
		// long score=Statistics.getFitScore(genomeImage,masterImage);

		int scale = 4;

		BufferedImage scaledImage = GenomeUtilities.scaleImage(image, scale);
		long score = Statistics.getFitScore(
				GenomeUtilities.getScaledBufferedImage(myGenome, 4),
				scaledImage);

		return score;
	}

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

	public void pauseThread() throws InterruptedException {
		pauseThreadFlag = true;
	}

	public void resumeThread() {
		synchronized (PAUSE_MONITOR) {
			pauseThreadFlag = false;
			PAUSE_MONITOR.notify();
		}
	}
}
