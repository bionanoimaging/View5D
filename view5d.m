%VIEW5D Start the java viewer by Rainer Heintzmann
%
% VIEW5D(in,ts,mode,myViewer,element) starts a java based image viewer
%
% PARAMETERS:
%   in:   can either be an image or a figure handle
%   ts:   is the input a time series? (0,1)
%   mode: 'direct', 'newElement', 'replaceElement' or 'extern', for, respectively, starting the Java applet
%         directly, directly into a new element, replacing element in MATLAB or stating it in your web browser.
%   myViewer: (only for newElement' or 'replaceElement') a previously created view5d instance.
%   element: (only for 'replaceElement'), the element number to replace the data
%
% RETURNS:
%   a java instance of the viewer.
%
% DEFAULTS:
%   ts:   0
%   mode: 'direct'
%
% See also DIPSHOW.
%
% NOTES:
%   To use the external viewer, configure your MATLAB to find your web browser,
%   see DOCOPT. Make sure your web browser has java enabled.
%
%   If there is no java support in MATLAB, the external mode is always used.
%
%   For larger images you may get java.lang.OutOfMemoryError
%   Increase the memory for the jvm by placing the file java.opts in your
%   MATLAB directory with -Xmx#bytes (java 1.3) or maxHeapSize=#bytes (older)
%   Set #bytes to something large (600000000).
%
% More information about the viewer
%   http://www.nanoimaging.de/View5D/
%
% OPTIONAL INSTALLATION INSTRUCTIONS:
%   This part is optional, but might be necessary for earlier versions of
%   MATLAB. In short, if you get error messages when trying to use this
%   function, follow these instructions:
%
%   You can to modify the MATLAB file 'classpath.txt' to use the Java applet
%   View5D.jar (see Help > MATLAB > External Interfaces > Using Sun Java...)
%   Copy the file 'classpath.txt' to $HOME/matlab. To find this file, type:
%   >> which classpath.txt
%   at the MATLAB command prompt. Edit your copy of this file, and add the full
%   path to the View5D.jar file to the end of the list. The line you have to add
%   you can generate by typing:
%   >> fullfile(fileparts(which('view5d')),'private','View5D.jar')
%   at the MATLAB command promt.

% (C) Copyright 1999-2009               Pattern Recognition Group
%     All rights reserved               Faculty of Applied Physics
%                                       Delft University of Technology
%                                       Lorentzweg 1
%                                       2628 CJ Delft
%                                       The Netherlands
%
% Bernd Rieger, Rainer Heintzmann
% Oct 2004, Fixed startup behavior
% June 2005, Fixed Bug with multi-time data (tag is "times")
% June 2005, Added support of complex and double datatypes
% March 2007, Updated for new View5D (RH)
% August 2007, Changed the parsing of inputs, removed function VIEW5D_WINDOW,
%              using web browser as external viewer, using absolute paths with
%              external viewer, removed output argument. (CL)
% 2 December 2008, Setting dynamic Java path, no need to edit classpath.txt. (CL)
% 17 Feb 2009, added return of figure handle (BR)
% 17 December 2009: Using new function MATLABVER_GE.
% 2019: Added functionality both on the Matlab and Java side. Allowing for live uptdates. Fixed a bug with sint16 display. Rainer Heitzmann

function out=view5d(varargin)

% Parse input
whe = 'direct';
ts = 0;
ElementNum=0;

switch nargin
case 1
case 2
   if ischar(varargin{2})
      whe = varargin{2};
   else
      ts = varargin{2};
   end
case 3
   ts = varargin{2};
   whe = varargin{3};
case 4
   ts = varargin{2};
   whe = varargin{3};
   myViewer = varargin{4};
case 5
   ts = varargin{2};
   whe = varargin{3};
   myViewer = varargin{4};
   ElementNum = varargin{5};
otherwise
   error('Too few/many input arguments');
end
in = varargin{1};
if ~ischar(whe)
   error('MODE string should be ''direct'' or ''extern''.')
end
switch whe
   case 'direct'
      direct = 1;
   case 'newElement'
      direct = 1;
      if nargin < 4
          error('The option ''newElement'' requires view5d to be called with a java instance as 4th argument')
      end
   case 'newTime'
      direct = 1;
      if nargin < 4
          error('The option ''newTime'' requires view5d to be called with a java instance as 4th argument')
      end
   case 'replaceElement'
      direct = 1;
      if nargin < 4
          error('The option ''replaceElement'' requires view5d to be called with a java instance as 4th argument')
      end
      if nargin < 5
          ElementNum=0;
      end
   case 'extern'
      direct = 0;
   otherwise
      error('MODE string should be ''direct'', ''newElement'', ''newTime'', ''replaceElement'' or ''extern''.')
end

if direct
   % Check for Java
   if exist('javachk','file')
      tmpj = isempty(javachk('jvm'));
   else
      tmpj = 0;
   end
   if ~tmpj
      disp('Using the external viewer, MATLAB started without java support.');
      direct = 0;
   end
end
if direct
   try
      % Add the path if not known
      jp = javaclasspath('-all');
      jarfile = jarfilename;
      if ~any(strcmp(jp,jarfile))
         if ~any(endsWith(jp,'View5D.jar'))
            javaaddpath(jarfile);
         end
      end
      % Force the loading of the JAR file
      import view5d.*
   catch
      error(['Please update your classpath.txt file as explained in the',10,'help to this function, or use the external viewer'])
   end
end

% Prepare input image
if ishandle(in)
   in = dipgetimage(in);
else
   in = dip_image(in);
end
if isa(in,'cuda')
    in = dip_image_force(in);
end
if ~isscalar(in) && ~isvector(in)
   error('Input image must be a scalar or vector image.')
end
sz = imsize(in);
if length(sz)>5 || length(sz)<2
   error('Only available for 2, 3, 4 and 5D images.');
end
if any(sz(1:2)==1)
   error('First two image dimensions must be larger than 1.');
end
if isvector(in)
   % The array elements need to go along the 4th dimension, and not become the time axis later on.
   if length(sz)>4
      error('Only available for 2, 3 and 4D tensor images.');
   end
   if length(sz)<3
      %#function expanddim
      in = iterate('expanddim',in,3);
   end
   in = array2im(in);
   if ndims(in)==5 && size(in,5)>1 && size(in,4)==1
      in = permute(in,[1,2,3,5,4]); % the new elements dimension is 5th, should be 4th.
   end
   elements = 1;
else
   elements = 0;
end
in = expanddim(in,5); % make sure the input has 5 dimensions
if strcmp(datatype(in),'dcomplex')
   in = dip_image(in,'scomplex'); % dcomplex not supported
end
sz = imsize(in);
if ts
   % The user asks for a time series -- move the last data dimension to the 5th dimension
   t = find(sz>1); t = t(end);
   if t<3 || t==5
      t = [];
   elseif t==4 && elements
      sz(t) = 1;
      t = find(sz>1); t = t(end);
      if t<3
         t = [];
      end
   end
   if ~isempty(t)
      order = 1:5;
      order(t) = 5;
      order(5) = t;
      in = permute(in,order);
   end
end

if strcmp(whe,'newElement')
%    fprintf('Adding Element: SizeE:%d, SizeT:%d \n',sz(4),sz(5));
  if sz(4) > 1 || sz(5) > 1
    for t=1:sz(5)
        for e=1:sz(4)
%            fprintf('Adding Element: %d, Time: %d\n',e,t);
            myViewer=view5d(in(:,:,:,e-1,t-1),ts,'newElement',myViewer);
        end
    end
    out = myViewer;
    return
  end
end

if strcmp(whe,'newTime')
    % fprintf('Adding Time: SizeE:%d, SizeT:%d \n',sz(4),sz(5));
  if sz(5) > 1
    for t=1:sz(5)
        for e=1:sz(4)
            % fprintf('Adding ElemenTime: %d, Time: %d\n',e,t);
            myViewer=view5d(in(:,:,:,e-1,t-1),ts,'newTime',myViewer);
        end
    end
    out = myViewer;
    return
  end
end


% Start the applet
if direct
   if ~isreal(in)
      % Make a one dimensional flat input array
      inr = reshape(real(in),1,prod(sz));
      ini = reshape(imag(in),1,prod(sz));
      in5df = dip_array(reshape([inr ini],1,2*prod(sz)));      
      if strcmp(whe,'direct')
          out = View5D.Start5DViewerC(in5df,sz(1),sz(2),sz(3),sz(4),sz(5));
          out.AddElement(angle(dip_array(in(:))),sz(1),sz(2),sz(3),sz(4),sz(5));          
          ElementNum = [0:out.getNumElements()-1];
          out.ProcessKeyMainWindow('O'); % logarithmic display mode
          out.ProcessKeyMainWindow('e'); % advance an element
          for q=1:12
              out.ProcessKeyMainWindow('c'); % cyclic colormap
          end
          out.ProcessKeyMainWindow('t'); % min max adjustment
          out.ProcessKeyMainWindow('v'); % 
          out.ProcessKeyMainWindow('V'); % 
          out.ProcessKeyMainWindow('E'); % Back to previous element
          out.ProcessKeyMainWindow('C'); % multicolor on
          out.UpdatePanels();
      elseif strcmp(whe,'newElement')
          out = myViewer.AddElementC(in5df,sz(1),sz(2),sz(3),sz(4),sz(5));
          myphase = dip_array(reshape(angle(in),1,prod(sz)));
          out = out.AddElement(myphase,sz(1),sz(2),sz(3),sz(4),sz(5));          
          ElementNum = out.getNumElements()-1;
          out.ProcessKeyMainWindow('O'); % logarithmic display mode
          out.ProcessKeyMainWindow('e'); % advance an element
          for q=1:12
              out.ProcessKeyMainWindow('c'); % cyclic colormap
          end
          out.ProcessKeyMainWindow('t'); % min max adjustment
          out.ProcessKeyMainWindow('v'); % 
          out.ProcessKeyMainWindow('V'); % 
          out.ProcessKeyMainWindow('E'); % Back to previous element
          out.ProcessKeyMainWindow('C'); % multicolor on
      elseif strcmp(whe,'newTime')
          myViewer.setTimesLinked(0);
          out = myViewer.AddTime(in5df,sz(1),sz(2),sz(3),sz(4),sz(5));
          myphase = dip_array(reshape(angle(in),1,prod(sz)));
          out = out.AddElement(myphase,sz(1),sz(2),sz(3),sz(4),sz(5));          
          ElementNum = out.getNumElements()-1;
          out.ProcessKeyMainWindow('O'); % logarithmic display mode
          out.ProcessKeyMainWindow('e'); % advance an element
          for q=1:12
              out.ProcessKeyMainWindow('c'); % cyclic colormap
          end
          out.ProcessKeyMainWindow('t'); % min max adjustment
          out.ProcessKeyMainWindow('v'); % 
          out.ProcessKeyMainWindow('V'); % 
          out.ProcessKeyMainWindow('E'); % Back to previous element
          out.ProcessKeyMainWindow('C'); % multicolor on
      elseif strcmp(whe,'replaceElement')
          out = myViewer;
          % amplitude
          if (ElementNum >= out.getNumElements())
              out = myViewer.AddElementC(in5df,sz(1),sz(2),sz(3),sz(4),sz(5));
              ElementNum = out.getNumElements()-1;
          else
              myViewer.ReplaceDataC(ElementNum,0,in5df);
          end
          % now phase
          myphase = dip_array(reshape(angle(in),1,prod(sz)));
          ElementNum = ElementNum+1;
          if (ElementNum >= out.getNumElements())
              out = out.AddElement(myphase,sz(1),sz(2),sz(3),sz(4),sz(5));
              ElementNum = out.getNumElements()-1;
          else
              myViewer.ReplaceData(ElementNum,0,myphase);
          end
      end
   else
      % Make a one dimensional flat input array
      in5d = dip_array(reshape(in,1,prod(sz)));
      if isa(in5d,'uint16')
          in5d=char(in5d);  % only this way java understands this type...
      end
      if strcmp(whe,'direct')
          out = View5D.Start5DViewer(in5d,sz(1),sz(2),sz(3),sz(4),sz(5));
          ElementNum = [0:out.getNumElements()-1];
      elseif strcmp(whe,'newElement')          
          out = myViewer.AddElement(in5d,sz(1),sz(2),sz(3),sz(4),sz(5));
          ElementNum = out.getNumElements()-1;
      elseif strcmp(whe,'newTime')          
          myViewer.setTimesLinked(0);
          out = myViewer.AddTime(in5d,sz(1),sz(2),sz(3),sz(4),sz(5));
          ElementNum = out.getNumElements()-1;
      elseif strcmp(whe,'replaceElement')
          out = myViewer;
          if (ElementNum >= out.getNumElements())
              out = myViewer.AddElement(in5d,sz(1),sz(2),sz(3),sz(4),sz(5));
              ElementNum = out.getNumElements()-1;
          else
              myViewer.ReplaceData(ElementNum,0,in5d);
          end
      end
   end
   out.UpdatePanels();
 else
   view5d_image_extern(in);
   out =[];
end

if ~isempty(in.pixelsize)
    % myViewer.SetAxisScalesAndUnits(int elementNum, int timeNum, double SV, double SX,double SY,double SZ,double SE,double ST,double OV,double OX,double OY,double OZ,double OE,double OT,String NameV, String Names[],String UnitV, String Units[]) {
    AxisNames={'X','Y','Z','E','T'};
    AxisUnits=[in.pixelunits(1:3),'a.u.','a.u.'];
    for n=1:numel(ElementNum)
        out.SetAxisScalesAndUnits(ElementNum(n), 0, 1.0, in.pixelsize(1),in.pixelsize(2),in.pixelsize(1),1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,'intensity', AxisNames,'a.u.', AxisUnits);
    end
end

%-----------------------------------------------------------------------
function jarfile = jarfilename
try
    if matlabver_ge([6,5])
       jarfile = fullfile(fileparts(mfilename('fullpath')),'private','View5D.jar');
    else
       jarfile = fullfile(fileparts(which(mfilename)),'private','View5D.jar');
    end
catch
   [p,n,e]=fileparts(which('dipgetcoords'));
   jarfile = fullfile(p,'private','View5D.jar');
end
%-----------------------------------------------------------------------
function view5d_image_extern(in)

jarfile = jarfilename;
base = tempdir; % return OS set tempdir
if ~tempdir
   error('No temp directory given by OS.');
end
fn = [base,'dipimage_view5d'];

mdt = {'uint8',    1, 8,  'Byte'
       'uint16',   2, 16, 'Char'
       'uint32',   4, 32, 'Long'
       'sint8',    1, 8,  'Byte'
       'sint16',   2, 16, 'Short'
       'sint32',   4, 32, 'Long'
       'sfloat',  -1, 32, 'Float'
       'dfloat',  -1, 64, 'Double'
       'scomplex',-1, 32, 'Complex'
       'bin',      1, 8,  'Byte'
   };
ind = find(strcmp(datatype(in),mdt(:,1)));
bytes = mdt{ind,2};
bits  = mdt{ind,3};
dtype = mdt{ind,4};

sz = imsize(in);

% write raw data
%in = permute(in,[1 2 3 5 4]); % (Why swap two last dimensions??? I don't get this. -- CL)
writeim(in,fn,'icsv1',0);
delete([fn,'.ics']);

% write html file
content = ['<html><head><title>5D Viewer</title></head><body>' 10 ...
      '<!--Automatically generated by DIPimage.-->' 10 ...
      '<h1>5D Viewer</h1><p>Image Data displayed by View5D, a Java-Applet by Rainer Heintzmann</p>' 10 ...
      '<hr><applet archive=',jarfile,' code=View5D.class width=600 height=700 alt="Please enable Java.">' 10 ...
      '<param name=file value=',fn,'.ids>' 10 ...
      '<param name=sizex value=' num2str(sz(1)) '>' 10 ...
      '<param name=sizey value=' num2str(sz(2)) '>' 10 ...
      '<param name=sizez value=' num2str(sz(3)) '>' 10 ...
      '<param name=times value=' num2str(sz(5)) '>' 10 ...
      '<param name=elements value=' num2str(sz(4)) '>' 10 ...
      '<param name=bytes value=' num2str(bytes) '>' 10 ...
      '<param name=bits value=' num2str(bits) '>' 10 ...
      '<param name=dtype value=''' dtype '''>' 10 ...
      '<param name=unitsx value=''pix''>' 10 ...
      '<param name=scalex value=''1''>' 10 ...
      '<param name=unitsy value=''pix''>' 10 ...
      '<param name=scaley value=''1''>' 10 ...
      '<param name=unitsz value=''pix''>' 10 ...
      '<param name=scalez value=''1''>' 10 ...
      '<param name=unitse value=''color''>' 10 ...
      '<param name=scalee value=''1''>' 10 ...
      '<param name=unitst value=''time''>' 10 ...
      '<param name=scalet value=''1''>' 10 ...
      '<param name=unitsv1 value=''int.''>' 10 ...
      '<param name=unitsv2 value=''int.''>' 10 ...
      '<param name=unitsv3 value=''int.''>' 10 ...
      '<param name=unitsv4 value=''int.''>' 10 ...
      '<p>Your browser does not support Java applets.</p>' 10 ...
      '</applet><hr></body></html>' 10];
fid = fopen([fn '.html'],'w');
if fid <0
   error(['Could not create file' fn '.html']);
end
fprintf(fid,'%s',content);
fclose(fid);

% Start the applet
web(['file://' fn '.html']);
