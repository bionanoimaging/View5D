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

// import java.io.*;
package view5d;

import java.applet.Applet;
import java.awt.*;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
// import view5d.*;
import java.text.NumberFormat;

// insert : "public" before this to make it an applet
public class View5D extends Applet{  // can also be: Container  (but then without the applet functionality)
    // NOTE: Window events are handles in "AlternateViewer.java"
  static final long serialVersionUID = 2;  // Just to fulfil the requirement. Is not used. Look at View5D_.java
  public int SizeX=0,SizeY=0,SizeZ=0,Elements=1,Times=1;
  int defaultColor0=-1,
      redEl=-1,greenEl=-1,blueEl=-1;
  My3DData data3d=null;
  TextArea myLabel;
  ImgPanel mypan=null;
  AlternateViewer aviewer;  // to keep track of the viewer instance
  Vector<ImgPanel> panels=new Vector<ImgPanel>();  // Keeps track of all the views. Sometimes this information is needed to send the updates.
  
  public String filename=null;

  public static Dimension getScreenSize() {
        Dimension size;
        try {
            //if (System.getProperty("java.version").compareTo("1.9") < 0)  // this is to avoid some nasty crash with some of the 1.8 versions in windows using getScreenSize();
            //    return new Dimension(640, 480);  // needed for example for headless mode
            //else
            //    size = Toolkit.getDefaultToolkit().getScreenSize();
            final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice[] gs = ge.getScreenDevices();
            final DisplayMode dm = gs[0].getDisplayMode(); //
            return new Dimension(dm.getWidth(),dm.getHeight());
        }
        catch(Exception e) {
            size = new Dimension(640, 480);  // needed for example for headless mode
        }
        return size;
    }

    public String getVersion() {
        try {
            URL resourceUrl = View5D.class.getResource(View5D.class.getSimpleName() + ".class");
            JarURLConnection connection = (JarURLConnection) resourceUrl.openConnection();
            Manifest manifest = connection.getManifest();
            Attributes mainAttrs = manifest.getMainAttributes();
            if (mainAttrs == null) return null;
            return mainAttrs.getValue("Implementation-Version");
        } catch (IOException E) {
            return "unkown version";
        }        
    }

    public void UpdatePanels()  // update all panels
        {
            for (int i=0;i<panels.size();i++)
                ((ImgPanel) panels.elementAt(i)).c1.UpdateAllNoCoord();
        }
 
