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


public class UnsignedShortElement extends AnElement {
    char [] myData;        // holds the 3D 16 bit data. Java does not have unsigned datatypes but char is an unsigned short in Java
    int NumBytes=2;
    int SizeXY;

    UnsignedShortElement(int SX, int SY, int SZ) {
        super(SX,SY,SZ,65536.0);
        myData = new char[Sizes[0]*Sizes[1]*Sizes[2]];
        SizeXY = Sizes[0]*Sizes[1];
        DataType = UnsignedShortType;
        NumBytes = 2;
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
        if (val > MaxValue-1) val = MaxValue-1;
        myData[x+Sizes[0]*y+SizeXY*z]= (char) val;
    }

    int GetIntValueAt(int x, int y, int z)  // scaled to 16 bit integer (for pseudocolor display)
    {
        int val = myData[x+Sizes[0]*y+SizeXY*z];
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
        double val = myData[x+Sizes[0]*y+SizeXY*z];
        // if (val < 0 ) val += 256;
        return val;
    }

    double GetValueAt(int x, int y, int z)
    {
        int val = myData[x+Sizes[0]*y+SizeXY*z];
        // if (val < 0) val += 256;
        return val*ScaleV+OffsetV;
    }

    void ConvertSliceFromSimilar(int myslice, int bufslice, Object Ibuffer, int mstep, int moff) {
        // System.out.println("Byte Converting "+SizeXY+"\n");
        if (Ibuffer instanceof short[]) {
            short [] mbuffer;
            mbuffer = (short []) Ibuffer;
            for (int i=0;i<SizeXY;i+=mstep)
                myData[i+Sizes[0]*Sizes[1]*myslice] = (char) mbuffer[bufslice*SizeXY+i+moff];
        } else {
            char [] mbuffer;
            mbuffer = (char[]) Ibuffer;
            for (int i=0;i<SizeXY;i+=mstep)
                myData[i+Sizes[0]*Sizes[1]*myslice] = mbuffer[bufslice*SizeXY+i+moff];
        }
    }

    void ConvertSliceFromByte(int myslice, int bufslice, byte [] Ibuffer, int mstep, int moff)
    {
        for (int i=0;i<SizeXY;i+=mstep)
            myData[i+Sizes[0]*Sizes[1]*myslice] = (char) Ibuffer[bufslice*SizeXY+i+moff];
    }

    void ConvertSliceFromRGB(int myslice, int bufslice, int [] Ibuffer, int mstep, int moff, int suboff)  // suboff defines which char to use
    {
        int bitshift=suboff*8;
        for (int i=0;i<SizeXY;i+=mstep)
            myData[i+Sizes[0]*Sizes[1]*myslice] = ((char) ((Ibuffer[bufslice*SizeXY+i+moff] >> bitshift) & 0xff)) ;
    }

    void CopySliceToSimilar(int myslice, Object buffer)
    {
        char [] mbuffer = (char[]) buffer;
        for (int i=0;i<SizeXY;i++)
            mbuffer[i]=myData[i+Sizes[0]*Sizes[1]*myslice];
    }
}
