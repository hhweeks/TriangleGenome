import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/****************************************************************************
 *Tribe
 *@author Hans Weeks
 *@author Paige Romero
 *  object holds a list of Genomes to call hillclimbing and crossover on.
 *  Extends thread. Values of GenomeList size for starting contained in
 *  TriangleGenomeGUI, and end size contained locally
 ****************************************************************************/
public class Tribe extends Thread
{
  Random rand = new Random();
  volatile ArrayList<Genome> genomeList = new ArrayList<>();
  BufferedImage masterImage;
  ImageContainer imagecontainer;
  private final Object GUI_INITIALIZATION_MONITOR = new Object();
  public final long GENEDIFFERENCELIMIT = 500;
  public volatile boolean pauseThreadFlag = false;
  long timeStamp;
  public int tribeId;
  public static final int ENDINGTRIBESIZE = 10;
  public volatile boolean climbLoop = true;
  int startingTribeSize;
  TriangleGenomeGUI imagePanel;

  /****************************************************************************
   * Constructor
   * Input:BufferedImage of master image, GUI object that is parent of this tribe thread
   * Description: Creates a new object to hold Genome in its Genome list.
   ****************************************************************************/
  public Tribe(BufferedImage image, TriangleGenomeGUI tg)
  {
    startingTribeSize = TriangleGenomeGUI.STARTINGTRIBESIZE;
    masterImage = image;
    imagePanel = tg;
    double seedSigma=4/3;
    // populate genome list
    for (int i = 0; i < startingTribeSize; i++)
    {

      Genome genome = new Genome(masterImage);
      int seed = (int) Math.abs(rand.nextGaussian() * seedSigma);

      while (seed > GenomeUtilities.NSEEDING)
      {
        seed = (int) Math.abs(rand.nextGaussian() * seedSigma);

      }

    
      switch (seed)
      {
      case 0:
        GenomeUtilities.mixedSampleGenome(genome, masterImage);
        break;

      case 1:
        GenomeUtilities.averagingGenome(genome, masterImage);
        break;

      case 2:
        GenomeUtilities.averageGridGenome(genome, masterImage);
     
        break;

      }
      genomeList.add(genome);
      genome.startFitscore = Statistics.getFitScore(
          GenomeUtilities.getBufferedImage(genome), masterImage);
      genome.genomeId = i;
    }
  }

  /****************************************************************************
   * Run
   * Input:none
   * Output:none
   * Description: starts the tribe object thread by calling climbRoutine
   ****************************************************************************/
  public void run()
  {

    climbLoop = true;
    climbRoutine();
  }

  /****************************************************************************
   * climbRoutine
   * Input:none
   * Output:none
   * Description:begins hill climbing by calling goToLocalMax as many times as
   *  set in NBREEDSTEPS
   ****************************************************************************/
  public void climbRoutine()
  {
    goToLocalMax(TriangleGenomeGUI.NBREEDSTEPS);

  }

