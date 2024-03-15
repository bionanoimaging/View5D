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
package view5d;

// import java.io.*;

public class ByteElement extends AnElement {
  public byte [] myData;        // holds the 3D byte data
  int SizeXY;
  
  ByteElement(int SX, int SY, int SZ) {
      super(SX,SY,SZ,256.0);
      myData = new byte[Sizes[0]*Sizes[1]*Sizes[2]];
      SizeXY = Sizes[0]*Sizes[1];
      DataType = ByteType; 
  }
  
  void Clear() {
    for (int i = 0; i < Sizes[0]*Sizes[1]*Sizes[2];i++) 
            myData[i] = 0;
    } 

  void DeleteData() {
            myData = null;
  }
  
  int GetStdByteNum() {return 1;}

  void SetValueAt(int x, int y, int z, double val)
  {
      if (val < 0.0) val = 0.0;
      if (val > 255) val = 255;
      myData[x+Sizes[0]*y+SizeXY*z]= (byte) val;
  }
  
  int GetIntValueAt(int x, int y, int z)  // scaled to 16 bit integer (for pseudocolor display)
  {
      int val = myData[x+Sizes[0]*y+SizeXY*z] & 0xff;
      // if (val < 0) val += 256;
      return (int)((val - shift) * scaleI);
  }
  int GetByteValueAt(int x, int y, int z)
  {
      int val = myData[x+Sizes[0]*y+SizeXY*z] & 0xff;
      // if (val < 0) val += 256;
      return (int) ((val-shift) * scaleB);
  }

  double GetRawValueAt(int x, int y, int z)
  {
      double val = myData[x+Sizes[0]*y+SizeXY*z] & 0xff;
      // if (val < 0 ) val += 256;
      return val;
  }

  double GetValueAt(int x, int y, int z)
  {
      int val = myData[x+Sizes[0]*y+SizeXY*z] & 0xff;
      // if (val < 0) val += 256;
      return val*ScaleV+OffsetV;
  }
        
  void ConvertSliceFromSimilar(int myslice, int bufslice, Object Ibuffer, int mstep, int moff) {
    // System.out.println("Byte Converting "+SizeXY+"\n");
    byte [] mbuffer = (byte []) Ibuffer;
    for (int i=0;i<SizeXY;i+=mstep)
        myData[i+Sizes[0]*Sizes[1]*myslice] = mbuffer[bufslice*SizeXY+i+moff]; 
    }
  
  void ConvertSliceFromByte(int myslice, int bufslice, byte [] Ibuffer, int mstep, int moff)
    {
      for (int i=0;i<SizeXY;i+=mstep)
        myData[i+Sizes[0]*Sizes[1]*myslice] = Ibuffer[bufslice*SizeXY+i+moff]; 
    }

  void ConvertSliceFromRGB(int myslice, int bufslice, int [] Ibuffer, int mstep, int moff, int suboff)  // suboff defines which byte to use
    {
      int bitshift=suboff*8;
      for (int i=0;i<SizeXY;i+=mstep)
          myData[i+Sizes[0]*Sizes[1]*myslice] = ((byte) ((Ibuffer[bufslice*SizeXY+i+moff] >> bitshift) & 0xff)) ; 
    }
  
    void CopySliceToSimilar(int myslice, Object buffer)  
    {
      byte [] mbuffer = (byte[]) buffer;
      for (int i=0;i<SizeXY;i++)
          mbuffer[i]=myData[i+Sizes[0]*Sizes[1]*myslice]; 
    }
  }
