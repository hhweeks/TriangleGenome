import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TriangleGenomeGUI extends JFrame
{
  public static final int NTRIBES=1;
  static ImagePanel imageWindow;
  static ImagePanel triangleWindow;
  static JPanel buttonPanel;
  static JPanel sliderPanel=new JPanel();
  static JPanel controlPanel=new JPanel();
  JComboBox<String> imageSelect;
  JSlider triangleSlider=new JSlider(0, 200, 0);
  JSlider tribeSlider=new JSlider(0, NTRIBES, 0);
  JLabel triangleLabel=new JLabel("triangles");
  JLabel tribeLabel=new JLabel("tribes");
  JSlider genomeSlider=new JSlider(0, NTRIBES, 0);
  JLabel genomeLabel=new JLabel("genome");
  JButton runPauseButton=new JButton("RUN");
  JButton nextButton=new JButton("NEXT");
  JButton resetButton=new JButton("RESET");
  JButton tableButton=new JButton("GENOME TABLE");
  JButton readButton=new JButton("READ");
  JButton writeButton=new JButton("WRITE");
  JButton appendButton=new JButton("APPEND STATS TO FILE");
  JLabel genomeStats=new JLabel();
  TriangleGenomeGUI tg;
  Genome drawGenome;
  BufferedImage img;
  String path="images/";
  int numUdates;//used by triangleWindowUpdate
  long stats;
  public int tribeIndex;
  public int genomeIndex;
  ArrayList<Tribe> tribeList;
  String tmpGenomeStats="min:sec=0.0   gen=0   gen/sec=NaN   Fitness=";
  String flname;
  public long startTime = Long.MAX_VALUE;
  static int seconds = 1;
  boolean reset = false;
  final JFileChooser fc = new JFileChooser();
  FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");

  public TriangleGenomeGUI() throws IOException
  {
    tg=this;
    System.out.println("start");
    File folder=new File(path);
    ArrayList<String> findFiles=new ArrayList<String>();
    File[] listOfFiles=folder.listFiles();
    for(int i=0;i<listOfFiles.length;i++)
    {
      if(listOfFiles[i].isFile())
      {
        findFiles.add(listOfFiles[i].getName());
      }
    }
    
    String[] filenames=findFiles.toArray(new String[findFiles.size()]);
    buttonPanel=new JPanel();
    buttonPanel.setBounds(0, 500, 1500, 300);

    imageSelect=new JComboBox<String>(filenames);
    imageSelect.setSelectedIndex(0);

    String filename=(String) imageSelect.getSelectedItem();
    BufferedImage img=readImage(filename);
    imageWindow=new ImagePanel(img, 0, 0);
    triangleWindow=new ImagePanel(img, 0, 0);
    imageSelect.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        /*String*/ flname=(String) imageSelect.getSelectedItem();

        File imageFile=new File(path+flname);
        try
        {
          imageWindow.changeImage(ImageIO.read(imageFile));
          BufferedImage img=imageWindow.image;
          // make one genome for random display
          makeTribes(img);
          drawGenome=getGenome();
          // drawGenome=new Genome(img.getWidth(), img.getHeight());
          // GenomeUtilities
          // .averagingGenome(drawGenome,img);
          GenomeUtilities.drawNTriangles(200, triangleWindow, drawGenome);
          // triangleWindow.image=GenomeUtilities.getBufferedImage(myGenome);
          triangleSlider.setValue(200);
        } catch (IOException ec)
        {
          System.out.println("Image "+path+flname+" not Found");
        }

      }

    });

    // make triange window

    // Genome genome=new Genome();

    triangleWindow=new ImagePanel(img.getWidth(), img.getHeight());
    // build new tribes of appropriate image

    makeTribes(imageWindow.getImage());
    // make one genome for display
    drawGenome=getGenome();
    GenomeUtilities.drawNTriangles(200, triangleWindow, drawGenome);

    // generate statistics
    stats=Statistics.getFitScore(triangleWindow.image, imageWindow.image);
    System.out.println(stats);

    // populate control panel
    // imageSelect.setFont(new Font(imageSelect.getFont().getFontName(), 0,10));
    buttonPanel.add(imageSelect);

    resetButton.addActionListener(new ActionListener()
    {
    	
      @Override
      public void actionPerformed(ActionEvent e)
      {
        //triangleWindowUpdate();
        reset = true;
    	  makeTribes(triangleWindow.image);
    	  triangleWindowUpdate();
      }
    });
    nextButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        triangleWindowUpdate();
      }
    });

    runPauseButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        //if(!reset) startTime = System.nanoTime();
        if(runPauseButton.getText().compareTo("RUN")==0)
        {
          if(startTime > System.nanoTime()) startTime = System.nanoTime();
          runPauseButton.setText("PAUSE");
          toggleButtons(false);

          for(Tribe myTribe:tribeList)
          {
            if(myTribe.isAlive())
            {
              myTribe.resumeThread();
            }else
            {
              myTribe.start();
            }            
          }
          
        } else
        {
          try
          {
            for(Tribe myTribe:tribeList)
            {
              myTribe.pauseThread();
            }
          } catch (InterruptedException e1)
          {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          runPauseButton.setText("RUN");
          toggleButtons(true);
      }
    }});
    
    tableButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        new GenomeTable(drawGenome);
      }
    });
    readButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        fc.setFileFilter(xmlFilter);
        fc.setDialogTitle("Open Genome");
        fc.setCurrentDirectory(null);
        try
        {
          int returnVal = fc.showOpenDialog(readButton);
          if(returnVal == JFileChooser.APPROVE_OPTION)
          {
            File file = fc.getSelectedFile();
            XMLUtil.readXML(file.getName());
          }
          //XMLUtil.readXML("1413682511764729000.xml");
          else
          {
            System.out.println("File selection canceled by the user!");
          }
        }
        catch (Exception e1)
        {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    writeButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        XMLUtil.writeXML(System.nanoTime() + ".xml", drawGenome);
        //XMLUtil.writeXML(System.nanoTime() + ".xml", drawGenome, path+flname);
      }
    });
    triangleSlider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        GenomeUtilities.drawNTriangles(triangleSlider.getValue(),
            triangleWindow, drawGenome);
        triangleLabel.setText(triangleSlider.getValue()+" triangle(s)");
      }
    });
    tribeSlider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        tribeIndex=tribeSlider.getValue();
        tribeLabel.setText("Tribe #"+tribeIndex);
      }
    });
    genomeSlider=new JSlider(0, tribeList.size() - 1, 0);
    genomeSlider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        genomeIndex=genomeSlider.getValue();
        genomeLabel.setText("Genome #"+genomeIndex + 1);
        //System.out.println("tribe size " + tribeList.size());
        drawGenome = tribeList.get(0).genomeList.get(tribeIndex);
        //System.out.print("genome Index " + genomeIndex + "\ntribeIndex " + tribeIndex);
        //triangleWindowUpdate();
      }
    });

    buttonPanel.add(runPauseButton);
    buttonPanel.add(nextButton);
    buttonPanel.add(nextButton);
    triangleSlider.setValue(200);
    buttonPanel.add(triangleSlider);
    buttonPanel.add(tableButton);
    buttonPanel.add(readButton);
    buttonPanel.add(writeButton);
    buttonPanel.add(appendButton);
    buttonPanel.add(resetButton);
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

    controlPanel.setLayout(new BorderLayout());
    controlPanel.add(buttonPanel, BorderLayout.NORTH);

    // display images together on a single Jpanel
    JPanel imagePane=new JPanel();
    imagePane.setLayout(new GridLayout());
    imagePane.add(imageWindow);
    imagePane.add(triangleWindow);
    imagePane.setSize(600, 800);

    //genomeStats.setText(tmpGenomeStats+stats);
    genomeStats.setText("min:sec = " + "" +
    		"    gen = " + numUdates +
    		"    gen/sec = " + "" +
    		"    Fitness = " + stats + 
    		"    Duration = " + "0"//getRunDuration(startTime)
    		);
    this.add(genomeStats, BorderLayout.SOUTH);

    // buttonPanel.setPreferredSize(new Dimension(1150, 250));
    controlPanel.setPreferredSize(new Dimension(1150, 175));
    this.add(imagePane, BorderLayout.CENTER);
    sliderPanel.setLayout(new GridLayout(7, 0));
    sliderPanel.add(triangleLabel);
    sliderPanel.add(triangleSlider);
    sliderPanel.add(genomeLabel);
    
    sliderPanel.add(genomeSlider);
    sliderPanel.add(tribeLabel);
    sliderPanel.add(tribeSlider);
    sliderPanel.add(genomeStats);
    controlPanel.add(sliderPanel);
    // controlPanel.add(genomeStats);
    System.out.println("end");
    this.add(controlPanel, BorderLayout.SOUTH);
    this.setSize(1150, 650);
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public class ImagePanel extends JPanel
  {

    public BufferedImage image;
    int x,y;

    // construct blank image if passed height and width.
    public ImagePanel(int width,int height)
    {

      image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    }

    // construct an image of given size
    public ImagePanel(BufferedImage imagein,int xin,int yin)
    {

      image=imagein;
      x=xin;
      y=yin;
      this.setSize(x, y);

    }

    @Override
    protected void paintComponent(Graphics g)
    {
      super.paintComponent(g);
      buttonPanel.repaint();
      g.drawImage(image, x, y, null);
    }

    private void changeImage(BufferedImage cim)
    {
      image=cim;
      this.repaint();
    }

    public BufferedImage getImage()
    {
      return image;
    }

  }

  public void makeTribes(BufferedImage image)
  {
    tribeList=new ArrayList<>();
    for(int i=0;i<NTRIBES;i++)
    {
      Tribe tribe=new Tribe(image,tg);
      System.out.println("makeTribes");
      tribeList.add(tribe);
    }
  }
