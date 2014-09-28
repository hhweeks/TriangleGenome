import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
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

public class TriangleGenomeGUI extends JFrame
{

  static ImagePanel imageWindow;
  static ImagePanel triangleWindow;
  JComboBox<String> imageSelect;
  JSlider triangleSlider = new JSlider(0, 200, 0);
  JButton runPauseButton = new JButton("RUN");
  JButton nextButton = new JButton("NEXT");
  JButton resetButton = new JButton("RESET");
  JButton tableButton = new JButton("GENOME TABLE");
  JButton readButton = new JButton("READ");
  JButton writeButton = new JButton("WRITE");
  JLabel genomeStats = new JLabel();
  BufferedImage img;
  String path = "images/";
  Statistics stats;
  String tmpGenomeStats  = "min:sec=0.0   gen=0   gen/sec=NaN   Fitness=";

  public TriangleGenomeGUI() throws IOException 
  {
    File folder = new File(path);
    ArrayList<String> findFiles = new ArrayList<String>();
    File[] listOfFiles = folder.listFiles();
    for (int i = 0; i < listOfFiles.length; i++)
    {
      if (listOfFiles[i].isFile())
      {
        findFiles.add(listOfFiles[i].getName());
      }
    }
    String[] filenames = findFiles.toArray(new String[findFiles.size()]);
    JPanel controlPanel=new JPanel();

    controlPanel.setBounds(0, 500, 1500, 300);
    //controlPanel.setBackground(Color.BLACK);

    imageSelect=new JComboBox<String>(filenames);
    imageSelect.setSelectedIndex(0);

    String filename=(String) imageSelect.getSelectedItem();

    BufferedImage img=readImage(filename);
    imageWindow=new ImagePanel(img, 0, 0);

    imageSelect.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        String flname=(String) imageSelect.getSelectedItem();

        File imageFile=new File(path + flname);
        try
        {
        	imageWindow.changeImage(ImageIO.read(imageFile));
        	BufferedImage img=imageWindow.image;
        // make one genome for random display
        Genome genome=new Genome(img.getWidth(), img.getHeight());
        GenomeUtilities
            .setRandomGenome(genome);
        GenomeUtilities.drawNTriangles(200, triangleWindow, genome);
        //triangleWindow.image=GenomeUtilities.getBufferedImage(myGenome);
          
        } catch (IOException ec)
        {
          System.out.println("Image2notFound");
        }

      }

    });

    // make triange window

    // Genome genome=new Genome();

    triangleWindow=new ImagePanel(img.getWidth(), img.getHeight());

    // make one genome for random display
    Genome genome=new Genome(img.getWidth(), img.getHeight());
    GenomeUtilities
        .setRandomGenome(genome);
    GenomeUtilities.drawNTriangles(200, triangleWindow, genome);

    // generate statistics
    stats = new Statistics(triangleWindow.image, imageWindow.image);
    System.out.println(stats.getFitScore());

    // populate control panel
    controlPanel.add(imageSelect);
    
    resetButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Genome genome=new Genome(imageWindow.image.getWidth(), imageWindow.image.getHeight());
        GenomeUtilities.setRandomGenome(genome);
        GenomeUtilities.drawNTriangles(200, triangleWindow, genome);
        stats = new Statistics(triangleWindow.image, imageWindow.image);
        System.out.println(stats.getFitScore());
        genomeStats.setText(tmpGenomeStats+  stats.getFitScore());
      }
    });
    nextButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Genome genome=new Genome(imageWindow.image.getWidth(), imageWindow.image.getHeight());
        GenomeUtilities.setRandomGenome(genome);
        GenomeUtilities.drawNTriangles(200, triangleWindow, genome);
        stats = new Statistics(triangleWindow.image, imageWindow.image);
        System.out.println(stats.getFitScore());
        genomeStats.setText(tmpGenomeStats+  stats.getFitScore());
      }
    });
    
    runPauseButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if(runPauseButton.getText().compareTo("RUN") == 0)
        {
          runPauseButton.setText("PAUSE");
          imageSelect.setEnabled(false);
          resetButton.setEnabled(false);
          nextButton.setEnabled(false);
          triangleSlider.setEnabled(false);
          tableButton.setEnabled(false);
          readButton.setEnabled(false);
          writeButton.setEnabled(false);
        }
        else
        {
          runPauseButton.setText("RUN");
          imageSelect.setEnabled(true);
          resetButton.setEnabled(true);
          nextButton.setEnabled(true);
          triangleSlider.setEnabled(true);
          tableButton.setEnabled(true);
          readButton.setEnabled(true);
          writeButton.setEnabled(true);
        }
      }
    });
    
    tableButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("DISPLAY TABLE");
      }
    });
    readButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("READ GENOME");
      }
    });
    writeButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("WRITE GENOME");
      }
    });
    triangleSlider.addChangeListener(new ChangeListener()
    {
      @Override
      public void stateChanged(ChangeEvent e)
      {
          System.out.println("triangleSlider value = " + triangleSlider.getValue());
      }
    });
    
    controlPanel.add(runPauseButton);
    controlPanel.add(nextButton);
    controlPanel.add(resetButton);
    controlPanel.add(triangleSlider);
    controlPanel.add(tableButton);
    controlPanel.add(readButton);
    controlPanel.add(writeButton);
    controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    // display images together on a single Jpanel
    JPanel imagePane=new JPanel();
    imagePane.setLayout(new GridLayout());
    imagePane.add(imageWindow);
    imagePane.add(triangleWindow);
    imagePane.setSize(600, 800);
    
    genomeStats.setText(tmpGenomeStats+  stats.getFitScore());
    this.add(genomeStats, BorderLayout.SOUTH);

    this.add(controlPanel);
    this.add(imagePane);
    this.setSize(1500, 800);
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

    }

    @Override
    protected void paintComponent(Graphics g)
    {
      super.paintComponent(g);
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

  // for reading images from file names
  public BufferedImage readImage(String filename) throws IOException
  {
    File imageFile=new File(filename);

    return ImageIO.read(imageFile);

  }

  public static void main(String[] args) throws IOException
  {
    TriangleGenomeGUI tg=new TriangleGenomeGUI();

  }

}
