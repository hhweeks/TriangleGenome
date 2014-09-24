import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class Picture extends JFrame
{ 
  public static final String VERSION = "Picture Version 2013.2.14";
  
  private int imageWidth, imageHeight;
  private BufferedImage userImage;
  private int insideLeft, insideTop;
  
  //Constructor to create an empty picture of a specified inside size
  public Picture(int insideWidth, int insideHeight)
  {
    this.setTitle(VERSION);
    
    imageWidth = insideWidth;
    imageHeight = insideHeight;
    userImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

    addSpaceToFrameForBoarder();
  }
  
  
  //Constructor to ask user for image file and create a Picture with that image
  public Picture()
  {
    
    String userFilePath = pickFile();
    
    userImage = loadImage(userFilePath, this);
    if (userFilePath == null)
    { imageWidth = 250;
      imageHeight = 250;
    }
    else
    { this.setTitle(userFilePath);
      imageWidth = userImage.getWidth();
      imageHeight = userImage.getHeight();
    }
    addSpaceToFrameForBoarder();
  }
  
  
  
  private void addSpaceToFrameForBoarder()
  {
    this.setSize(imageWidth, imageHeight); 
    this.setVisible(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    Insets inset = this.getInsets();
    insideLeft = inset.left;
    insideTop = inset.top;
    int frameWidth = imageWidth + inset.left + inset.right;
    int frameHeight = imageHeight + inset.top + inset.bottom;
    this.setSize(frameWidth, frameHeight);
    
    System.out.println("frame size="+frameWidth+", " +frameHeight);
  }
  
  
  private static String pickFile()
  {
    String dir = System.getProperty("user.dir");
    JFileChooser fileChooser = new JFileChooser(dir);
    int returnVal = fileChooser.showOpenDialog(null);
    
    if (returnVal == JFileChooser.APPROVE_OPTION) 
    {
      File file = fileChooser.getSelectedFile();
      String imagePath = file.getPath();
      System.out.println("You selected file: ["+imagePath+"]");
      return imagePath; 
    }
    
    return null;
  }
  
  
  
  
  private static BufferedImage loadImage(String imagePath, Component window)
  {
    if (imagePath == null) return null;
    
    // Create a MediaTracker instance, to montior loading of images
    MediaTracker tracker = new MediaTracker(window);

    // load each image and register it, 
    // using the MediaTracker.addImage (Image, int) method. 
    // It takes as its first parameter an image, 
    // and the idcode of the image as its second parameter. 
    // The idcode can be used to inquire about the status of 
    // a particular image, rather than a group of images.

    // Load the image
    Toolkit tk = Toolkit.getDefaultToolkit();
    Image loadedImage = tk.getImage(imagePath);

    // Register it with media tracker
    tracker.addImage(loadedImage, 1);
    try
    { tracker.waitForAll();
    }
    catch (Exception e){}
    
    int width = loadedImage.getWidth(null);
    int height = loadedImage.getHeight(null);
    BufferedImage imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics g = imageBuffer.getGraphics();
    g.drawImage(loadedImage, 0, 0, null);
    
    return imageBuffer; 
  }
  
  
  
  
  public Graphics getOffScreenGraphics()
  { return userImage.getGraphics();
  }
  
  
  
  public int getImageWidth()
  { return userImage.getWidth();
  }
  
  
  
  public int getImageHeight()
  { return userImage.getHeight();
  }
  
  
  
  
  public int getRed(int x, int y)
  {
    int rgb = userImage.getRGB(x, y);
    int red = (rgb & 0x00FF0000) >> 16;
    return red;
  }
  
  public int getGreen(int x, int y)
  {
    int rgb = userImage.getRGB(x, y);
    int green = (rgb & 0x0000FF00) >> 8;
    return green;
  }
  
  
  public int getBlue(int x, int y)
  {
    int rgb = userImage.getRGB(x, y);
    int blue = rgb & 0x000000FF;
    return blue;
  }
  
  
  
  public void setRGB(int x, int y, int r, int g, int b)
  {
    if (x<0) return;
    if (y<0) return;
    if (x>imageWidth) return;
    if (y>imageHeight) return;
    if (r<0 || g<0 || b<0) return;
    if (r>255 || g>255 || b>255) return;
    
    int rgb = (r<<16) | (g<<8) | b;
    userImage.setRGB(x, y, rgb);
  }
  
  public void setColor(int x, int y, Color c)
  {
    setRGB(x, y, c.getRed(), c.getGreen(), c.getBlue());
  }
  
  public void saveImage()
  {
    JFileChooser fileChooser = new JFileChooser();

    int returnValue = fileChooser.showSaveDialog(null);

    if (returnValue != JFileChooser.APPROVE_OPTION) return;

    File inputFile = fileChooser.getSelectedFile();
    String path = inputFile.getAbsolutePath();
    if ((path.endsWith(".png") == false) && (path.endsWith(".PNG") == false))
    { path = path+".png";
    }
    
    File myFile = new File(path); 
    try
    { ImageIO.write(userImage, "png", myFile);
    }
    catch (Exception e){ e.printStackTrace();}
  }
  
  
  
  public void paint(Graphics canvas)
  { 
    canvas.drawImage(userImage, insideLeft, insideTop, null);
  }
}