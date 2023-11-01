package xterminators.spellingbee.ui;

import java.io.File;

/**
 * An abstract factory for creating a UI for the speeling bee game.
 */
public abstract class UIFactory {
    /** The file containing the full dictionary of valid words. */
    protected File fullDictionary;
    /** The file containind the dictionary of valid root words. */
    protected File rootsDictionary;

    /**
     * Creates a UIFactory with the given dictionaries.
     * 
     * @param fullDictionary
     * @param rootsDictionary
     */
    public UIFactory(File fullDictionary, File rootsDictionary) {
        this.fullDictionary = fullDictionary;
        this.rootsDictionary = rootsDictionary;
    }

    /**
     * Creates a controller for the UI.
     * 
     * @return the controller
     */
    public abstract Controller createController();

    /**
     * Creates a view for the UI.
     * 
     * @return the view
     */
    public abstract View createView();
}
