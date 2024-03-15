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

//import java.io.*;
//import java.lang.*;
// import java.lang.Number.*;
// import java.awt.image.ColorModel.*;
// import java.awt.color.*;

import java.awt.*;

// Tagged component classes, taken from my JFlow project and simplified
class TaggedComponent extends Panel { // a general superclass if called with component==null it can be used as text 
    static final long serialVersionUID = 1;
    GridBagLayout gridbag;
    GridBagConstraints c;
    String name;
    String mytag;
    Label mylabel;
    Component mycomp;
    public TaggedComponent(String tag, String label, Component acomp)
    {
	super();
	name="tcomp";
	mytag = tag;
	mylabel = new Label(label);
	mycomp = acomp;

        gridbag = new GridBagLayout();
	c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	c.gridwidth = GridBagConstraints.REMAINDER;
	c.anchor = GridBagConstraints.NORTHWEST;
	c.weightx = 0.0;  // each component in a row !
	// setFont(new Font("Helvetica", Font.PLAIN, 14));
	setLayout(gridbag);

	if (! label.equals("none"))
	    add(mylabel);
	if (mycomp != null)
	    add(mycomp);
    }
    public void AddComponent(Component acomp)
    {
	mycomp = acomp;
	if (mycomp != null)
	    add(mycomp);
    }
    public void setValue(String val)  // converts value to component attributes
    {
	if (val.equals("none"))
	    return;
	System.out.println("Error Tagged Component setValue called\n"); 
    }
    public Object getValue() {        // converts component attributes to value
	// System.out.println("Error Tagged Component getValue called\n"); 
	return null;
    }
    public String getDescription() {
	String ret=name+" ";
	if(mytag.equals(""))
	    ret+="none ";
	else
	    ret+="\""+mytag+"\" ";
	if(mylabel.equals(""))
	   ret+="none ";
	else
	    ret+="\""+mylabel.getText()+"\" ";
	/*String tmp=getValue();
	if(tmp.equals(""))
	   ret+="none";
	else
	    ret+="\""+tmp+"\"";
         */
	return ret;
    }
}
