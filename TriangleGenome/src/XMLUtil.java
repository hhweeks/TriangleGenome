import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.*;


public class XMLUtil
{
  public static void writeXML(String fileName, Genome myGenome)
  {    
    String rootElement="gene";    
    XMLOutputFactory myXOF=XMLOutputFactory.newInstance();
    try
    {      
      XMLStreamWriter myXSW= myXOF.createXMLStreamWriter(new FileOutputStream(fileName), "UTF-8");
      
      //declaration
      myXSW.writeStartDocument("UTF-8", "1.0");
      myXSW.writeCharacters("\n");
      
      //write the root element      
      myXSW.writeStartElement("genome");
      myXSW.writeCharacters("\n\t");
      //write genomes variable, width and height
      myXSW.writeStartElement("width");
      myXSW.writeCharacters(Integer.toString(myGenome.IMG_WIDTH));
      myXSW.writeEndElement();
      
      myXSW.writeCharacters("\n\t");
      myXSW.writeStartElement("height");
      myXSW.writeCharacters(Integer.toString(myGenome.IMG_HEIGHT));
      myXSW.writeEndElement();
      
      for(int index=0;index<myGenome.geneList.size();index++)
      {
        Gene gene=myGenome.geneList.get(index);
        
        //store fields in gene all variables
        String x1=Integer.toString(gene.xpoints[0]);
        String y1=Integer.toString(gene.ypoints[0]);
        String x2=Integer.toString(gene.xpoints[1]);
        String y2=Integer.toString(gene.ypoints[1]);
        String x3=Integer.toString(gene.xpoints[2]);
        String y3=Integer.toString(gene.ypoints[2]);
        String r=Integer.toString(gene.r);
        String g=Integer.toString(gene.g);
        String b=Integer.toString(gene.b);
        String a=Integer.toString(gene.a);
        
        //write gene index
        myXSW.writeCharacters("\n\t");
        myXSW.writeStartElement("gene");
        myXSW.writeCharacters("\n\t\t");
      //myXSW.writeStartElement("index" + Integer.toString(index));
        //myXSW.writeEndElement();
        myXSW.writeStartElement("index");// + Integer.toString(index));
        myXSW.writeCharacters(Integer.toString(index));
        myXSW.writeEndElement();
        
        //write gene's elements
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("x1");
        myXSW.writeCharacters(x1);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("y1");
        myXSW.writeCharacters(y1);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("x2");
        myXSW.writeCharacters(x2);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("y2");
        myXSW.writeCharacters(y2);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("x3");
        myXSW.writeCharacters(x3);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("y3");
        myXSW.writeCharacters(y3);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("red");
        myXSW.writeCharacters(r);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("green");
        myXSW.writeCharacters(g);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("blue");
        myXSW.writeCharacters(b);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t\t");
        myXSW.writeStartElement("alpha");
        myXSW.writeCharacters(a);
        myXSW.writeEndElement();
        
        myXSW.writeCharacters("\n\t");
        myXSW.writeEndElement();
      }
      //write end of document
      myXSW.writeCharacters("\n");
      myXSW.writeEndElement();
      myXSW.flush();
      myXSW.close();
      
      
    }catch (XMLStreamException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (FileNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static Genome readXML(String filePath) throws Exception
  {
    String tmp = "";
    int width = 1;
    int height = 1;
    ArrayList<int[]> tmpReadGene = new ArrayList<int[]>();
    ArrayList<Gene> tmpGenesArray = new ArrayList<Gene>();
    Scanner in = new Scanner(new FileReader(filePath));
    while(in.hasNext())
    {
      tmp = in.next();
      if(tmp.contains("<height>")) height = Integer.parseInt(tmp.substring(8, tmp.length()-9));
      if(tmp.contains("<width>")) width = Integer.parseInt(tmp.substring(7, tmp.length()-8));
      if(tmp.contains("<index>"))
      {
        int[] tmpArr = new int[10];
        //x1
        tmp = in.next();
        tmpArr[0] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //y1
        tmp = in.next();
        tmpArr[1] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //x2
        tmp = in.next();
        tmpArr[2] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //y2
        tmp = in.next();
        tmpArr[3] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //x3
        tmp = in.next();
        tmpArr[4] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //y3
        tmp = in.next();
        tmpArr[5] = Integer.parseInt(tmp.substring(4, tmp.length()-5));
        //red
        tmp = in.next();
        tmpArr[6] = Integer.parseInt(tmp.substring(5, tmp.length()-6));
        //green
        tmp = in.next();
        tmpArr[7] = Integer.parseInt(tmp.substring(7, tmp.length()-8));
        //blue
        tmp = in.next();
        tmpArr[8] = Integer.parseInt(tmp.substring(6, tmp.length()-7));
        //alpha
        tmp = in.next();
        tmpArr[9] = Integer.parseInt(tmp.substring(7, tmp.length()-8));
        tmpReadGene.add(tmpArr);
      }
    }
    
    for(int[] g : tmpReadGene)
    {
      Gene tmpGene = new Gene();
      tmpGene.r = g[6];
      tmpGene.g = g[7];
      tmpGene.b = g[8];
      tmpGene.a = g[9];
      tmpGene.xpoints = new int[] {g[0], g[2], g[4]};
      tmpGene.ypoints = new int[] {g[1], g[3], g[5]};
      tmpGenesArray.add(tmpGene);
    }
    
    Genome readGenome = new Genome(width, height);
    readGenome.geneList = new ArrayList<Gene>(tmpGenesArray);
    //writeXML(System.nanoTime()+".xml", readGenome);
    new GenomeTable(readGenome);
    
    return readGenome;
  }
}
