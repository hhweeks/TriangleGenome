import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Tribe extends Thread
{
  Random rand=new Random();
  ArrayList<Genome> genomeList=new ArrayList<>();
  BufferedImage masterImage;
  ImageContainer imagecontainer;
  private final Object GUI_INITIALIZATION_MONITOR=new Object();
  private boolean pauseThreadFlag=false;
  public ReentrantLock lock=new ReentrantLock();

  private volatile boolean running=true; // Run unless told to pause
  public static final int STARTINGTRIBESIZE=2;
  public static final int ENDINGTRIBESIZE=8;
  TriangleGenomeGUI imagePanel;
  HillClimber hc;

  public Tribe(BufferedImage image,TriangleGenomeGUI tg)
  {
    masterImage=image;
    imagePanel=tg;
    hc=new HillClimber(masterImage);
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
    	for(int i=0;i<4;i++)
    	{
    	  climbRoutine();
    	}
    	System.out.println("crossover");
    	for(int i=0;i<10;i++)interCrossRoutine(sigma);
    	imagePanel.crossTribes();
    }
  }
  
  public void climbRoutine()
  {
//    checkForPaused();
    goToLocalMax(10);    
  }
  
  public void interCrossRoutine(int sigma)
  { 
    Genome son=new Genome(masterImage);
    Genome daughter=new Genome(masterImage);
    
    generateFitScores();
    Collections.sort(genomeList);
//    sigma=genomeList.size()/2;//didn't we initialize sigma to this value?
    
    int index0=(int) Math.abs(rand.nextGaussian()*sigma);
    int index1=(int) Math.abs(rand.nextGaussian()*sigma);
    
    while(index0>=genomeList.size())
    {
      index0=(int) Math.abs(rand.nextGaussian()*sigma);
    }

    while(index1>=genomeList.size()||index1==index0)
    {
      index1=(int) Math.abs(rand.nextGaussian()*sigma);
    }

    CrossOver.breed(genomeList.get(index0), genomeList.get(index1), son, daughter, rand.nextInt(2000));
    genomeList.add(GenomeUtilities.genomeCopy(son));
    genomeList.add(GenomeUtilities.genomeCopy(daughter));
    if(genomeList.size()>ENDINGTRIBESIZE)
    {
      generateFitScores();
      Collections.sort(genomeList);
      
      ArrayList<Genome> fitGenomeHolder=new ArrayList<Genome>();
      for(int i=0;i<STARTINGTRIBESIZE;i++)
      {
        fitGenomeHolder.add(genomeList.get(i));
      }
      
      genomeList.clear();
      genomeList.addAll(fitGenomeHolder);

      System.out.println("trimmed");
    }
  }
  
  public LinkedList<Genome> intraCrossRoutin()
  {
    synchronized (this)
    {
      int sigma=genomeList.size()/2;
      LinkedList<Genome> toCrossList=new LinkedList<>();
      
      Genome son=new Genome(masterImage);
      Genome daughter=new Genome(masterImage);
      
      generateFitScores();
      Collections.sort(genomeList);
      
      int index0=(int) Math.abs(rand.nextGaussian()*sigma);
      int index1=(int) Math.abs(rand.nextGaussian()*sigma);
      
      while(index0>=genomeList.size())
      {
        index0=(int) Math.abs(rand.nextGaussian()*sigma);
      }

      while(index1>=genomeList.size()||index1==index0)
      {
        index1=(int) Math.abs(rand.nextGaussian()*sigma);
      }
      
      Genome genome0=GenomeUtilities.genomeCopy(genomeList.get(index0));
      Genome genome1=GenomeUtilities.genomeCopy(genomeList.get(index1));
      genome0.masterImage=masterImage;
      genome1.masterImage=masterImage;
      
      toCrossList.add(genome0);
      toCrossList.add(genome1);
      
      return toCrossList;
    }    
  }

  private void checkForPaused()
  {
    synchronized (this)
    {
      while(pauseThreadFlag)
      {
        try
        {
          this.wait();
        } catch (Exception e){}
      }
    }
  }

  public void pauseThread() throws InterruptedException
  {
    pauseThreadFlag=true;
  }

  public void resumeThread()
  {
    synchronized (this)
    {
      pauseThreadFlag=false;
      this.notify();
    }
  }

  public void generateFitScores()
  {
    Genome[] genomeArr=new Genome[genomeList.size()];
    for(int i=0;i<genomeList.size();i++)
    {
      genomeArr[i]=genomeList.get(i);
    }
    for(Genome genome:genomeArr)
    {
      genome.fitscore=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),masterImage);
    }
  }

  public void goToLocalMax(int N)
  { 
    //TODO experimental, generating more Genomes than we're Hillclimbing
    int topTwo=0;
    
    checkForPaused();
    Genome[] genomeArr=new Genome[genomeList.size()];
    for(int i=0;i<genomeList.size();i++)
    {
      genomeArr[i]=genomeList.get(i);
    }    
    synchronized(this)
    {
      for (Genome genome : genomeArr)
      {
        if (topTwo > 1)break;
//        System.out.println("updating Genome " + topTwo);
        topTwo++;
        for (int i = 0; i < N; i++)
        {
          // checkForPaused();
          // if(pauseThreadFlag)return;
          hc.climbStep(genome);
          imagePanel.triangleWindowUpdate();
        }
      }
    }
  }
}