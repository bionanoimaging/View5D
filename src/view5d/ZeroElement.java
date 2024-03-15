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

class ZeroElement extends AnElement {
    ZeroElement(int SX, int SY, int SZ) {super(SX,SY,SZ,0.0);DataType=FloatType;}
    void Clear() {return;}
    void DeleteData() {return;}
    int GetStdByteNum() {return 0;}
    void ConvertSliceFromSimilar(int param, int bufslice, Object values, int mstep, int moff) {return;}
    void ConvertSliceFromByte(int myslice, int bufslice, byte [] Ibuffer, int mstep, int moff) {return;}
    void ConvertSliceFromRGB(int myslice, int bufslice, int [] Ibuffer, int mstep, int moff, int soff)    {throw new IllegalArgumentException("Int: Inapplicable conversion\n");}
    int GetByteValueAt(int param, int param1, int param2) {return 0;}
    int GetIntValueAt(int param, int param1, int param2) {return 0;}
    double GetRawValueAt(int param, int param1, int param2) {return 0.0;}
    double GetValueAt(int param, int param1, int param2) {return 0.0;}
    void SetValueAt(int param, int param1, int param2, double param3) {return;}
    void CopySliceToSimilar(int myslice, Object buffer)  {return;}
}
