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

class RectROI extends ROI {
    int ProjMin[], ProjMax[];
     RectROI() {
         super();
     }
     
    double GetROISize(int dim) {
        return (ProjMax[dim] - ProjMin[dim])+1;  // returns number of pixels including both borders
     }

    public Rectangle GetSqrROI(int dim) {
        if (ProjMin == null)
            return null;
        if (dim == 0)
            return new Rectangle(ProjMin[2],ProjMin[1],
            (int) GetROISize(2)-1,(int) GetROISize(1)-1);
        if (dim == 1)
            return new Rectangle(ProjMin[0],ProjMin[2],
            (int) GetROISize(0)-1,(int) GetROISize(2)-1);
        if (dim == 2)
            return new Rectangle(ProjMin[0],ProjMin[1],
            (int) GetROISize(0)-1,(int) GetROISize(1)-1);
        return null;
    }
    

    void TakeSqrROIs(int Pmin[], int Pmax[]) {
        if (ProjMin == null)
        {
            ProjMin = new int[3];
            ProjMax = new int[3];
        }
        for (int d=0;d<3;d++)
        {
            ProjMin[d] = Pmin[d];ProjMax[d] = Pmax[d];
        }
    }

    public void UpdateSqrROI(int ROIX,int ROIY, int ROIXe, int ROIYe,int dir)
    {
        if (dir ==0)
        {
            ProjMin[2] = ROIX;ProjMax[2] = ROIXe;
            ProjMin[1] = ROIY;ProjMax[1] = ROIYe;
        }
        else if (dir==1)
        {
            ProjMin[0] = ROIX;ProjMax[0] = ROIXe;
            ProjMin[2] = ROIY;ProjMax[2] = ROIYe;
        }
        else
        {
            ProjMin[0] = ROIX;ProjMax[0] = ROIXe;
            ProjMin[1] = ROIY;ProjMax[1] = ROIYe;
        }
    }

    boolean InROIRange(int x,int y,int z) {
         if  (ProjMin == null)
             return true;
         if (x < ProjMin[0])
             return false;
         if (y < ProjMin[1])
             return false;
         if (z < ProjMin[2])
             return false;
         if (x > ProjMax[0])
             return false;
         if (y > ProjMax[1])
             return false;
         if (z > ProjMax[2])
             return false;
         return true;
     }
}
