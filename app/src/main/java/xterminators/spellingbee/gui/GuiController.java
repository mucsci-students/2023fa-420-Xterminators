package xterminators.spellingbee.gui;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import xterminators.spellingbee.model.Rank;
import xterminators.spellingbee.model.Puzzle;

import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;

public class GuiController {
    private GuiFunctions guiFunctions;
    private ArrayList<JButton> letterButtons;
    private GuessKeyListener guessKeyListener;

    // Standard Fonts for GUI
    private Font standardFont;
    private Font smallFont;

    // Global GUI Components
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTextField tbGuess;
    private JButton primaryLetterButton;
    private JTextArea foundWordsArea;
    private JPanel rankImagePanel;
    private JLabel currentPointsLabel;
    private JLabel currentRankLabel;

    // File paths
    private static final String BEE_PATH = Paths.get("src", "main", "resources", "bee_icon.png").toString();
    private static final String EMPTY_RANK_START = Paths.get("src", "main", "resources", "hex_empty_start.png").toString();
    private static final String EMPTY_RANK_END = Paths.get("src", "main", "resources", "hex_empty_end.png").toString();
    private static final String EMPTY_RANK_MID = Paths.get("src", "main", "resources", "hex_empty.png").toString();

    // Constants for component coordinates and sizes
    private static final int FRAME_WIDTH = 770;
    private static final int FRAME_HEIGHT = 480;
    private static final int PUZZLE_LEFT_X = 50;
    private static final int PUZZLE_TOP_Y = 100;
    private static final int PZL_BTN_WIDTH = 50;
    private static final int PZL_BTN_HEIGHT = 50;
    private static final int PUZZLE_WIDTH = (PZL_BTN_WIDTH * 3) + 60;

    /**
     * This sets up all of the components of the UI.
     * All components are created here, and all event
     * handlers are added here.
     * This is the only function where magic numbers 
     * are, unfortunately, somewhat unavoidable, but
     * try to avoid them as much as possible.
     */
    public void InitUI() {

        mainFrame = new JFrame("Spelling Bee");
        mainPanel = new JPanel();
        standardFont = new Font("Helvetica", Font.BOLD, 16);
        smallFont = new Font("Helvetica", Font.BOLD, 13);
        guessKeyListener = new GuessKeyListener();
        guiFunctions = new GuiFunctions(this);

        try {
            Image iconImage = javax.imageio.ImageIO.read(new File(BEE_PATH));
            mainFrame.setIconImage(iconImage);
        } catch (IOException ex) {
            //ignore, icon just won't get set
        }

        initGuessComponents();
        initPuzzleButtons();
        initRankComponents();
        initActionComponents();
        initFoundWordsComponents();
        
        mainPanel.setBackground(Color.gray);
        mainFrame.setContentPane(mainPanel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - FRAME_WIDTH) / 2;
        int centerY = (int) (screenSize.getHeight() - FRAME_HEIGHT) / 2;
        mainFrame.setLocation(centerX, centerY);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setLayout(null);
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

        JButton btnGuess = createButton("Guess", 
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
     * Initializes all components in the action panel.
     */
    private void initActionComponents() {
        JPanel actionPanel = new JPanel();
        actionPanel.setBounds(PUZZLE_LEFT_X + PUZZLE_WIDTH + 50, PUZZLE_TOP_Y - 65, PUZZLE_WIDTH - 30, FRAME_HEIGHT - 105);
        actionPanel.setBackground(Color.gray);
        mainPanel.add(actionPanel);

        // New Puzzle
        JButton newPuzzleButton = createButton("New Puzzle", 0, 0, 50, 12, actionPanel);
        newPuzzleButton.addActionListener(this::newPuzzleButtonClick);

        // Shuffle Letters
        JButton shufflePuzzleButton = createButton("Shuffle", 0, 16, 50, 12, actionPanel);
        shufflePuzzleButton.addActionListener(this::shufflePuzzleButtonClick);

        //
        // Add subsequent action buttons to actionPanel here (i.e. save, load, shuffle)
        //
    }

    /**
     * Initializes all components in the found words panel.
     */
    private void initFoundWordsComponents() {
        JLabel foundWordsLabel = new JLabel("Found Words");
        foundWordsLabel.setFont(standardFont);
        foundWordsLabel.setBounds(PUZZLE_LEFT_X + (PUZZLE_WIDTH * 2) + 20, PUZZLE_TOP_Y - 85, PUZZLE_WIDTH, 20);
        mainPanel.add(foundWordsLabel);

        JPanel foundWordsPanel = new JPanel();
        foundWordsPanel.setBounds(PUZZLE_LEFT_X + (PUZZLE_WIDTH * 2) + 5, PUZZLE_TOP_Y - 65, PUZZLE_WIDTH, FRAME_HEIGHT - 105);
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
        CustomInputDialog inputDialog = new CustomInputDialog(mainFrame);
        inputDialog.setVisible(true);

        if (!inputDialog.isCanceled()) {
            String puzzleWord = inputDialog.getBaseWord();
            String requiredLetterStr = inputDialog.getRequiredLetter();
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
                createPuzzle("", 'a');
            }
            redrawRank();
            drawFoundWords();
            tbGuess.requestFocus();
        }
    }

    private void shufflePuzzleButtonClick(ActionEvent e) {
        if (guiFunctions.getPuzzle() == null) {
            return;
        }

        Puzzle p = guiFunctions.getPuzzle();
        p.shuffle();

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

        if (guiFunctions.getPuzzle() == null) {
            showErrorDialog("No puzzle has been loaded." + 
                " Please click \"New Puzzle\" to start a new puzzle. ");
                return;
        }

        String result = guiFunctions.guessWord(tbGuess.getText());
        if (!result.isEmpty()) {
            drawFoundWords();
            redrawRank();
            showMessage(result);
        }
        tbGuess.setText("");
    }

    /**
     * Creates a new puzzle in GuiFunctions,
     * and then updates all of the letter buttons
     * with the puzzle's letters.
     */
    private void createPuzzle(String baseWord, char requiredLetter) {
        try {
            if (baseWord == null || baseWord.isEmpty()) {
                guiFunctions.createNewPuzzle();
            } else {
                guiFunctions.createNewPuzzle(baseWord, requiredLetter);
            }
        } catch (Exception ex) {
            showErrorDialog("There was a problem making the puzzle. " + ex.getMessage());
        }

        redrawPuzzleButtons();
    }

    private void redrawPuzzleButtons() {
        Puzzle p = guiFunctions.getPuzzle();
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

        Puzzle p = guiFunctions.getPuzzle();
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

    /**
     * Fills the found word box with all found words from the puzzle's
     * found words list. 
     */
    private void drawFoundWords() {
        foundWordsArea.setText("");
        Puzzle puzzle = guiFunctions.getPuzzle();
        if (puzzle != null && puzzle.getFoundWords() != null) {
            for (String word : puzzle.getFoundWords()) {
                foundWordsArea.append(word + "\n");
            }
        }
    }
}
