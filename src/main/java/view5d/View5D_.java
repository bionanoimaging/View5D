/*-
 * #%L
 * View5D viewer for 5D visualization.
 * %%
 * Copyright (C) 1998 - 2024 Rainer Heintzmann.
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

// By making the appropriate class "View5D" or "View5D_" public and renaming the file, this code can be toggled between Applet and ImageJ respectively
package view5d;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import java.util.*;
import java.text.*;
import ij.plugin.frame.PlugInFrame;
import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;

import java.net.JarURLConnection;  // needed for extracting the Version information
import java.net.URLClassLoader;
import java.util.jar.Attributes;
// import java.util.jar.Manifest;
// import java.util.jar.Manifest.*;
import java.util.jar.JarFile.*;
import java.net.URL;
// import java.net.JarURLConnection;
import java.util.jar.Manifest;

  
/* The code below is necessary to include the software as a plugin into ImageJ */
public class View5D_ extends PlugInFrame implements PlugIn, WindowListener {
    // public static final long serialVersionUID = 2;
    // public static final long serialSubVersionUID = 5;   // to agree with the Fiji Version (jump from 2.3.9 to 2.5.2)
    // public static final long serialSubSubVersionUID = 2;
    public static String VersionString = "unknown";
	// Panel panel;
	int previousId;
	ImagePlus imp;
	ImageProcessor ip;
  	My3DData data3d;  // Takes care of all datasets
  	TextArea myLabel;
	MenuBar IJMenu; // to save it
    Vector<ImgPanel> panels=new Vector<ImgPanel>();  // Keeps track of all the views. Sometimes this information is needed to send the updates.
        
    String [] DimensionChoices={"ZCT","CZT","ZTC","TZC","CTZ","TCZ"};
    String [] AppendChoices={"Color","Time"};
    boolean AspectFromData=false;
    public int SizeX=0,SizeY=0,SizeZ=0,Elements=1,Times=1,DimensionOrder=0,AppendTo=0;

    public String getVersion() {
        try {
            URL resourceUrl = View5D_.class.getResource(View5D_.class.getSimpleName() + ".class");
            JarURLConnection connection = (JarURLConnection) resourceUrl.openConnection();
            Manifest manifest = connection.getManifest();
            Attributes mainAttrs = manifest.getMainAttributes();
            if (mainAttrs == null) return null;
            return mainAttrs.getValue("Implementation-Version");
        } catch (IOException E) {
            return "unkown version";
        }        
    }
    
    public void windowActivated(java.awt.event.WindowEvent windowEvent) {
    }
    
    public void windowClosed(java.awt.event.WindowEvent windowEvent) {

        // System.out.println("Plugin closed\n");
        // setVisible(false);
    }
    
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {  
    	// System.out.println("Plugin closing\n");
    	data3d.cleanup();
        removeAll();
        System.gc();
        dispose();
        // setVisible(false);
    }
    
    public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {
    }
    
    public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {
    // setVisible(true);
    }
    
    public void windowIconified(java.awt.event.WindowEvent windowEvent) {
    // setVisible(false);
    }
    
    public void windowOpened(java.awt.event.WindowEvent windowEvent) {
    setVisible(true);
    }
    
    
    public void UpdatePanels()  // update all panels
        {
            for (int i=0;i<panels.size();i++)
                ((ImgPanel) panels.elementAt(i)).c1.UpdateAllNoCoord();
        }
        
        
   void ImportDialog() {  // allows the user to define the import parameters
            ANGenericDialog md= new ANGenericDialog("Import Data");
            md.addChoice("Dimension Order",DimensionChoices,DimensionChoices[DimensionOrder]);
            md.addMessage("Size X: "+SizeX+", Size Y: "+SizeY);
            md.addNumericFields("SliceCount, Size Z: ",SizeZ,3,3);
            md.addNumericFields("TimeCount, Numer of Timepoints: ",Times,3,3);
            md.addNumericFields("Colors: ",Elements,3,3);
            if (data3d != null)
            	md.addChoice("append along",AppendChoices,AppendChoices[AppendTo]);
            md.addCheckbox("Lock Aspects with data scaling",AspectFromData);
            md.showDialog();
            if (! md.wasCanceled())
            {
                DimensionOrder=md.getNextChoiceIndex();
                SizeZ=(int) md.getNextNumber();
                Times=(int) md.getNextNumber();
                Elements=(int) md.getNextNumber();
                if (data3d != null)
                	AppendTo=md.getNextChoiceIndex();
                AspectFromData=md.getNextBoolean();
            }
        }
        

