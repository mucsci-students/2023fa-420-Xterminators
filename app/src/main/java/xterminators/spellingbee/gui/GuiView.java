package xterminators.spellingbee.gui;

import xterminators.spellingbee.gui.GuiController;

import javax.swing.Timer;
import javax.swing.BoxLayout;

import java.util.ArrayList;
import java.nio.file.Paths;

import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

import java.io.File;
import java.io.IOException;

import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import xterminators.spellingbee.model.Puzzle;
import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.SaveMode;
import xterminators.spellingbee.ui.View;


public class GuiView extends View {
    private GuiController guiController;
    private ArrayList<JButton> letterButtons;
    private GuessKeyListener guessKeyListener;

    // Standard Fonts for GUI
    private Font standardFont;
    private Font smallFont;

    // Global GUI Components
    private JButton btnGuess;
    private JButton primaryLetterButton;
    private JButton saveHighScoreButton;
    private JFrame mainFrame;
    private JLabel currentPointsLabel;
    private JLabel currentRankLabel;
    private JPanel highScoresPanel;
    private JPanel mainPanel;
    private JPanel rankImagePanel;
    private JTextArea foundWordsArea;
    private JTextField tbGuess;
    // Used where the guess components are for screen grab
    private JLabel placeholderLabel;

    // File paths
    private static final String BEE_PATH = Paths.get("src", "main", "resources", "bee_icon.png").toString();
    private static final String EMPTY_RANK_START = Paths.get("src", "main", "resources", "hex_empty_start.png").toString();
    private static final String EMPTY_RANK_END = Paths.get("src", "main", "resources", "hex_empty_end.png").toString();
    private static final String EMPTY_RANK_MID = Paths.get("src", "main", "resources", "hex_empty.png").toString();

    // Constants for component coordinates and sizes
    private static final int FRAME_WIDTH = 990;
    private static final int FRAME_HEIGHT = 480;
    private static final int PUZZLE_LEFT_X = 50;
    private static final int PUZZLE_TOP_Y = 100;
    private static final int PZL_BTN_WIDTH = 50;
    private static final int PZL_BTN_HEIGHT = 50;
    private static final int PUZZLE_WIDTH = (PZL_BTN_WIDTH * 3) + 60;
    private static final int ACTION_PANEL_WIDTH = PUZZLE_WIDTH - 20;
    private static final int HIGH_SCORE_PANEL_WIDTH = PUZZLE_WIDTH;

    // Initialization *********************************************************

    public GuiView(
        GuiController controller,
        File dictionaryFile,
        File rootsDictionaryFile
    ) {
        guiController = controller;
        mainFrame = new JFrame("Spelling Bee");
        mainPanel = new JPanel();
        standardFont = new Font("Helvetica", Font.BOLD, 16);
        smallFont = new Font("Helvetica", Font.BOLD, 13);
        guessKeyListener = new GuessKeyListener();
    }

