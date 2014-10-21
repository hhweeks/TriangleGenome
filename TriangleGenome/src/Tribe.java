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
  private volatile boolean pauseThreadFlag=false;

  //private volatile boolean running=true; // Run unless told to pause
  public static final int STARTINGTRIBESIZE=2;
  public static final int ENDINGTRIBESIZE=3;
  TriangleGenomeGUI imagePanel;

  public Tribe(BufferedImage image,TriangleGenomeGUI tg)
  {
    masterImage=image;
    imagePanel=tg;
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
    	
    	climbRoutine();
    	interCrossRoutine(sigma);
    }
  }
  
  public void climbRoutine()
  {
    System.out.println("start");
    goToLocalMax(100);
    
//    for(Genome myGenome :genomeList)
//    {
//      Statistics.getFitScore(GenomeUtilities.getBufferedImage(myGenome), myGenome.image);
//    }
    
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

    CrossOver.breed(genomeList.get(index0), genomeList.get(index1), son, daughter, rand.nextInt(200));
    genomeList.add(GenomeUtilities.genomeCopy(son));
    genomeList.add(GenomeUtilities.genomeCopy(daughter));
    System.out.println("crossover");
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
  
  public void intraCrossRoutin(int sigma)
  {
    
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
//      genome.fitscore=Statistics.getFitScore(masterImage,GenomeUtilities.getBufferedImage(genome));
      genome.fitscore=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),masterImage);
    }
  }

  public void goToLocalMax(int N)
  {
    HillClimber hc=new HillClimber(masterImage);
    System.out.println("climber started");
    
    for(Genome genome:genomeList)
    {
      long startScore=0;//TODO debug
      long endScore=0;//TODO debug
      for(int i=0;i<N;i++)
      {
        checkForPaused();
     // long startTime=System.currentTimeMillis();
      
      //startScore=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),masterImage);
      hc.climbStep(genome);
      imagePanel.triangleWindowUpdate();
//      endScore=Statistics.getFitScore(GenomeUtilities.getBufferedImage(genome),masterImage);//TODO debug
//      long mutateResult=endScore-startScore;//TODO debug
//      if(mutateResult>1)//TODO debug
//      {
//        System.out.println("kept bad mutation");//TODO debug//TODO debug
//      }
      
      
     // long endTime=System.currentTimeMillis();
      //System.out.println(endTime-startTime);
      }
      
      //genome.image=GenomeUtilities.getBufferedImage(genome);
     
    }
  }

  public static void nextGeneration()
  {
    // while(genomeList.size()<ENDINGTRIBESIZE){
    Random rand=new Random();
    for(int i=0;i<50;i++)
    {
      int index=(int) Math.abs(rand.nextGaussian()*4);

      System.out.println(index);    }

  }

  
}