% aViewer=LiveView(aViewer) : updates a live viewer.
% 
function aViewer=LiveView(aViewer,aRecon)
if isempty(aViewer) || (isnumeric(aViewer) && aViewer > 1)
    dipshow(3,aRecon);drawnow();
else
    if aViewer == 0
        aViewer = view5d(aRecon); 
    elseif aViewer == 1
        if isa(aRecon,'cuda')
            toShow = ConditionalCudaConvert(aRecon,0);
        else
            toShow = aRecon;
        end
        aViewer = dipshow(toShow);
        aViewer.UserData.curslice =floor(size(toShow,3)/2);
        drawnow();
    else
        if isa(aViewer,'matlab.ui.Figure')
            if isa(aRecon,'cuda')
                toShow = ConditionalCudaConvert(aRecon,0);
            else
                toShow = aRecon;
            end
            try
                aViewer = dipshow(aViewer,toShow);
            catch
                aViewer = dipshow(toShow);
                aViewer.UserData.curslice =floor(size(toShow,3)/2);
            end
            drawnow();
        else
        try
            % aViewer.toFront();
            view5d(aRecon,0,'replaceElement',aViewer,0);
            aViewer.ProcessKeyMainWindow('t');
            aViewer.UpdatePanels()
        catch
            dipshow(3,aRecon);
            drawnow();
        end
        end
    end
end