/******************************************************************************
 *triangleWindowUpdate
 *input:none
 *output:none
 *description:redraws the the triangle window and updates the Buffered image
 *  of the Genome
 *****************************************************************************/
  public void triangleWindowUpdate()
  {
    numUdates++;
    drawGenome=getGenome();
    
    if(numUdates%25==0)
    {
      GenomeUtilities.drawNTriangles(200, triangleWindow, drawGenome);
      stats=Statistics.getFitScore(triangleWindow.image, imageWindow.image);
      drawGenome.fitscore=stats;
      //System.out.println("50 updates happened");
    }
    //genomeStats.setText(tmpGenomeStats+stats);
    genomeStats.setText("min:sec = " + "" +
        "    Duration = " + getRunDuration(startTime)+
    		"    gen = " + numUdates +
    		"    gen/sec = " + getGenPerSec() +
    		"    Fitness = " + stats
    		);
  }

  public Genome getGenome()
  {
    return tribeList.get(0).genomeList.get(tribeIndex);
  }

  public Genome getDrawGenome()
  {
    return drawGenome;
  }

  // for reading images from file names
  public BufferedImage readImage(String filename) throws IOException
  {
    File imageFile=new File(path+filename);

    return ImageIO.read(imageFile);

  }
  
  private void toggleButtons(boolean s)
  {
	  imageSelect.setEnabled(s);
      resetButton.setEnabled(s);
      nextButton.setEnabled(s);
      triangleSlider.setEnabled(s);
      genomeSlider.setEnabled(s);
      tribeSlider.setEnabled(s);
      tableButton.setEnabled(s);
      readButton.setEnabled(s);
      writeButton.setEnabled(s);
      appendButton.setEnabled(s);
  }

  private String getRunDuration(long start)
  {
    seconds = (int) ((System.nanoTime() - start) * 0.000000001);
    String time = "";
    if(seconds/60 < 10) time += "0" + seconds/60;
    else time += seconds/60;
    if(seconds%60 < 10) time += ":0" + seconds%60;
    else time += ":" + seconds%60;
    return time;
    //return seconds/60 + ":" + seconds%60 + "";
  }
  private String getGenPerSec()
  {
    try { return String.format("%.5g%n", (double)numUdates/(double)seconds); }
    catch(ArithmeticException e) { return ""+-1; }
  }
  public static void main(String[] args) throws IOException
  {
    TriangleGenomeGUI tg=new TriangleGenomeGUI();
  }

}