  /****************************************************************************
   * interCrossRoutine
   * Input:integer sigma, half the size of the Tribe's genome list.
   * Output:none
   * Description: begins inter-tribe crossover. Randomly selects, while favoring
   *  fit genomes, 2 genomes to cross over, then does single point cross over on
   *  them 6 times.
   ****************************************************************************/
  public void interCrossRoutine(int sigma)
  {
    int listSize=genomeList.size();
    if(listSize==1)
      {
        goToLocalMax(TriangleGenomeGUI.NBREEDSTEPS);
        return;
      }
    for (int i = 0; i < TriangleGenomeGUI.STARTINGTRIBESIZE; i++)
    {
      checkForPaused();
      Genome genome = genomeList.get(i);
      try
      {
        genome.hc.pauseThread();
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    Genome son = new Genome(masterImage);
    Genome daughter = new Genome(masterImage);

    generateFitScores();
    Collections.sort(genomeList);
    // System.out.println("sorted   :"+this);
    int index0 = (int) Math.abs(rand.nextGaussian() * sigma);
    int index1 = (int) Math.abs(rand.nextGaussian() * sigma);
    int geneDiffAttempts = 0;
    while (index0 >= genomeList.size())
    {
      index0 = (int) Math.abs(rand.nextGaussian() * sigma);
    }

    while (index1 >= genomeList.size() || index1 == index0)
    {
      index1 = (int) Math.abs(rand.nextGaussian() * sigma);
    }

    // ensure genetic diversity
    while (HammingDistance.calcDiff(genomeList.get(index0),
        genomeList.get(index1)) < GENEDIFFERENCELIMIT)
    {
      while (index0 >= genomeList.size())
      {
        index0 = (int) Math.abs(rand.nextGaussian() * sigma);
      }

      while (index1 >= genomeList.size() || index1 == index0)
      {
        index1 = (int) Math.abs(rand.nextGaussian() * sigma);
      }
      if (geneDiffAttempts < 5)
        break;
    }

    checkForPaused();

    CrossOver.multiBreed(genomeList.get(index0), genomeList.get(index1), son,
        daughter, 3);
    genomeList.add(GenomeUtilities.genomeCopy(son));
    genomeList.add(GenomeUtilities.genomeCopy(daughter));
    if (genomeList.size() > ENDINGTRIBESIZE)
    {
      generateFitScores();
      try
      {
        Collections.sort(genomeList);
      } catch (java.util.ConcurrentModificationException e)
      {

      }

      ArrayList<Genome> fitGenomeHolder = new ArrayList<Genome>();
      for (int i = 0; i < startingTribeSize; i++)
      {
        fitGenomeHolder.add(genomeList.get(i));
      }

      genomeList.clear();
      genomeList.addAll(fitGenomeHolder);
    }

    climbLoop = true;

    // System.out.println("threads resume");
    imagePanel.drawGenome = imagePanel.tribeList.get(imagePanel.tribeSlider
        .getValue()).genomeList.get(imagePanel.genomeSlider.getValue());
    goToLocalMax(TriangleGenomeGUI.NBREEDSTEPS);

    // System.out.println("inerCross ends");
  }

  /****************************************************************************
   * intraCrossRoutin
   * Input:none
   * Output:returns a linked list of 2 Genomes contained in the tribe, deep-copied
   * Description:Called by TriangleGenomeGUI's crossTribes method. It returns deep
   *  copies of 2 random Genomes (favoring selection of fit ones) for the cross over
   ****************************************************************************/
  public LinkedList<Genome> intraCrossRoutin()
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      int sigma = genomeList.size() / 2;
      LinkedList<Genome> toCrossList = new LinkedList<>();

      // Genome son = new Genome(masterImage);
      // Genome daughter = new Genome(masterImage);

      generateFitScores();
      Collections.sort(genomeList);
      // sigma=genomeList.size()/2;//didn't we initialize sigma to this
      // value?

      int index0 = (int) Math.abs(rand.nextGaussian() * sigma);
      int index1 = (int) Math.abs(rand.nextGaussian() * sigma);

      while (index0 >= genomeList.size())
      {
        index0 = (int) Math.abs(rand.nextGaussian() * sigma);
      }

      while (index1 >= genomeList.size() || index1 == index0)
      {
        index1 = (int) Math.abs(rand.nextGaussian() * sigma);
      }

      Genome genome0 = GenomeUtilities.genomeCopy(genomeList.get(index0));
      Genome genome1 = GenomeUtilities.genomeCopy(genomeList.get(index1));
      genome0.masterImage = masterImage;
      genome1.masterImage = masterImage;

      toCrossList.add(genome0);
      toCrossList.add(genome1);
      for (int i = 0; i < genomeList.size(); i++)
      {
        Genome genome = genomeList.get(i);
        genome.startFitscore = Statistics.getFitScore(
            GenomeUtilities.getBufferedImage(genome), masterImage);
      }

      return toCrossList;
    }
  }

  /****************************************************************************
   * checkForPaused
   ****************************************************************************/
  public void checkForPaused()
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      while (pauseThreadFlag)
      {
        try
        {
          GUI_INITIALIZATION_MONITOR.wait();

        } catch (Exception e)
        {
        }
      }
    }
  }

  /****************************************************************************
   * pauseThread
   ****************************************************************************/
  public void pauseThread() throws InterruptedException
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      pauseThreadFlag = true;
      for (int i = 0; i < imagePanel.STARTINGTRIBESIZE; i++)
      {
        Genome genome = genomeList.get(i);
        genome.hc.pauseThread();

      }
    }
  }

  /****************************************************************************
   * resumeThread
   ****************************************************************************/
  public void resumeThread()
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      pauseThreadFlag = false;
      GUI_INITIALIZATION_MONITOR.notify();
      for (int i = 0; i < imagePanel.STARTINGTRIBESIZE; i++)
      {
        Genome genome = genomeList.get(i);
        genome.hc.resumeThread();
        // }
      }
    }
  }

  /****************************************************************************
   * generateFitScores
   * Input:none
   * Output:none
   * Description: calculates fitScore for each genome in the Tribes genomeList
   ****************************************************************************/
  public void generateFitScores()
  {
    for (int i = 0; i < genomeList.size(); i++)
    {
      Genome genome = genomeList.get(i);
      genome.fitscore = Statistics.getFitScore(
          GenomeUtilities.getBufferedImage(genome), masterImage);
    }
  }

  /****************************************************************************
   * goToLocalMax
   * Input:int N, times to repeat
   * Output:none
   * Description:initializes hillclimbing thread object for each Genome and starts
   *  it
   ****************************************************************************/
  public void goToLocalMax(int N)
  {
    checkForPaused();
    for (int i = 0; i < imagePanel.STARTINGTRIBESIZE; i++)
    {
      Genome genome = genomeList.get(i);
      genome.hc = new HillClimber(masterImage, genome, N);

      if (N > 1)
        genome.hc.doBreed = true;
      genome.hc.tribe = this;
      if (!genome.hc.isAlive())
      {
        genome.hc.start();
      }
    }
  }
}