   /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public View5D AddElement(byte [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ByteType, NumBytes=1, NumBits=8;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                            data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
                System.arraycopy( myarray, t*SizeX*SizeY*SizeZ,
                                 ((ByteElement) data3d.ElementAt(ne,t)).myData, 0 , SizeX*SizeY*SizeZ);
		}
	    data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
        Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
   }
   /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public View5D AddElement(short [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ShortType, NumBytes=2, NumBits=16;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                            data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
                System.arraycopy( myarray, t*SizeX*SizeY*SizeZ,
                                 ((ShortElement) data3d.ElementAt(ne,t)).myData, 0 , SizeX*SizeY*SizeZ);
		}
	    data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
       Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
   }
    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddElement(char [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.UnsignedShortType, NumBytes=1, NumBits=8;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
            System.arraycopy( myarray, t*SizeX*SizeY*SizeZ,
                    ((UnsignedShortElement) data3d.ElementAt(ne,t)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
        Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
    }
   /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public View5D AddElement(float [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.FloatType, NumBytes=4, NumBits=32;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                            data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
                System.arraycopy( myarray, t*SizeX*SizeY*SizeZ,
                                 ((FloatElement) data3d.ElementAt(ne,t)).myData, 0 , SizeX*SizeY*SizeZ);
		}
	data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
       Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
   }
   /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public View5D AddElement(double [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.DoubleType, NumBytes=8, NumBits=64;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                            data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
                System.arraycopy( myarray, t*SizeX*SizeY*SizeZ,
                                 ((DoubleElement) data3d.ElementAt(ne,t)).myData, 0 , SizeX*SizeY*SizeZ);
		}
	data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
       Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
   } 

   /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public View5D AddElementC(float [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ComplexType, NumBytes=8, NumBits=64;
        int ne=data3d.GenerateNewElement(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                            data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits());
        for (int t=0;t<Times;t++) {
                System.arraycopy( myarray, 2*t*SizeX*SizeY*SizeZ,
                                 ((ComplexElement) data3d.ElementAt(ne,t)).myData, 0 , 2*SizeX*SizeY*SizeZ);
		}
	data3d.GetBundleAt(ne).ToggleOverlayDispl(1);
       Elements = data3d.GetNumElements();Times = data3d.GetNumTimes();
        return this;
   }

    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTime(byte [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ByteType, NumBytes=1, NumBits=8;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, e*SizeX*SizeY*SizeZ,
                    ((ByteElement) data3d.ElementAt(e,nt)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }
    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTime(short [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ShortType, NumBytes=2, NumBits=16;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, e*SizeX*SizeY*SizeZ,
                    ((ShortElement) data3d.ElementAt(e,nt)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }
    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTime(char [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.UnsignedShortType, NumBytes=1, NumBits=8;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, e*SizeX*SizeY*SizeZ,
                    ((UnsignedShortElement) data3d.ElementAt(e,nt)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }
    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTime(float [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.FloatType, NumBytes=4, NumBits=32;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, e*SizeX*SizeY*SizeZ,
                    ((FloatElement) data3d.ElementAt(e,nt)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }
    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTime(double [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.DoubleType, NumBytes=8, NumBits=64;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, e*SizeX*SizeY*SizeZ,
                    ((DoubleElement) data3d.ElementAt(e,nt)).myData, 0 , SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }

    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public View5D AddTimeC(float [] myarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        int DataType=AnElement.ComplexType, NumBytes=8, NumBits=64;
        int nt=data3d.GenerateNewTime(DataType,NumBytes,NumBits,data3d.GetScale(data3d.ActiveElement),
                data3d.GetOffset(data3d.ActiveElement),1.0,0.0,data3d.GetAxisNames(),data3d.GetAxisUnits(),Elements);
        for (int e=0;e<Elements;e++) {
            System.arraycopy( myarray, 2*e*SizeX*SizeY*SizeZ,
                    ((ComplexElement) data3d.ElementAt(e,nt)).myData, 0 , 2*SizeX*SizeY*SizeZ);
        }
        mypan.CheckScrollBar();
        if (aviewer != null)
            ((ImgPanel) aviewer.mycomponent).CheckScrollBar();
        return this;
    }


    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public static View5D Start5DViewer(byte [] barray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.ByteType, NumBytes=1, NumBits=8;
        // System.out.println("Parameters ARE: " + SizeX+", "+SizeX+", "+SizeZ+", "+Elements+", "+Times+", "+AX+", "+AY);
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((ByteElement) anApplet.data3d.ActElement()).myData= barray; // new byte[SizeX*SizeY*SizeZ];
	    anApplet.data3d.ToggleOverlayDispl(1);
        anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
            {
                // System.out.println("Debug t:" + t+", e:"+e);
                if (e==0 && t ==0) {
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                    // mypan = anApplet.mypan;
                } else
                    System.arraycopy( barray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((ByteElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
            }
        anApplet.start();
        return anApplet;
   }

    public void minimize() {
        // aviewer.setVisible(false); // hide it
        aviewer.setState(Frame.ICONIFIED);
        // this.dispatchEvent(new WindowEvent(aviewer, WindowEvent.WINDOW_ICONIFIED));
        // aviewer.setExtendedState(JFrame.ICONIFIED); // should also cause the icon to appear
        // aviewer.dispose();
    }

    public void toFront() {
        data3d.InvalidateSlices();
        //java.awt.EventQueue.invokeLater(new Runnable() {
        //    @Override
        //    public void run() {
        //        aviewer.toFront();
        //        aviewer.repaint();
        //    }
        //});
        if (aviewer.getState() != Frame.NORMAL) { aviewer.setState(Frame.NORMAL); }
        aviewer.setVisible(true);
        aviewer.toFront();
        aviewer.repaint();
    }

    public void closeAll() {
       this.dispatchEvent(new WindowEvent(aviewer, WindowEvent.WINDOW_CLOSING));
       aviewer.dispose();
       this.stop();
       this.destroy();
    }

    public void SetElementsLinked(boolean doLink) {
       data3d.setElementsLinked(doLink);
    }

    public void setTimesLinked(boolean doLink) {
        data3d.setTimesLinked(doLink);
    }

    public void SetGamma(int e, double gamma) {
        data3d.SetGamma(e,gamma);
    }

    /* Just an alias to call the overloaded methods from Python using pyjnius */
    public static View5D Start5DViewerB(byte [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, AX, AY);
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(byte [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times,(int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }

    public static View5D Start5DViewerB(byte [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) { // This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5)); // This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }

    public void setSize(int width, int height) {
        aviewer.setSize(width,height);
    }

    public void ReplaceData(int e, int t, byte [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((ByteElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataB(int e, int t, byte [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((ByteElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceData(int e, int t, double [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((DoubleElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataD(int e, int t, double [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((DoubleElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceData(int e, int t, float [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((FloatElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataF(int e, int t, float [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((FloatElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceData(int e, int t, int [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((IntegerElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataI(int e, int t, int [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((IntegerElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceData(int e, int t, char [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((UnsignedShortElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataUS(int e, int t, char [] anarray) {
        int [] sizes = data3d.sizes; int tsize = sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((UnsignedShortElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public void ReplaceDataC(int e, int t, float [] anarray) {
        int [] sizes = data3d.sizes; int tsize = 2*sizes[0]*sizes[1]*sizes[2];
        System.arraycopy(anarray, 0, ((ComplexElement) data3d.ElementAt(e,t)).myData, 0 , tsize);
    }
    public int getNumElements() {
        return data3d.GetNumElements();
    }
    public int getNumTime() {
        return data3d.GetNumTimes();
    }
    public static View5D Dummy(byte[] sarray, int SizeX, int SizeY, int SizeZ, int Elements,int Times) { // int AX, int AY
        int DataType=AnElement.ByteType, NumBytes=1, NumBits=8;
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,1,1,DataType,NumBytes,NumBits);
        sarray[0]=0;
        ((ByteElement) anApplet.data3d.ActElement()).myData= sarray; // new byte[SizeX*SizeY*SizeZ];
        anApplet.data3d.ToggleOverlayDispl(1);
        anApplet.aviewer=new AlternateViewer(anApplet,600,600);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
            {
                // System.out.println("Debug t:" + t+", e:"+e);
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else
                    System.arraycopy(sarray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                            ((ByteElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
            }

        anApplet.start();
        // System.out.println("Parameters ARE: " + SizeX+", "+SizeX+", "+SizeZ+", "+Elements+", "+Times);
        return anApplet;
    }

    public static int DummyI(int sss) {
        return 44;
    }

    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
   public static View5D Start5DViewer(short [] sarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.ShortType, NumBytes=2, NumBits=8;
        // System.out.println("viewer invoked (short datatype)");
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((ShortElement) anApplet.data3d.ActElement()).myData= sarray; // new byte[SizeX*SizeY*SizeZ];
   	    anApplet.data3d.ToggleOverlayDispl(1);
        anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
            {
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else
                    System.arraycopy(sarray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((ShortElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
            }
        anApplet.start();
        return anApplet;
   }

    /* Just an alias to call the overloaded methods from Python using pyjnius */
    public static View5D Start5DViewerS(short [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, AX, AY);
    }
    public static View5D Start5DViewerS(short [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(short [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }

    /* The code below is necessary to include the software as a plugin into Matlab and DipImage (Univ. Delft) */
    public static View5D Start5DViewer(char [] sarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.UnsignedShortType, NumBytes=2, NumBits=16;
        // System.out.println("viewer invoked (unsigned short datatype)");
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((UnsignedShortElement) anApplet.data3d.ActElement()).myData= sarray; // new byte[SizeX*SizeY*SizeZ];
        anApplet.data3d.ToggleOverlayDispl(1);
        anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
            {
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else
                    System.arraycopy(sarray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                            ((UnsignedShortElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
            }
        anApplet.start();
        return anApplet;
    }

    /* Just an alias to call the overloaded methods from Python using pyjnius or javabridge*/
    public static View5D Start5DViewerUS(char [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times,AY,AX);
    }
    public static View5D Start5DViewerUS(char [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times,(int) (size.getWidth()/2),(int) (size.getHeight()/1.5));// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(char [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }

    /* The code below is necessary to include the software as a plugin into Mathlab and DipImage (Univ. Delft) */
   public static View5D Start5DViewer(float [] farray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.FloatType, NumBytes=4, NumBits=32;
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((FloatElement) anApplet.data3d.ActElement()).myData= farray; // new byte[SizeX*SizeY*SizeZ];
	anApplet.data3d.ToggleOverlayDispl(1);
       anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else     
                    System.arraycopy( farray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((FloatElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
        anApplet.start();
        return anApplet;
   }

    /* Just an alias to call the overloaded methods from Python using pyjnius */
    public static View5D Start5DViewerF(float [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, AX, AY);
    }
    public static View5D Start5DViewerF(float [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(float [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size =getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }

    /* Complex is not a known class in java, therefore a separate */
   public static View5D Start5DViewerC(float [] carray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.ComplexType, NumBytes=8, NumBits=64;
	// SizeX=SizeX/2;
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((ComplexElement) anApplet.data3d.ActElement()).myData= carray; // new byte[SizeX*SizeY*SizeZ];
	anApplet.data3d.ToggleOverlayDispl(1);
       anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else
                    System.arraycopy(carray, 2*(e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((ComplexElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , 2*SizeX*SizeY*SizeZ);
        anApplet.start();
        return anApplet;
   }
    public static View5D Start5DViewerC(float [] carray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) { // This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewerC(carray, SizeX, SizeY, SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5)); // This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }

   /* The code below is necessary to include the software as a plugin into Mathlab and DipImage (Univ. Delft) */
   public static View5D Start5DViewer(double [] farray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.DoubleType, NumBytes=8, NumBits=64;
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((DoubleElement) anApplet.data3d.ActElement()).myData= farray; // new byte[SizeX*SizeY*SizeZ];
	    anApplet.data3d.ToggleOverlayDispl(1);
        anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else     
                    System.arraycopy( farray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((DoubleElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
        anApplet.start();
        return anApplet;
   }

    /* Just an alias to call the overloaded methods from Python using pyjnius */
    public static View5D Start5DViewerD(double [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, AX, AY);
    }
    public static View5D Start5DViewerD(double [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(double [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }



    /* The code below is necessary to include the software as a plugin into Mathlab and DipImage (Univ. Delft) */
   public static View5D Start5DViewer(int [] iarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        int DataType=AnElement.IntegerType, NumBytes=4, NumBits=32;
        // System.out.println("viewer invoked (int datatype)");
        View5D anApplet=Prepare5DViewer(SizeX,SizeY,SizeZ,Elements,Times,DataType,NumBytes,NumBits);
        ((IntegerElement) anApplet.data3d.ActElement()).myData= iarray; // new byte[SizeX*SizeY*SizeZ];
	anApplet.data3d.ToggleOverlayDispl(1);
       anApplet.aviewer=new AlternateViewer(anApplet,AX,AY);
        for (int t=0;t<Times;t++)
            for (int e=0;e<Elements;e++)
                if (e==0 && t ==0)
                    anApplet.aviewer.Assign3DData(anApplet,anApplet.mypan,anApplet.data3d);
                else                        
                    System.arraycopy( iarray, (e+Elements*t)*SizeX*SizeY*SizeZ,
                                 ((IntegerElement) anApplet.data3d.ElementAt(e,t)).myData, 0 , SizeX*SizeY*SizeZ);
        anApplet.start();
        return anApplet;
   }

    /* Just an alias to call the overloaded methods from Python using pyjnius */
    public static View5D Start5DViewerI(int [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times, int AX, int AY) {
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, AX, AY);
    }
    public static View5D Start5DViewerI(int [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));// This is unfortunately NECESSARY due to a limited argument length of javabringe in Python
    }
    /* Just an alias for backward compatibility calling without viewer sizes */
    public static View5D Start5DViewer(int [] array, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
        Dimension size = getScreenSize();
        return Start5DViewer(array,SizeX,SizeY,SizeZ,Elements,Times, (int) (size.getWidth()/2),(int) (size.getHeight()/1.5));
    }


    /* The code below is necessary to include the software as a plugin into Mathlab and DipImage (Univ. Delft) */
   //public static View5D Start5DViewer(short [] sarray, int SizeX, int SizeY, int SizeZ,int Elements,int Times) {
   //     int [] iarray= new int[SizeX*SizeY*SizeZ*Times*Elements];
   //     for (int i=0;i<SizeX*SizeY*SizeZ*Times*Elements;i++)
   //          iarray[i]=sarray[i];
   //     return Start5DViewer(iarray, SizeX, SizeY, SizeZ, Elements, Times);
   //}   
   
   public void ProcessKeyMainWindow(char myChar)  // for simple scripting from outside
   {
	   mypan.c1.ProcessKey(myChar);
	   mypan.c1.UpdateAll();
   }
   
   public void ProcessKeyElementWindow(char myChar)  // for simple scripting from outside
   {
	   mypan.label.PixDisplay.ProcessKey(myChar);      
	   mypan.c1.UpdateAll();
   }

    public void NameElement(int elementNum, String Name) {
       data3d.ElementAt(elementNum).NameV = Name;
   }
    public void setName(int elementNum, String Name) {
        data3d.ElementAt(elementNum).NameV = Name;
    }
    public void setUnit(int elementNum, String Name) { data3d.ElementAt(elementNum).UnitV = Name; }
    public void setOffsetScale(int elementNum, double offset, double scale) {
        data3d.ElementAt(elementNum).OffsetV = offset;
        data3d.ElementAt(elementNum).ScaleV = scale;
    }
    public void setMinMaxThresh(int elementNum, double MinVal, double MaxVal) {
       data3d.SetThresh(elementNum,MinVal,MaxVal);
    }

    public void SetAxisScalesAndUnits(int elementNum, int timeNum, double SV, double SX,double SY,double SZ,double SE,double ST,double OV,double OX,double OY,double OZ,double OE,double OT,String NameV, String Names[],String UnitV, String Units[]) {
        data3d.ElementAt(elementNum,timeNum).ScaleV = SV;
        data3d.ElementAt(elementNum,timeNum).Scales[0] = SX;
        data3d.ElementAt(elementNum,timeNum).Scales[1] = SY;
        data3d.ElementAt(elementNum,timeNum).Scales[2] = SZ;
        data3d.ElementAt(elementNum,timeNum).Scales[3] = SE;
        data3d.ElementAt(elementNum,timeNum).Scales[4] = ST;
        data3d.ElementAt(elementNum,timeNum).OffsetV = OV;
        data3d.ElementAt(elementNum,timeNum).Offsets[0] = OX;
        data3d.ElementAt(elementNum,timeNum).Offsets[1] = OY;
        data3d.ElementAt(elementNum,timeNum).Offsets[2] = OZ;
        data3d.ElementAt(elementNum,timeNum).Offsets[3] = OE;
        data3d.ElementAt(elementNum,timeNum).Offsets[4] = OT;
        data3d.ElementAt(elementNum,timeNum).Names = Names;
        data3d.ElementAt(elementNum,timeNum).Units = Units;
        data3d.ElementAt(elementNum,timeNum).NameV = NameV;
        data3d.ElementAt(elementNum,timeNum).UnitV = UnitV;
    }
    public void SetAxisScalesAndUnits(double SV, double SX,double SY,double SZ,double SE,double ST,double OV,double OX,double OY,double OZ,double OE,double OT,String NameV, String Names[],String UnitV, String Units[]) {
        for (int e = 0; e < data3d.Elements; e++) {
            for (int t = 0; t < data3d.Times; t++)
                SetAxisScalesAndUnits(e, t, SV, SX,SY,SZ,SE,ST,OV,OX,OY,OZ,OE,OT,NameV,Names,UnitV, Units);
        }
    }

    public void NameWindow(String Name) {
       aviewer.setTitle(Name);
    }

    public String ExportMarkers()
   {
       return data3d.MyMarkers.PrintList(data3d);
   }

   public double[][] ExportMarkerLists()
   {
       return data3d.MyMarkers.ExportMarkerLists(data3d);
   }

   public double[][] ExportMarkers(int list)
   {
       return data3d.MyMarkers.ExportMarkers(list,data3d);
   }

   // one of the function accesable from Matlab and Mathematica
   // The whole state of all lists and their connections is imported
   public void ImportMarkerLists(float [][] lists)  // all markers are just stored in one big matrix  
   {
       data3d.MyMarkers.ImportMarkerLists(lists); // insert the markers into the list
       data3d.InvalidateSlices();
       mypan.c1.UpdateAllNoCoord();
   }
   public void DeleteAllMarkerLists() // deletes all existing marker lists
   {
       data3d.MyMarkers.DeleteAllMarkerLists(); // insert the markers into the list	
   }


   public void ImportMarkers(float [][] positions, int NumPos)   // for backward compatibility reasons
   {
     ImportMarkers(positions);
   }

   // one of the function accesable from Matlab and Mathematica.
   // This function imports only one list!
   public void ImportMarkers(float [][] positions)  
   {
       data3d.NewMarkerList();  // open a new list
       data3d.MyMarkers.ImportPositions(positions); // insert the markers into the list
       data3d.InvalidateSlices();
       mypan.c1.UpdateAllNoCoord();
   }
    public void setMarkerInitTrackDir(int TD,int SX,int SY,int SZ,boolean UC,int CX,int CY,int CZ) {
        data3d.setMarkerInitTrackDir(TD,SX,SY,SZ,UC,CX,CY,CZ);
    }

    public static View5D Prepare5DViewer(int SizeX, int SizeY, int SizeZ, int Elements, int Times, int DataType, int NumBytes, int NumBits) {
	// SizeX=100,SizeY=100,SizeZ=100,
        int   redEl=-1,greenEl=-1,blueEl=-1;
        double[] Scales = new double[5];
        for (int j=0;j<5;j++) Scales[j]=1.0;                
        double[] Offsets = new double[5];
        for (int j=0;j<5;j++) Offsets[j]=0.0;
        double ScaleV=1.0,OffsetV=0.0;
        int HistoX = 0;
        int HistoY = -1;
        int HistoZ = -1;
        if (Elements > 1)
            HistoY = 1;

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
        
        String [] Names = new String[5];
        String [] Units = new String[5];
        Names[0] = "X";Names[1] = "Y";Names[2] = "Z";Names[3] = "Elements";Names[4] = "Time";
        Units[0] = "pixels";Units[1] = "ypixels";Units[2] = "zpixels";Units[3] = "elements";Units[4] = "time";
        String NameV = "intensity";
        String UnitV = "a.u.";

        View5D anApplet=new View5D();

        My3DData data3d = new My3DData(anApplet,SizeX,SizeY,SizeZ,
                                    Elements,Times,
                                    redEl,greenEl,blueEl,
                                    HistoX,HistoY,HistoZ,
                				     DataType,NumBytes,NumBits,
                                    Scales,Offsets,
                                    ScaleV, OffsetV, Names, Units);
        // System.out.println("created data " + Elements);
        for (int e=0;e<Elements;e++)
            data3d.SetValueScale(e,ScaleV,OffsetV,NameV,UnitV);
        
        anApplet.data3d = data3d;
        anApplet.Elements = Elements;
        anApplet.Times = Times;
        anApplet.SizeX = SizeX;
        anApplet.SizeY = SizeY;
        anApplet.SizeZ = SizeZ;
        anApplet.redEl = redEl;
        anApplet.greenEl = greenEl;
        anApplet.blueEl = blueEl;
        anApplet.initLayout(Times);
        if (Elements <= 5)
            data3d.elementsLinked=false;
        else
            data3d.elementsLinked=true;
        if ((Elements > 1 && Elements < 5) || redEl >= 0)
    		{
    		    data3d.ToggleColor(true);  // switch to Color
                    // data3d.AdjustThresh(true); // initThresh();
	    	}
	    	else
	    	    {
                    data3d.ToggleColor(false);  // switch to BW
                    // data3d.AdjustThresh(true); // initThresh();
                }
//       data3d.AdjustThreshFlexible(); // .initThresh();
        // System.out.println("Layout ready");
        // anApplet.mypan.setVisible(true);  // does not work ???
        // System.out.println("started");
        // setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);  // releases all resources occupied
        /*anApplet.addWindowListener(new WindowListener ()
        {
            public void windowClosing(WindowEvent we)
            {
                System.exit(0);
            }
        }); */
        return anApplet;
    }


  //   The stuff below relies heavily on this class being a decendent of Applet. It can be removed and the View5D class be extending Container
  // see also: https://www.oracle.com/technetwork/java/javase/migratingfromapplets-2872444.pdf

  // Container ExtraWindows;
  int ParseInt(String s, boolean dowarn, int adefault) {  // a version of ParseInt, which performs a check only if required
    int result;
      try 
        {result = Integer.parseInt(getParameter(s));}
      catch(Exception e)
        {
            if (dowarn)
            {
            System.out.println("ParseInt: Caught Exceptionlooking for Parameter " + s +":"+e.getMessage());
            e.printStackTrace();
            }
            result = adefault;
	}
    return result;
  }
  
  double ParseDouble(String s, boolean dowarn, double adefault) {  // a version of ParseInt, which performs a check only if required
    double result;
      try 
        {result = Double.valueOf(getParameter(s)).doubleValue();}
      catch(Exception e)
        {
            if (dowarn)
            {
            System.out.println("ParseDouble: Caught Exception looking for Parameter " + s +":"+e.getMessage());
            e.printStackTrace();
            }
            result = adefault;
	}
    return result;
  }
  
  String ParseString(String s, boolean dowarn, String adefault) {  
    String result=null;
      try 
        {result = getParameter(s);}
      catch(Exception e)
        {
            if (dowarn)
            {
            System.out.println("ParseString: Caught Exception looking for Parameter " + s +":"+e.getMessage());
            e.printStackTrace();
            }
            result = adefault;
	}
    if (result == null)
            result = adefault;
        
    return result;
  }
  // stop commenting here

  public String StringFromType(int TypeNr)
  {
  	if (TypeNr >= 0 && TypeNr < AnElement.NumTypes)
	{
	   return AnElement.TypeNames[TypeNr];
	}
	else
	{
	 System.out.println("Error in StringFromType: Unknown datatype: "+TypeNr+"\n");
	 return "";
	}
  }
  
  public int TypeFromString(String AType)
  {
  	for (int i=0;i<AnElement.NumTypes;i++)
	   if (AType.equals(AnElement.TypeNames[i]) || AType.equals(AnElement.UTypeNames[i]))  // unsigned types are converted to signed
	   	return i;
	System.out.println("Error: Unknown datatype: "+AType+"\n");
	return -1;
  }

  public void setFontSize(int FontSize) {
      data3d.setFontSize(FontSize);
   }

  public void initLayout(int Times) {
                    // data3d.initGlobalThresh();
                    
		setLayout(new BorderLayout());
		mypan = new ImgPanel(this, data3d);
        // System.out.println("CP 6 "+mypan);
        panels.addElement(mypan);  // enter this view into the list
		add("Center", mypan);
        myLabel=new TextArea("5D-viewer Java Applet by Rainer Heintzmann, [press '?' for help]",1,76,TextArea.SCROLLBARS_NONE);
        add("North", myLabel);
 		mypan.CheckScrollBar(); 
                //Frame myfr=new Frame("View5D menu");
                //myfr.setMenuBar(mypan.MyMenu);
                //myfr.setSize(this.getSize().width,20);
                //myfr.show();
                
        setVisible(true);
  }

  //   The stuff below relies heavily on this class being a decendent of Applet. It is removed for now, as applets are deprecated
  // This can be commented out and the View5D class can be changed to Component
  // see also: https://www.oracle.com/technetwork/java/javase/migratingfromapplets-2872444.pdf
  public void init() {
	 if (System.getProperty("java.version").compareTo("1.4") < 0)  // this viewer should work from version 1.4 on
	     {
	 	setLayout(new BorderLayout());
	 	add("North", new ImageErr());
	 	setVisible(true);
	     }
	 else
	    {
		filename = getParameter("file");
		if (filename == null) filename="xxx.raw";
		
		SizeX = Integer.parseInt(getParameter("sizex"));
		SizeY = Integer.parseInt(getParameter("sizey"));
		SizeZ = ParseInt("sizez",true,1);
                Times = ParseInt("times",false,1);
                defaultColor0 = ParseInt("defcol0",false,-1);
                Elements = ParseInt("elements",true,1);
        if (Elements < 5)
            data3d.elementsLinked=false;
        else
            data3d.elementsLinked=true;
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
		redEl = ParseInt("red",false,redEl);
		greenEl = ParseInt("green",false,greenEl);
		blueEl = ParseInt("blue",false,blueEl); 
		    
		int DataType=AnElement.ByteType;
		int DataTypeN;
                int NumBytes=1;
		int NumBits=8;
		NumBytes = ParseInt("bytes",false,NumBytes);  // 0,1 means byte, 2= short, -1 = float
		NumBits = ParseInt("bits",false,NumBits);  
                if (NumBytes > 1) {
                	if (NumBytes > 1) {
				DataType=AnElement.IntegerType; }
			else {
				DataType=AnElement.ShortType; }
			}
                else if (NumBytes == -1) {DataType=AnElement.FloatType;NumBytes=4;NumBits=32;}
                if (NumBytes*8 < NumBits) NumBits = NumBytes*8;
		
		String DatType = ParseString("dtype",false,StringFromType(DataType));  // 0: byte, 1: int, 2: float, 3: double, 4: complex(float)
		
		DataTypeN = TypeFromString(DatType);
		if (DataTypeN != DataType) 
		{
		   DataType = DataTypeN;
		   if (DataType == AnElement.IntegerType && NumBytes <1)
		   	NumBytes=2;
		   if (DataType == AnElement.ByteType)
		   	NumBytes = 1;
		   if (DataType == AnElement.FloatType)
		   	NumBytes = 4;
		   if (DataType == AnElement.DoubleType)
		   	NumBytes = 8;
		   if (DataType == AnElement.ComplexType)
		   	NumBytes = 8;
		   if (DataType == AnElement.ShortType)
		   	NumBytes = 2;
		   if (DataType == AnElement.LongType)
		   {
		   	DataType=AnElement.IntegerType;
		   	NumBytes = 4;
		   }
		}
		
                double[] Scales = new double[5];
                for (int j=0;j<5;j++) Scales[j]=1.0;                
                double[] Offsets = new double[5];
                for (int j=0;j<5;j++) Offsets[j]=0.0;
                double ScaleV=1.0,OffsetV=0.0;
                
                Scales[0] = ParseDouble("scalex",false,1.0);  // scaling
		Scales[1] = ParseDouble("scaley",false,1.0);  
		Scales[2] = ParseDouble("scalez",false,1.0);
		Scales[3] = ParseDouble("scalee",false,1.0);
		Scales[4] = ParseDouble("scalet",false,1.0);
                Offsets[0] = ParseDouble("offsetx",false,0.0);  // scaling
		Offsets[1] = ParseDouble("offsety",false,0.0);  
		Offsets[2] = ParseDouble("offsetz",false,0.0);
		Offsets[3] = ParseDouble("offsete",false,0.0);
		Offsets[4] = ParseDouble("offsett",false,0.0);
                String [] Names = new String[5];
                String [] Units = new String[5];
                String NameV = "intensity";
                String UnitV = "a.u.";
                
                Names[0] = ParseString("namex",false,"X");
                Names[1] = ParseString("namey",false,"Y");
                Names[2] = ParseString("namez",false,"Z");
                Names[3] = ParseString("namee",false,"Elements");
                Names[4] = ParseString("namet",false,"Time");
                Units[0] = ParseString("unitsx",false,"pixels");
                Units[1] = ParseString("unitsy",false,"ypixels");
                Units[2] = ParseString("unitsz",false,"zpixels");
                Units[3] = ParseString("unitse",false,"elements");
                Units[4] = ParseString("unitst",false,"time");
                
		if (SizeX <= 0) SizeX = 256;
		if (SizeY <= 0) SizeY = 256;
		if (SizeZ <= 0) SizeZ = 10;

                int HistoX = 0;
                int HistoY = -1;
                int HistoZ = -1;
                if (Elements > 1)
                    HistoY = 1;
                // if (Elements > 2)    HistoZ = 2;  // the user will have to choose
                
                HistoX = ParseInt("histox",false,HistoX);  
		HistoY = ParseInt("histoy",false,HistoY);  
		HistoZ = ParseInt("histoz",false,HistoZ);  
		
                data3d = new My3DData(this,SizeX,SizeY,SizeZ,
                                    Elements,Times,
                                    redEl,greenEl,blueEl,
                                    HistoX,HistoY,HistoZ,DataType,NumBytes,NumBits, 
                                    Scales,Offsets,
                                    ScaleV,OffsetV,Names,Units);

                if(defaultColor0 >0) 
			data3d.ToggleModel(0,defaultColor0);  
                
                for (int e=0;e<Elements;e++)
                {
                    ScaleV = ParseDouble("scalev"+(e+1),false,1.0); 
                    OffsetV = ParseDouble("offsetv"+(e+1),false,0.0); 
                    NameV = ParseString("namev"+(e+1),false,"intensity"); 
                    UnitV = ParseString("unitsv"+(e+1),false,"a.u."); 
                    data3d.SetValueScale(e,ScaleV,OffsetV,NameV,UnitV);
                }

            data3d.Load(DataType,NumBytes,NumBits,this.getDocumentBase() + filename);  // "file://"+filename

		String MyMarkerIn = getParameter("markerInFile");
		if (MyMarkerIn != null) 
			{
			data3d.markerInfilename=MyMarkerIn;
			System.out.println("... loading marker file "+data3d.markerInfilename);
				data3d.LoadMarkers();
			}

		String MyMarkerOut = getParameter("markerOutFile");
		if (MyMarkerOut != null) 
			data3d.markerOutfilename = MyMarkerOut;

        initLayout(Times);
        if (Elements < 5)
            data3d.elementsLinked=false;
        else
            data3d.elementsLinked=true;
        if ((Elements > 1 && Elements < 5) || redEl >= 0)
		    {
            data3d.ToggleColor(true);  // switch to Color
                    // data3d.AdjustThresh(true); // initThresh();
		    }
		    else
                {
                    data3d.ToggleColor(false);  // switch to BW
                    // data3d.AdjustThresh(true); // initThresh();
                }                
	    }
    }
    // here you should end the commend, when removing the applet-dependent functions

    public void setElement(int e) {
       if (e<0) {
           e = getNumElements()-1;
       }
       data3d.setElement(e);
    }

    public void setTime(int t) {
        if (t<0) {
            t = getNumTime()-1;
        }
        data3d.setTime(t);
    }

    // The function below does not work. Updates to the crosshair seem improssible from outside
    public void setPosition(int x, int y, int z, int e, int t) {
        //mypan.RememberOffset(); // panels.elementAt(0)
        //mypan.AdjustOffset();
        //mypan.c1.PositionValue = z;
        //mypan.c2.PositionValue = x;
        //mypan.c3.PositionValue = y;
        setElement(e);
        setTime(t);
        // for some reason "mypanel" does not do the trick, but the panel list "panels" works fine.
        for (int i=0;i<panels.size();i++) { // panels.size()
            ImgPanel pan = ((ImgPanel) panels.elementAt(i)); // .c1.UpdateAllNoCoord();
            //System.out.println("current position: " + pan.c1.PositionValue + ", " + pan.c2.PositionValue + ", " + pan.c3.PositionValue);

            pan.setPositions(new APoint(x, y, z, e, t));
            //System.out.println("Parameters ARE: " + x + ", " + y + ", " + z + ", " + e + ", " + t);

            // data3d.InvalidateSlices();
            pan.c1.label.CoordsChanged();
            //System.out.println("Parameters ARE: " + pan.c1.PositionValue + ", " + mypan.c2.PositionValue + ", " + mypan.c3.PositionValue);
            pan.c1.CalcPrev();
            pan.c1.label.CoordsChanged();
            pan.c1.UpdateAll();
        }
        // mypan.c1.repaint();
        // mypan.c2.repaint();
        // mypan.c3.repaint();
    }

    public void start() {
		data3d.AdjustThresh(); // initThresh();
		mypan.InitScaling();
    }

    public void stop() {
    }

    public void destroy() {
       data3d.cleanup();
    }

    public My3DData get_data() {
        return data3d;
    }

    public Bundle get_bundle_at(int ne) {
        return data3d.GetBundleAt(ne);
    }

    public void set_colormap_no(int nr, int elem) {
        data3d.ToggleModel(elem, nr);
        mypan.c1.UpdateAll();
    }

    public int get_active_element() {
        return data3d.ActiveElement;
    }
    public int get_active_time() {
        return data3d.ActiveTime;
    }

    public void add_colormap(int sz, byte reds[], byte greens[], byte blues[], int type) {
        int lastLUT=data3d.AddLookUpTable(sz, reds, greens, blues, type);
        NumberFormat  nf = java.text.NumberFormat.getNumberInstance(Locale.US);
        String LUTName="User defined "+nf.format(lastLUT - Bundle.ElementModels+1);
        // System.out.println("CP 2 "+LUTName);
        //((ImgPanel) panels.firstElement()).c1.label.PixDisplay.AddColorMenu(LUTName,lastLUT);
        //((ImgPanel) panels.elementAt(0)).c1.label.PixDisplay.AddColorMenu(LUTName,lastLUT);
        // mypan.c1.myPanel.label.PixDisplay.AddColorMenu(LUTName, lastLUT);
        // No Idea why the below does not work:
        mypan.label.PixDisplay.AddColorMenu(LUTName, lastLUT);
        data3d.SetColorModelNr(data3d.ActiveElement, lastLUT);
		if (! data3d.SetThresh(0, sz-1))  // Set to the correct initial thresholds
		    data3d.InvalidateSlices();
        mypan.c1.UpdateAll();
    }

    public String getAppletInfo() {
        return "A 5Dimage viewing tool.";
    }

}
