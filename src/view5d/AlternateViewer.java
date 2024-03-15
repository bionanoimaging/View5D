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
/****************************************************************************
 *   Copyright (C) 1996-2007 by Rainer Heintzmann                          *
 *   heintzmann@gmail.com                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************
*/
// By making the appropriate class "View5D" or "View5D_" public and renaming the file, this code can be toggled between Applet and ImageJ respectively

//import java.io.*;
// import java.awt.image.ColorModel.*;
package view5d;

import java.awt.event.*;
// import java.awt.color.*;
import java.awt.*;

public class AlternateViewer extends Frame implements WindowListener {
 static final long serialVersionUID = 1;
 My3DData cloned=null;
 Component mycomponent=null;
 Container applet;
    public AlternateViewer(Container myapplet) {
    super("Alternate Viewer");
    applet = myapplet;
    //if (myapplet != null)
    //    setSize(myapplet.getBounds().width,myapplet.getBounds().height);
    //else
     Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
     setSize((int) (size.getWidth()/2),(int) (size.getHeight()/1.5)); // Heuristics
    // setSize(500,500);
    setVisible(true);
    addWindowListener(this); // register this class for handling the events in it
    }

    public AlternateViewer(Container myapplet, int width, int height) {
    super("Alternate Viewer");
    applet = myapplet;
    setSize(width,height);
    setVisible(true);
    addWindowListener(this); // register this class for handling the events in it
    }
      
    public void Assign3DData(Container myapplet, ImgPanel ownerPanel, My3DData cloneddata) {
    cloned = cloneddata; // new My3DData(datatoclone);
    ImgPanel np=new ImgPanel(myapplet,cloneddata);
    if (!(applet instanceof View5D))
	   ((View5D_) applet).panels.addElement(np);  // enter this view into the list
    else
	   ((View5D) applet).panels.addElement(np);  // enter this view into the list
    np.OwnerPanel(ownerPanel);
    mycomponent=np;
    np.CheckScrollBar();
    add("Center", np);	
    setVisible(true);
    }

    public void AssignPixelDisplay(PixelDisplay pd) {
    mycomponent=pd;
    pd.setBounds(getBounds());
    add("Center", pd);
    // ((PixelDisplay) mycomponent).c1.myPanel.label.doLayout();
    }

    public void windowActivated(java.awt.event.WindowEvent windowEvent) {
    }
    
    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
        cloned=null; // This deletes the data
    if (!(applet instanceof View5D))
	   ((View5D_) applet).panels.removeElement(mycomponent);  // remove this view from the list
    else
	   ((View5D) applet).panels.removeElement(mycomponent);  // remove this view from the list
    if (applet instanceof View5D) {
            ((View5D) applet).Elements = -1;
            ((View5D) applet).Times = -1;
        }

    }
    
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
    if (mycomponent instanceof PixelDisplay)
    {
        ((PixelDisplay) mycomponent).c1.myPanel.label.add(mycomponent);
        // ((PixelDisplay) mycomponent).c1.myPanel.label.preferredSize();
        GridLayout myLayout=new GridLayout(2,2);   // back to the old layout
        ((PixelDisplay) mycomponent).c1.myPanel.label.setLayout(myLayout);
        ((PixelDisplay) mycomponent).c1.myPanel.label.doLayout();
        ((PixelDisplay) mycomponent).c1.myPanel.label.repaint();
    }
                
    if (cloned != null)
        if (cloned.DataToHistogram != null)
        {
            if (cloned.DataToHistogram.MyHistogram == cloned)
                    cloned.DataToHistogram.MyHistogram = null;
        }
    else {
            if (applet instanceof View5D) {
                ((View5D) applet).closeAll();
            }
        }
    setVisible(false);
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
}
