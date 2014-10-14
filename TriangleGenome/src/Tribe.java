import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Tribe extends Thread
{
  Random rand=new Random();
  ArrayList<Genome> genomeList=new ArrayList<>();
  BufferedImage masterImage;
  ImageContainer imagecontainer;
  private final Object GUI_INITIALIZATION_MONITOR=new Object();
  private boolean pauseThreadFlag=false;

  private volatile boolean running=true; // Run unless told to pause
  public static final int STARTINGTRIBESIZE=8;
  public static final int ENDINGTRIBESIZE=128;

  public Tribe(BufferedImage image)
  {
    masterImage=image;
    // populate genome list
    for(int i=0;i<STARTINGTRIBESIZE;i++)
    {
      System.out.println("tribe builder");
      Genome genome=new Genome(masterImage);
      GenomeUtilities.averagingGenome(genome, masterImage);
      genomeList.add(genome);
    }
    // imagecontainer.setImage(GenomeUtilities.getBufferedImage(genome));
  }

  public void run()
  {
    int sigma=genomeList.size()/2;
    generateFitScores();
    while(true)
    {
      checkForPaused();
      System.out.println("start");
      goToLocalMax(100);
      System.out.println("mutate 100 times");

      Genome son=new Genome(masterImage);
      Genome daughter=new Genome(masterImage);
      sigma=genomeList.size()/2;
      int index0=(int) Math.abs(rand.nextGaussian()*sigma);
      int index1=(int) Math.abs(rand.nextGaussian()*sigma);
      while(index0>=genomeList.size())
      {
        index0=(int) Math.abs(rand.nextGaussian()*sigma);
      }

      while(index1>=genomeList.size())
      {
        index1=(int) Math.abs(rand.nextGaussian()*sigma);
      }

      CrossOver.breed(genomeList.get(index0), genomeList.get(index1), son, daughter, rand.nextInt(200));
      genomeList.add(GenomeUtilities.genomeCopy(son));
      genomeList.add(GenomeUtilities.genomeCopy(daughter));
      System.out.println("crossover");
      if(genomeList.size()>=ENDINGTRIBESIZE)
      {
        generateFitScores();
        Collections.sort(genomeList);
        System.out.println("sorted");

        Genome survivor0=genomeList.get(0);
        Genome survivor1=genomeList.get(1);
        genomeList.clear();
        genomeList.add(survivor0);
        genomeList.add(survivor1);

        System.out.println("trimmed");
      }
    }
  }

  private void checkForPaused()
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      while(pauseThreadFlag)
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

  public void pauseThread() throws InterruptedException
  {
    pauseThreadFlag=true;
  }

  public void resumeThread()
  {
    synchronized (GUI_INITIALIZATION_MONITOR)
    {
      pauseThreadFlag=false;
      GUI_INITIALIZATION_MONITOR.notify();
    }
  }

  public void generateFitScores()
  {
    for(Genome genome:genomeList)
    {
      // creates a fitscore from each image.
      // Should we preserve the Buffered Image as a phenotype???
      genome.fitscore=Statistics.getFitScore(masterImage,
          GenomeUtilities.getBufferedImage(genome));
    }
  }

  public void goToLocalMax(int N)
  {
    HillClimber hc=new HillClimber(masterImage);
    System.out.println("climber started");
    for(Genome genome:genomeList)
    {
      hc.climbLoop(genome, N);
      genome.image=GenomeUtilities.getBufferedImage(genome);
    }
  }

  public static void nextGeneration()
  {

    // while(genomeList.size()<ENDINGTRIBESIZE){
    Random rand=new Random();
    for(int i=0;i<50;i++)
    {
      int index=(int) Math.abs(rand.nextGaussian()*4);

      System.out.println(index);
    }

  }

  public static void main(String[] args)
  {
    nextGeneration();
  }
}