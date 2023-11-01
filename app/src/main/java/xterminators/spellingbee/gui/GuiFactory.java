package xterminators.spellingbee.gui;

import java.io.File;

import xterminators.spellingbee.ui.Controller;
import xterminators.spellingbee.ui.UIFactory;
import xterminators.spellingbee.ui.View;

public class GuiFactory extends UIFactory {
    private GuiController controller;
    private GuiView view;

    public GuiFactory(File fullDictionary, File rootsDictionary) {
        super(fullDictionary, rootsDictionary);
        
        this.controller = null;
        this.view = null;
    }

    /**
     * Creates a controller for the GUI. Spesifically, this Controller will be a
     * GuiController.
     */
    @Override
    public Controller createController() {
        if (controller == null || view == null) {
            initControllerViewPair();
        }

        return controller;
    }

    /**
     * Creates a view for the GUI. Spesifically, this View will be a GuiView.
     */
    @Override
    public View createView() {
        if (controller == null || view == null) {
            initControllerViewPair();
        }

        return view;
    }

    private void initControllerViewPair() {
        controller = new GuiController(null, fullDictionary, rootsDictionary);
        view = new GuiView(controller, fullDictionary, rootsDictionary);
        controller.setView(view);
    }
}
