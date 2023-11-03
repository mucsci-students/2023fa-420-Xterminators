package xterminators.spellingbee.cli;

import java.io.File;

import xterminators.spellingbee.ui.Controller;
import xterminators.spellingbee.ui.UIFactory;
import xterminators.spellingbee.ui.View;

/**
 * A factory for creating a CLI for the spelling bee game.
 */
public class CLIFactory extends UIFactory {
    private CLIController controller;
    private CLIView view;

    /**
     * Creates a CLIFactory with the given dictionaries.
     * 
     * @param fullDictionary the full dictionary of valid words
     * @param rootsDictionary the dictionary of valid root words
     */
    public CLIFactory(File fullDictionary, File rootsDictionary) {
        super(fullDictionary, rootsDictionary);

        this.controller = null;
        this.view = null;
    }

    /**
     * Creates a controller for the CLI. Spesifically, this Controller will be a
     * CLIController.
     */
    @Override
    public Controller createController() {
        if (view == null) {
            initControllerViewPair();
        } else if (controller == null) {
            controller
                = new CLIController(view, fullDictionary, rootsDictionary);
        }

        return controller;
    }

    /**
     * Creates a view for the CLI. Spesifically, this View will be a CLIView.
     */
    @Override
    public View createView() {
        if (view == null) {
            initControllerViewPair();
        }

        return view;
    }

    /**
     * Creates a CLIController and CLIView linked together as a pair.
     */
    private void initControllerViewPair() {
        view = new CLIView();
        controller = new CLIController(view, fullDictionary, rootsDictionary);
    }
}