    /**
     * This sets up all of the components of the UI.
     * All components are created here, and all event
     * handlers are added here.
     * This is the only function where magic numbers
     * are, unfortunately, somewhat unavoidable, but
     * try to avoid them as much as possible.
     */
    public void InitUI() {
        try {
            Image iconImage = javax.imageio.ImageIO.read(new File(BEE_PATH));
            mainFrame.setIconImage(iconImage);
        } catch (IOException ex) {
            //ignore, icon just won't get set
        }

        initGuessComponents();
        initPuzzleButtons();
        initRankComponents();
        initHighScoreComponents();
        initActionComponents();
        initFoundWordsComponents();

        mainPanel.setBackground(Color.gray);
        mainFrame.setContentPane(mainPanel);

        // Start off with a random puzzle
        createRandomPuzzle();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - FRAME_WIDTH) / 2;
        int centerY = (int) (screenSize.getHeight() - FRAME_HEIGHT) / 2;
        mainFrame.setLocation(centerX, centerY);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setLayout(null);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    /**
     * Initializes the text box and button for guessing words.
     */
    private void initGuessComponents() {
        tbGuess = new JTextField();
        tbGuess.setFont(standardFont);
        tbGuess.addKeyListener(guessKeyListener);
        tbGuess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = tbGuess.getText();
                if (text != null && !text.isEmpty()) {
                    guessWordButtonClick(null);
                }
            }
        });

        tbGuess.setBounds(PUZZLE_LEFT_X, PUZZLE_TOP_Y - 60,
                            PUZZLE_WIDTH - 75, 30);
        mainPanel.add(tbGuess);

        btnGuess = createButton("Guess",
                                PUZZLE_LEFT_X + PUZZLE_WIDTH - 70,
                                PUZZLE_TOP_Y - 60, 75, 30, mainPanel);
        btnGuess.setFont(smallFont);
        btnGuess.addActionListener(this::guessWordButtonClick);
    }

    /**
     * Initializes the buttons for the puzzle letters.
     */
    private void initPuzzleButtons() {

        letterButtons = new ArrayList<>();
        final int PUZZLE_COL_OFFSET = 80;

        JButton letter1 = createButton("1", PUZZLE_LEFT_X, PUZZLE_TOP_Y + 40, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter1.addActionListener(this::letterButtonClick);
        letterButtons.add(letter1);

        JButton letter2 = createButton("2", PUZZLE_LEFT_X + PUZZLE_COL_OFFSET, PUZZLE_TOP_Y, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter2.addActionListener(this::letterButtonClick);
        letterButtons.add(letter2);

        JButton letter3 = createButton("3", PUZZLE_LEFT_X + (PUZZLE_COL_OFFSET * 2), PUZZLE_TOP_Y + 40, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter3.addActionListener(this::letterButtonClick);
        letterButtons.add(letter3);

        JButton letter4 = createButton("4", PUZZLE_LEFT_X + (PUZZLE_COL_OFFSET * 2), PUZZLE_TOP_Y + 120, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter4.addActionListener(this::letterButtonClick);
        letterButtons.add(letter4);

        JButton letter5 = createButton("5", PUZZLE_LEFT_X + PUZZLE_COL_OFFSET, PUZZLE_TOP_Y + 160, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter5.addActionListener(this::letterButtonClick);
        letterButtons.add(letter5);

        JButton letter6 = createButton("6", PUZZLE_LEFT_X, PUZZLE_TOP_Y + 120, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        letter6.addActionListener(this::letterButtonClick);
        letterButtons.add(letter6);

        primaryLetterButton = createButton("P", PUZZLE_LEFT_X + PUZZLE_COL_OFFSET, PUZZLE_TOP_Y + 80, PZL_BTN_WIDTH, PZL_BTN_HEIGHT, mainPanel);
        primaryLetterButton.addActionListener(this::letterButtonClick);
        primaryLetterButton.setBackground(Color.yellow);
    }

    /**
     * Initializes all components in the rank panel.
     */
    private void initRankComponents() {
        currentPointsLabel = new JLabel();
        currentPointsLabel.setFont(standardFont);
        currentPointsLabel.setBounds(PUZZLE_LEFT_X + 5, FRAME_HEIGHT - 158, PUZZLE_WIDTH * 2, 40);
        mainPanel.add(currentPointsLabel);

        currentRankLabel = new JLabel();
        currentRankLabel.setFont(standardFont);
        currentRankLabel.setBounds(PUZZLE_LEFT_X + 5, FRAME_HEIGHT - 134, PUZZLE_WIDTH * 2, 40);
        mainPanel.add(currentRankLabel);

        rankImagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rankImagePanel.setBounds(PUZZLE_LEFT_X, FRAME_HEIGHT - 94, PUZZLE_WIDTH * 2, 16);
        rankImagePanel.setBackground(Color.gray);
        mainPanel.add(rankImagePanel);

        redrawRank();
    }

    /**
     * Initializes all components in the high scores panel.
     */
    private void initHighScoreComponents() {
        JLabel highScoresLabel = new JLabel("High Scores");
        highScoresLabel.setFont(standardFont);
        highScoresLabel.setBounds(PUZZLE_LEFT_X + PUZZLE_WIDTH + 50, PUZZLE_TOP_Y - 85, PUZZLE_WIDTH, 20);
        mainPanel.add(highScoresLabel);

        highScoresPanel = new JPanel();
        highScoresPanel.setLayout(new BoxLayout(highScoresPanel, BoxLayout.Y_AXIS));
        highScoresPanel.setBounds(PUZZLE_LEFT_X + PUZZLE_WIDTH + 50,
                                    PUZZLE_TOP_Y - 65, HIGH_SCORE_PANEL_WIDTH, FRAME_HEIGHT - 150);
        highScoresPanel.setBackground(Color.gray);

        redrawHighScores();

        mainPanel.add(highScoresPanel);
    }

    /**
     * Initializes all components in the action panel.
     */
    private void initActionComponents() {
        JPanel actionPanel = new JPanel();
        actionPanel.setBounds(PUZZLE_LEFT_X + PUZZLE_WIDTH + HIGH_SCORE_PANEL_WIDTH + 60, PUZZLE_TOP_Y - 65, ACTION_PANEL_WIDTH, FRAME_HEIGHT - 105);
        actionPanel.setBackground(Color.gray);
        mainPanel.add(actionPanel);

        // New Puzzle
        JButton newPuzzleButton = createButton("New Puzzle", 0, 0, 50, 12, actionPanel);
        newPuzzleButton.addActionListener(this::newPuzzleButtonClick);

        //Random Puzzle
        JButton randomPuzzleButton = createButton("New Random Puzzle", 0, 0, 50, 12, actionPanel);
        randomPuzzleButton.addActionListener(this::randomPuzzleButtonClick);

        // Shuffle Letters
        JButton shufflePuzzleButton = createButton("Shuffle", 0, 16, 50, 12, actionPanel);
        shufflePuzzleButton.addActionListener(this::shufflePuzzleButtonClick);

        // Save Puzzle
        JButton savePuzzleButton = createButton("Save Puzzle", 0, 0, 50, 12, actionPanel);
        savePuzzleButton.addActionListener(e -> {
            try {
                savePuzzleButtonClick(e);
            } catch (IOException a) {
                System.out.println("There was an I/O error, please try again.");
            }
        });

        // Load Puzzle
        JButton loadPuzzleButton = createButton("Load Puzzle", 0, 0, 50, 12, actionPanel);
        loadPuzzleButton.addActionListener(this::loadPuzzleButtonClick);

        // Screen Grab
        JButton screenGrabButton = createButton("Get Puzzle Image", 0, 0, 50, 12, actionPanel);
        screenGrabButton.addActionListener(this::screenGrabButtonClick);

        // Hints
        JButton hintButton = createButton("Hint", 0, 0, 50, 12, actionPanel);
        hintButton.addActionListener(this::hintButtonClick);

        //
        // Add subsequent action buttons to actionPanel here
        // unless they are invisible by default, then put them 
        // at the bottom of the class.
        //

        // Save High Score
        saveHighScoreButton = createButton("Save High Score", 0, 0, 50, 12, actionPanel);
        saveHighScoreButton.addActionListener(this::saveHighScoreButtonClick);
        saveHighScoreButton.setVisible(false);
    }

    /**
     * Initializes all components in the found words panel.
     */
    private void initFoundWordsComponents() {
        JLabel foundWordsLabel = new JLabel("Found Words");
        foundWordsLabel.setFont(standardFont);
        foundWordsLabel.setBounds(PUZZLE_LEFT_X + HIGH_SCORE_PANEL_WIDTH + ACTION_PANEL_WIDTH + PUZZLE_WIDTH + 80, PUZZLE_TOP_Y - 85, PUZZLE_WIDTH, 20);
        mainPanel.add(foundWordsLabel);

        JPanel foundWordsPanel = new JPanel();
        foundWordsPanel.setBounds(PUZZLE_LEFT_X + HIGH_SCORE_PANEL_WIDTH + ACTION_PANEL_WIDTH + PUZZLE_WIDTH + 65, PUZZLE_TOP_Y - 65, PUZZLE_WIDTH, FRAME_HEIGHT - 105);
        foundWordsPanel.setBackground(Color.gray);
        mainPanel.add(foundWordsPanel);

        foundWordsArea = new JTextArea(20, 15);
        foundWordsArea.setEditable(false);
        foundWordsArea.setFont(smallFont);
        foundWordsArea.setBounds(0, 0, PUZZLE_WIDTH, FRAME_HEIGHT - 115);
        foundWordsArea.setBackground(Color.white);
        JScrollPane scrollPane = new JScrollPane(foundWordsArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        foundWordsPanel.add(scrollPane);
    }

    // End Initialization *****************************************************
    // Button Click Handlers **************************************************

    /**
     * The handler for a letter button click.
     * Adds the letter displayed on the clicked button
     * to tbMain's displayed text.
     *
     * @param e The ActionEvent from the button click.
     */
    private void letterButtonClick(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        String buttonChar = source.getText();

        String currentText = tbGuess.getText();
        currentText += buttonChar;
        tbGuess.setText(currentText);
    }

    /**
     * The handler for the new puzzle button click.
     * Opens a dialog asking the user for a base word
     * and required letter, and then generates the
     * puzzle based on the user's input.
     *
     * @param e The ActionEvent from the button click.
     */
    private void newPuzzleButtonClick(ActionEvent e) {
        CustomInputDialog inputDialog = new CustomInputDialog(mainFrame, "Get Starting Word", "Base Word", "Required Letter");
        inputDialog.setVisible(true);

        if (!inputDialog.isCanceled()) {
            String puzzleWord = inputDialog.getField1();
            String requiredLetterStr = inputDialog.getField2();
            char[] requiredLetterArray = (
                requiredLetterStr == null
                ? new char[0]
                : requiredLetterStr.toCharArray()
            );
            char requiredLetter = '1';
            if (requiredLetterArray.length > 0) {
                requiredLetter = requiredLetterArray[0];
            }

            if (puzzleWord != null && !puzzleWord.isEmpty()) {
                createPuzzle(puzzleWord.toLowerCase(), requiredLetter);
            } else {
                createRandomPuzzle();
            }
            redrawRank();
            redrawFoundWords();
            refocusGuessTextBox();
        }
    }

    /**
     * The handler for the shuffle button click.
     * If there is a puzzle loaded, the letters
     * will be shuffled and the puzzle letter buttons
     * will be redrawn.
     *
     * @param e The ActionEvent from the button click.
     */
    private void shufflePuzzleButtonClick(ActionEvent e) {
        Puzzle puzzle = Puzzle.getInstance();

        if (puzzle == null) {
            return;
        }

        puzzle.shuffle();

        redrawPuzzleButtons();
    }

    /**
     * The handler for the guess button click.
     * Takes the text from tbGuess and passes it
     * to the puzzle's guess function.
     * The result is then displayed in a dialog.
     *
     * @param e The ActionEvent from the button click.
     */
    private void guessWordButtonClick(ActionEvent e) {
        if (tbGuess.getText() == null || tbGuess.getText().isEmpty()) {
            showErrorDialog("You need to type a guess first!");
            return;
        }

        if (Puzzle.getInstance() == null) {
            showErrorDialog("No puzzle has been loaded." +
                " Please click \"New Puzzle\" to start a new puzzle. ");
                return;
        }

        String result = guiController.guessWord(tbGuess.getText());
        if (!result.isEmpty()) {
            redrawFoundWords();
            redrawRank();
            showMessage(result);
        }
        if (guiController.isHighScore()) {
            saveHighScoreButton.setVisible(true);
        }
        refocusGuessTextBox();
    }

    /**
     * The handler for the random puzzle button click.
     * If there is a puzzle loaded, the user will be
     * warned and asked for confirmation to load a new
     * puzzle. If there is no puzzle loaded or the user
     * confirms that they want to overwrite the current
     * puzzle, a new puzzle is created.
     *
     * @param e The ActionEvent from the button click.
     */
    private void randomPuzzleButtonClick(ActionEvent e) {
        if (Puzzle.getInstance() != null) {
            int userChoice = JOptionPane.showConfirmDialog(
                mainFrame,
                "There is already a puzzle loaded. " +
                "Do you want to load a new one anyway?",
                "Puzzle Already Loaded",
                JOptionPane.YES_NO_OPTION
            );

            if (userChoice == JOptionPane.YES_OPTION) {
                createRandomPuzzle();
                redrawFoundWords();
                redrawRank();
                refocusGuessTextBox();
            }
            // No action on NO_OPTION
        } else {
            createRandomPuzzle();
            redrawFoundWords();
            redrawRank();
            refocusGuessTextBox();
        }
    }

    /**
     * The function that is called whenever the savepuzzlebutton is clicked.
     * @param e - The button click
     * @throws IOException - If an I/O error occurs.
     */
    private void savePuzzleButtonClick(ActionEvent e) throws IOException{
        try{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showSaveDialog(mainFrame);
 
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                int result = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Do you want to save the word list in an encrypted format?",
                    "Save Encrypted?",
                    JOptionPane.YES_NO_OPTION
                );

                String saveResult;
                if (result == JOptionPane.YES_OPTION) {
                    saveResult = 
                        guiController.savePuzzle(selectedFile.getAbsolutePath(), SaveMode.ENCRYPTED);
                } else {
                    saveResult = 
                        guiController.savePuzzle(selectedFile.getAbsolutePath(), SaveMode.UNENCRYPTED);
                }
                
                showMessage(saveResult);
            }
        }
        catch (IOException a){
            showErrorDialog("The puzzle could not be saved due to an IO error.");
        }
    }

    /**
     * Function that is called when loadPuzzleButton is clicked.
     * @param e - the button click.
     */
    private void loadPuzzleButtonClick(ActionEvent e){
        // Creates a JFileChooser for file selection.
        JFileChooser j = new JFileChooser();

        // Shows the created JFileChooser with an open dialog
        j.showOpenDialog(j);
        // Converts the selectedfile name to string format
        String loadFile = "" + j.getSelectedFile() + "";

        // Pop-up informing the user of the results for trying to load the file.
        showMessage(guiController.loadPuzzle(loadFile));

        // Refreshes the button and foundwords views.
        Puzzle p = Puzzle.getInstance();
        if (p != null) {
            char[] secondaryLetters = p.getSecondaryLetters();
            char[] allLetters = new char[secondaryLetters.length + 1];

            for (int i = 0; i < letterButtons.size(); ++i) {
                if (i >= secondaryLetters.length) break;
                letterButtons.get(i).setText(secondaryLetters[i] + "");
                allLetters[i] = secondaryLetters[i];
            }
            primaryLetterButton.setText(p.getPrimaryLetter() + "");

            allLetters[allLetters.length - 1] = p.getPrimaryLetter();
            guessKeyListener.setAllowedLetters(allLetters);
            redrawFoundWords();
        }
    }

    private void screenGrabButtonClick(ActionEvent e) {
        try {
            mainPanel.remove(tbGuess);
            mainPanel.remove(btnGuess);

            placeholderLabel = new JLabel("Spelling Bee");
            placeholderLabel.setFont(standardFont);
            placeholderLabel.setBounds(10, PUZZLE_TOP_Y - 90, tbGuess.getWidth(), tbGuess.getHeight());

            mainPanel.add(placeholderLabel);
            mainPanel.repaint();
            mainFrame.repaint();
            // Shrink frame to exclude action buttons and found words
            int temp = mainFrame.getWidth();
            mainFrame.setSize(temp - ACTION_PANEL_WIDTH - PUZZLE_WIDTH - 50, mainFrame.getHeight());

            // This is stupid, but necessary because the frame won't be redrawn
            // until the event handler ends.
            Timer timer = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {

                    // Action to be performed after 10 ms
                    System.out.println("Timer fired after 10 ms");

                    Robot robot = new Robot();
                    Rectangle frameBounds = mainFrame.getBounds();
                    Rectangle captureBounds = new Rectangle(frameBounds.x + 10, frameBounds.y + 40, frameBounds.width - 18, frameBounds.height - 55);
                    BufferedImage screenshot = robot.createScreenCapture(captureBounds);

                    Transferable transferable = new TransferableImage(screenshot);

                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(transferable, null);

                    // Restore guess controls
                    mainPanel.remove(placeholderLabel);
                    mainPanel.add(tbGuess);
                    mainPanel.add(btnGuess);

                    mainPanel.repaint();
                    mainFrame.repaint();

                    // Restore frame size
                    mainFrame.setSize(temp, mainFrame.getHeight());

                    JOptionPane.showMessageDialog(mainFrame, "Puzzle image copied to clipboard.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    ((Timer) e.getSource()).stop();
                }
            });

            // Start the timer
            timer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hintButtonClick(ActionEvent e){
        showMessage(guiController.hint());
    }

    private void saveHighScoreButtonClick(ActionEvent e) {
        CustomInputDialog inputDialog = new CustomInputDialog(mainFrame, "Save High Score", "Name", "");
        inputDialog.setVisible(true);

        if (!inputDialog.isCanceled()) {
            String userName = inputDialog.getField1();
            if (userName == null || userName.isEmpty()) {
                showMessage("The name you gave was empty! No high score saved.");
                return;
            }

            guiController.saveHighScore(userName);
            redrawHighScores();
        }
    }   

    // End Button Event Handlers **********************************************
    // Dialogs ****************************************************************

    /**
     * Displays a dialog box with the given message and an information icon.
     *
     * @param message The message to show in the dialog.
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            mainFrame,
            message,
            "",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a dialog box with the given message and an error icon.
     *
     * @param errorMessage The message to show in the dialog.
     */
    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
            mainFrame,
            errorMessage,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // End Dialogs ************************************************************
    // Other Functions ********************************************************

    /**
     * Creates a new button and adds it to the given panel.
     *
     * @param text The text that should be on the button.
     * @param x The X coordinate of the button.
     * @param y The Y coordinate of the button.
     * @param buttonWidth How wide the button should be.
     * @param buttonHeight How tall the button should be.
     * @param panel The panel to add the button to.
     *
     * @return The newly created button.
     */
    private JButton createButton(String text, int x, int y, int buttonWidth, int buttonHeight, JPanel panel) {
        JButton newButton = new JButton(text);
        newButton.setBounds(x, y, buttonWidth, buttonHeight);
        newButton.setBackground(Color.white);
        newButton.setFont(standardFont);
        panel.add(newButton);

        return newButton;
    }

    /**
     * Creates a new random puzzle in GuiFunctions,
     * and then updates all of the letter buttons
     * with the puzzle's letters.
     */
    private void createRandomPuzzle() {
        createPuzzle("", 'a');
    }

    /**
     * Creates a new puzzle in GuiFunctions,
     * and then updates all of the letter buttons
     * with the puzzle's letters.
     */
    private void createPuzzle(String baseWord, char requiredLetter) {
        try {
            if (baseWord == null || baseWord.isEmpty()) {
                guiController.createNewPuzzle();
            } else {
                guiController.createNewPuzzle(baseWord, requiredLetter);
            }
        } catch (Exception ex) {
            showErrorDialog("There was a problem making the puzzle. " + ex.getMessage());
        }

        redrawPuzzleButtons();
    }

    /**
     * Sets the text of all the puzzle buttons to the letters
     * of the puzzle. This is necessary whenever the letters
     * are expected to change, such as after a new puzzle is
     * created or the puzzle letters are shuffled.
     */
    private void redrawPuzzleButtons() {
        Puzzle p = Puzzle.getInstance();
        if (p != null) {
            char[] secondaryLetters = p.getSecondaryLetters();
            char[] allLetters = new char[secondaryLetters.length + 1];

            for (int i = 0; i < letterButtons.size(); ++i) {
                if (i >= secondaryLetters.length) break;
                letterButtons.get(i).setText(secondaryLetters[i] + "");
                allLetters[i] = secondaryLetters[i];
            }
            primaryLetterButton.setText(p.getPrimaryLetter() + "");

            allLetters[allLetters.length - 1] = p.getPrimaryLetter();
            guessKeyListener.setAllowedLetters(allLetters);
        }
    }

    /**
     * Draws the rank progress bar.
     * All of the hexes up to the puzzle's
     * current rank will use full rank images,
     * and any hexes above the current rank
     * will use empty rank images.
     */
    private void redrawRank() {
        final String FULL_RANK_START = Paths.get("src", "main", "resources", "hex_full_start.png").toString();
        final String FULL_RANK_END = Paths.get("src", "main", "resources", "hex_full_end.png").toString();
        final String FULL_RANK_MID = Paths.get("src", "main", "resources", "hex_full.png").toString();

        int earnedPoints = 0;
        String currentRankName = "None";
        int totalPoints = 100;

        Puzzle p = Puzzle.getInstance();
        if (p != null) {
            earnedPoints = p.getEarnedPoints();
            currentRankName = p.getRank().getRankName();
            totalPoints = p.getTotalPoints();
        }

        currentPointsLabel.setText("Current Points: " + earnedPoints);
        currentRankLabel.setText("Current Rank: " + currentRankName);

        Rank[] allRanks = Rank.values();

        rankImagePanel.removeAll();

        int x = 0;
        for (int i = 0; i < allRanks.length; ++i) {
            // Do this comparison up here so the code is easier to read
            boolean sufficientPoints = allRanks[i].getRequiredPoints(totalPoints) <= earnedPoints;

            ImageIcon icon = new ImageIcon(EMPTY_RANK_MID);
            if (sufficientPoints) {
                    icon = new ImageIcon(FULL_RANK_MID);
            }

            if (i == 0) {
                icon = new ImageIcon(EMPTY_RANK_START);
                if (sufficientPoints) {
                    icon = new ImageIcon(FULL_RANK_START);
                }
            } else if (i == allRanks.length - 1) {
                icon = new ImageIcon(EMPTY_RANK_END);
                if (sufficientPoints) {
                    icon = new ImageIcon(FULL_RANK_END);
                }
            }
            JLabel imageLabel = new JLabel();
            imageLabel.setIcon(icon);
            imageLabel.setBounds(x, 0, icon.getIconWidth(), icon.getIconHeight());
            rankImagePanel.add(imageLabel);
            x += icon.getIconWidth();
        }
        rankImagePanel.revalidate();
        rankImagePanel.repaint();
    }

    private void redrawHighScores() {
        int currentY = 0;
        TreeMap<String, Integer> scores = guiController.getHighScores();
        highScoresPanel.removeAll();

        int labelHeight = 20;
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            JLabel scoreLabel = new JLabel(entry.getKey() + ": " + entry.getValue());
            scoreLabel.setFont(standardFont);
            //scoreLabel.setBounds(0, currentY, highScoresPanel.getWidth() - 20, labelHeight);
            highScoresPanel.add(scoreLabel);
            currentY += labelHeight;
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Fills the found word box with all found words from the puzzle's
     * found words list.
     */
    private void redrawFoundWords() {
        foundWordsArea.setText("");
        Puzzle puzzle = Puzzle.getInstance();
        if (puzzle != null && puzzle.getFoundWords() != null) {
            for (String word : puzzle.getFoundWords()) {
                foundWordsArea.append(word + "\n");
            }
        }
    }

    private void refocusGuessTextBox() {
        tbGuess.setText("");
        tbGuess.requestFocus();
    }

    // End Other Functions ****************************************************
}