   public boolean LoadImg(int NumZ) {
  		int   redEl=-1,greenEl=-1,blueEl=-1;
    	//System.out.println("LoadImage called");
                
		imp = WindowManager.getCurrentImage();  // remember image
                if (imp == null)
                {
                  return false;
                }
		ip = imp.getProcessor();  // and its processor

		// ImageStack ims=imp.getStack();  // get the stack associated to this image
		ImageStack ims=imp.getImageStack();  // get the stack associated to this image

		//if imp instanceof Image5D
		//	ImageStack ims=imp.getImageStack();  // get the stack associated to this image
		//end
		
		SizeX = ims.getWidth();
		SizeY = ims.getHeight();
              
		if (NumZ > 0) 
                {
                    SizeZ=1; 
                    Elements=ims.getSize();
                }
        else
                {
                    SizeZ = ims.getSize();
                    if (SizeZ > 1)
                    {
                    	int SizeZ2=1;
                    	try {
                    		SizeZ2=imp.getNSlices();
                            Times= imp.getNFrames();
                            Elements = SizeZ / (SizeZ2 * Times);
                            SizeZ=SizeZ2;
                    	}
                    		catch(Exception e) {  // in case the version of ImageJ is too old to support it.
                    	
                    		SizeZ2= (int) IJ.getNumber("SliceCount unknown while importing stack. Number of slices: ",SizeZ);
                            Times= SizeZ / SizeZ2;
                            if (Times > 1)
                            Times= (int) IJ.getNumber("TimeCount unknown while importing stack. Number of times: ",Times);
                            Elements = SizeZ / (SizeZ2 * Times);
                            SizeZ = SizeZ2;
                    		}
                    }
                }
        DimensionOrder=1;
		if (SizeZ > 1 || Elements > 1 || Times > 1 || data3d != null)
			ImportDialog();

		int HistoX = 0;
        int HistoY = -1;
        int HistoZ = -1;
		int DataType=AnElement.ByteType;
                int NumBytes=1, NumBits=8;
		int ImageType=imp.getType();
     	switch (ImageType) {
     	case ImagePlus.COLOR_256: DataType=AnElement.ByteType;NumBytes=1;NumBits=8;break;    // 8-bit RGB with lookup
     	case ImagePlus.COLOR_RGB: DataType=AnElement.ByteType;NumBytes=1;NumBits=8;Elements=3; break;    // 24-bit RGB 
     	case ImagePlus.GRAY8: DataType=AnElement.ByteType;NumBytes=1;NumBits=8;break;    // 8-bit grayscale
     	case ImagePlus.GRAY16: DataType=AnElement.UnsignedShortType;NumBytes=2;NumBits=16;break;    // 16-bit grayscale
     	case ImagePlus.GRAY32: DataType=AnElement.FloatType;NumBytes=4;NumBits=32;break;    // float image
     	}

     	double[] Scales = new double[5];
     	for (int j=0;j<5;j++) Scales[j]=1.0;                
     	double[] Offsets = new double[5];
     	for (int j=0;j<5;j++) Offsets[j]=0.0;
     	double ScaleV=1.0,OffsetV=0.0;
     	String [] Names = new String[5];
     	String [] Units = new String[5];
     	Names[0] = "X";Names[1] = "Y";Names[2] = "Z";Names[3] = "Elements";Names[4] = "Time";
     	Units[0] = "pixels";Units[1] = "ypixels";Units[2] = "zpixels";Units[3] = "elements";Units[4] = "time";
     	String NameV = "intensity";
     	String UnitV = "a.u.";

     	ij.measure.Calibration myCal=imp.getCalibration();  // retrieve the spatial information
     	if (myCal.scaled())
     	{
     		Scales[0]=myCal.pixelWidth;
     		Scales[1]=myCal.pixelHeight;
     		Scales[2]=myCal.pixelDepth;
     		Offsets[0]=myCal.xOrigin;
     		Offsets[1]=myCal.yOrigin;
     		Offsets[2]=myCal.zOrigin;

     		if (myCal.getUnits() != "")
     		{
     			Units[0] = myCal.getUnits();
     			Units[1] = myCal.getUnits();
     			Units[2] = myCal.getUnits();
     		}

     		if (myCal.frameInterval != 0 && Scales[2] == 0) 
     		{
     			Scales[2]=myCal.frameInterval;
     			Units[2] = "seconds";
     		}
     	}

         if (myCal.calibrated())
                {
                    NameV="calib. int.";
                    UnitV=myCal.getValueUnit();
                    OffsetV=myCal.getCValue(0);
                    ScaleV=myCal.getCValue(1.0)-OffsetV;
                }

        if (data3d == null)  // is there no existing data?
			{
			if (Elements > 1)
				{
                    	redEl=0;
                    	greenEl=1;
				}
			if (Elements > 2)
                    	blueEl=2;
                
                	if (Elements >= 5)
                	{
                    	redEl=-1;greenEl=-1;blueEl=-1;
                	}
                        data3d = new My3DData(this,SizeX,SizeY,SizeZ,
                                    Elements,Times,
                                    redEl,greenEl,blueEl,
                                    HistoX,HistoY,HistoZ,DataType,NumBytes,NumBits,
                                    Scales,Offsets,
                                    ScaleV, OffsetV, Names, Units);
                        data3d.DimensionOrder=DimensionOrder;
                        data3d.AppendTo=AppendTo;
                        
                	for (int e=0;e<Elements;e++)
                      	 data3d.SetValueScale(e,ScaleV,OffsetV,NameV,UnitV);
			}
		else
			{
			if (SizeX != data3d.SizeX || SizeY != data3d.SizeY || SizeZ != data3d.SizeZ)
				{
                                    IJ.error("New image to load does not correspond in size! ...Not loading it.");
                                    System.out.println("Error! Size of new image to load does not correspond to old image\n");
                                    return false;
				}
			if (AppendTo == 0)  // append to elements
			  for (int i = 0;i< Elements;i++)
				{
				   int nr=data3d.GenerateNewElement(DataType,NumBytes,NumBits,Scales,Offsets,ScaleV, OffsetV, Names, Units);
				   data3d.SetValueScale(nr,ScaleV,OffsetV,NameV,UnitV);
				}
			else
			{
				  for (int i = 0;i< Times;i++)  // for all new time points do
					{
					   // int nr=data3d.GenerateNewTime(DataType,NumBytes,NumBits,Scales,Offsets,ScaleV, OffsetV, Names, Units);
					   //data3d.SetValueScale(nr,ScaleV,OffsetV,NameV,UnitV);
					}
			   // if (data3d.Times - Times <= 1)
	               ((ImgPanel) panels.elementAt(0)).CheckScrollBar();
			}
			}
                
		data3d.SetUp(DataType,this,Elements,Times,ImageType);   // here the data is really loaded
                //System.out.println("Check1\n");
                if (Elements == 1 && data3d.Elements <= 3 && data3d.Elements > 1)
                {
                    data3d.ActiveElement=0;
                    data3d.MarkChannel(0);
                    data3d.MarkAsHistoDim(0);
                    data3d.ActiveElement=1;
                    data3d.MarkChannel(1);
                    data3d.MarkAsHistoDim(1);
                    if (data3d.Elements == 3)
                    {
                        data3d.ActiveElement=2;
                        data3d.MarkChannel(2);
                    }
                }
                if ((data3d.Elements > 1 && data3d.Elements < 5) || redEl >= 0)
		        {
                    data3d.elementsLinked=false;
                    data3d.ToggleColor(true);  // switch to Color
		        }
                else
                {
                    data3d.elementsLinked=true;
                    data3d.ToggleColor(false);  // switch to BW
                }
                data3d.AdjustThresh(); // .initThresh();
                
                //System.out.println("Check2\n");
		if (data3d.HistoY<0 && data3d.Elements > 1)
                    {//System.out.println("Check2b\n");
                    data3d.HistoY = 1;}

                if (! panels.isEmpty())
                    {
                	CheckforLUT();
                    VerifyAspect();
                    }

                // System.out.println("Should lock Aspects\n");
                return true;
	}
        
