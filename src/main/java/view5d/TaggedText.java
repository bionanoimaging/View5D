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

// import java.io.*;
import java.awt.*;

class TaggedText extends TaggedComponent {  // this is a component with a tag, capable of generating a part of a call
	static final long serialVersionUID = 1;
	public TaggedText(String tag, String label, String value) {
	    super(tag,label,null); 
	    name="text";
	    if ((value != null) && !value.equals("none") && !value.equals(""))
		{
		    AddComponent(new TextField(20));
		    ((TextField) mycomp).setText(value);
		}
	}
    public void setValue(String val)  // converts value to component attributes
    {
	if (val.equals("none"))
	    val="";
	if (mycomp != null)
	    ((TextField) mycomp).setText(val);
    }
    public String getTextValue() {        // converts component attributes to value
	String ret="";
	if (mycomp != null)
	    ret= ((TextField) mycomp).getText();
	return ret;
    }    
}
