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

class TaggedChoice extends TaggedComponent {  // this is a component with a tag, capable of generating a part of a call
    static final long serialVersionUID = 1;
    public TaggedChoice (String tag, String label, String[] items, String value) {
   	super(tag,label,new Choice());
        if (items == null) return;
        Choice thisChoice = ((Choice) mycomp);
        //thisChoice.addKeyListener(this);
        //thisChoice.addItemListener(this);
        for (int i=0; i<items.length; i++)
                        thisChoice.addItem(items[i]);
	name = "check";
	setValue(value);
    }
  
   public void setValue(String val)  // converts value to component attributes
    {
	((Choice) mycomp).select(val);
    }
    public int getChoiceValue() {        // converts component attributes to value
	return (((Choice) mycomp).getSelectedIndex());
    }
  }