        public void CheckforLUT()
        {
		imp = WindowManager.getCurrentImage();  // remember image
                if (imp == null)
                {
                  return;
                }
		int ImageType=imp.getType();
                //IJ.showMessage("Checking for LUT!");
                //IJ.showMessage("ImageType is: "+ImageType);
                LookUpTable lt = imp.createLut();
                if (ImageType == ij.ImagePlus.COLOR_256 || 
		    (ImageType != ij.ImagePlus.COLOR_RGB && ! lt.isGrayscale()) ||
		     imp.isInvertedLut())
                // if (! lt.isGrayscale())
                {
                    // IJ.showMessage("Found LUT, importing ...");
                    int lastLUT=data3d.AddLookUpTable(lt.getMapSize(),lt.getReds(),lt.getGreens(),lt.getBlues(), 0);
                    NumberFormat  nf = java.text.NumberFormat.getNumberInstance(Locale.US);

                    String LUTName="User defined "+nf.format(lastLUT-Bundle.ElementModels+1);
                    ((ImgPanel) panels.firstElement()).c1.label.PixDisplay.AddColorMenu(LUTName,lastLUT);
                    data3d.SetColorModelNr(data3d.ActiveElement,lastLUT);
		    if (! data3d.SetThresh(0,lt.getMapSize()-1))  // Set to the correct initial thresholds
		    	 data3d.InvalidateSlices();

                }
        }

        public void VerifyAspect(){
        if (AspectFromData)
        {
            System.out.println("Locking Aspects\n");
            for (int i=0;i<panels.size();i++) 
            {   // c2.scale == X is the master
                // System.out.println("Locking Aspect on panel " + i + "\n");
            	((ImgPanel) panels.elementAt(i)).c1.AspectFromView=true;
            	((ImgPanel) panels.elementAt(i)).c2.AspectFromView=true;
            	((ImgPanel) panels.elementAt(i)).c3.AspectFromView=true;
                ((ImgPanel) panels.elementAt(i)).c1.AspectLocked.setState(true);
                ((ImgPanel) panels.elementAt(i)).c2.AspectLocked.setState(true);
                ((ImgPanel) panels.elementAt(i)).c3.AspectLocked.setState(true);
            }
        }
        }

	
	public View5D_() {
		super("5D Viewer");
        VersionString = getVersion();
		data3d=null;
		if (! LoadImg(0))
		{
			IJ.showMessage("Error: View5D needs a loaded image in ImageJ, when starting!");
			return;
		}
		ImgPanel mypan=new ImgPanel(this,data3d);
		panels.addElement(mypan);  // enter this view into the list
		CheckforLUT();
		VerifyAspect();

		setLayout(new BorderLayout());
		add("Center", mypan);
		myLabel=new TextArea("5D-viewer Java Applet by Rainer Heintzmann, [press '?' for help]",1,76,TextArea.SCROLLBARS_NONE);
		add("North", myLabel);
		mypan.CheckScrollBar();
		setMenuBar(mypan.MyMenu);
		// setVisible(true);

		// data3d.initThresh();
		mypan.InitScaling();
		addWindowListener(this);
		pack();
		GUI.center(this);
		//show();
		setVisible(true);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (size.getWidth()/2),(int) (size.getHeight()/1.5)); // Heuristics
		repaint();
    }

    public ImagePlus GetImp() { return imp;}
    public ImageProcessor GetIp() {return ip;}

    /*  // Why does the below not work??
    public void focusGained(java.awt.event.FocusEvent e)
      {
          if (! panels.isEmpty())
	  {
		ImgPanel mypan=(ImgPanel) panels.firstElement();
		IJMenu=IJ.getInstance().getMenuBar();
		IJ.getInstance().setMenuBar(mypan.MyMenu);
	  }
	}
    
    public void focusLost(java.awt.event.FocusEvent e)
      {
          if (IJMenu != null)
	  {
		IJ.getInstance().setMenuBar(IJMenu);
	  }
	}
	*/
	
    void showAbout() {
              IJ.showMessage("About View5D, Version V"+getVersion(), // serialVersionUID+"."+serialSubVersionUID+"."+serialSubSubVersionUID,
	      " 5D-Viewer by Rainer Heintzmann\nUniversity of Jena, Jena, Germany and Leibniz Institute of PHotonics Technology, Germany\n"+
              "heintzmann@googlemail.com\n"+
              "http://www.nanoimaging.de/View5D/\n"+
	      "use mouse click for changing slices, \ndrag images, zoom by typing 'A' and 'a'\n"+
	      "'next page' and 'prev. page'  for changing perpendicular slice ,\n'<' and '>' for perpendicular magnificaltion\n"+
	      "type 'i' for init, 'c' for change ColorMap, '1,2' and '3,4' for Threshold" );
        }                                                                                                                 
}
