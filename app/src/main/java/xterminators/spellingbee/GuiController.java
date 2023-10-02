package xterminators.spellingbee;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiController {

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTextField tbGuess;
    private UserFunctions m_functions;
    private Font standardFont;
    private Font smallFont;
    private GuessKeyListener guessKeyListener;
    private GuiFunctions guiFunctions;
    private ArrayList<JButton> letterButtons;
    private JButton primaryLetterButton;
    
    /**
     * This sets up all of the components of the UI.
     * All components are created here, and all event
     * handlers are added here.
     * This is the only function where magic numbers 
     * are, unfortunately, somewhat unavoidable, but
     * try to avoid them as much as possible.
     */
    public void InitUI() {
        final int FRAME_WIDTH = 700;
        final int FRAME_HEIGHT = 480;
        final int PUZZLE_LEFT_X = 50;
        final int PUZZLE_TOP_Y = 100;
        final int PZL_BTN_WIDTH = 50;
        final int PZL_BTN_HEIGHT = 50;

        mainFrame = new JFrame("Spelling Bee");
        mainPanel = new JPanel();
        m_functions = new UserFunctions();
        standardFont = new Font("Helvetica", Font.BOLD, 16);
        smallFont = new Font("Helvetica", Font.BOLD, 13);
        guessKeyListener = new GuessKeyListener();
        guiFunctions = new GuiFunctions(this);

        try {
            String imagePath = Paths.get("src", "main", "resources", "bee_icon.png").toString();
            Image iconImage = javax.imageio.ImageIO.read(new File(imagePath));
            mainFrame.setIconImage(iconImage);
        } catch (IOException ex) {
            //ignore, icon just won't get set
        }

        int puzzleWidth = (PZL_BTN_WIDTH * 3) + 60;

        // Create guess components (tbGuess, btnGuess)

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

        // The puzzle is three buttons wide with 10 units of space between each
        tbGuess.setBounds(PUZZLE_LEFT_X, PUZZLE_TOP_Y - 60, puzzleWidth - 75, 30);
        mainPanel.add(tbGuess);

        JButton btnGuess = createButton("Guess", PUZZLE_LEFT_X + puzzleWidth - 70, PUZZLE_TOP_Y - 60, 75, 30, mainPanel);
        btnGuess.setFont(smallFont);
        btnGuess.addActionListener(this::guessWordButtonClick);

        // When the puzzle gets loaded, all of these buttons need to get 
        // the appropriate letters assigned to their text fields.
        // Until a puzzle is loaded, they'll have placeholders.

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

        // Rank Panel

        JPanel rankPanel = new JPanel();
        rankPanel.setBounds(PUZZLE_LEFT_X, FRAME_HEIGHT - 140, puzzleWidth, 60);
        rankPanel.setBackground(Color.black);

        JLabel tempLabel = new JLabel("This a good spot for rank?");
        rankPanel.add(tempLabel);
        mainPanel.add(rankPanel);

        // Action Panel

        JPanel actionPanel = new JPanel();
        actionPanel.setBounds(PUZZLE_LEFT_X + puzzleWidth + 50, PUZZLE_TOP_Y - 65, puzzleWidth - 30, FRAME_HEIGHT - 105);
        actionPanel.setBackground(Color.gray);
        mainPanel.add(actionPanel);

        // New Puzzle
        JButton newPuzzleButton = createButton("New Puzzle", 0, 0, 50, 12, actionPanel);
        newPuzzleButton.addActionListener(this::newPuzzleButtonClick);
        actionPanel.add(newPuzzleButton);

        // Add subsequent action buttons to actionPanel here (i.e. save, load, shuffle)


        
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
     * Creates a new button and adds it to mainPanel.
     * 
     * @param text The text that should be on the button.
     * @param x The X coordinate of the button.
     * @param y The Y coordinate of the button.
     * @param buttonWidth How wide the button should be.
     * @param buttonHeight How tall the button should be.
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
        }
    }

    private void guessWordButtonClick(ActionEvent e) {
        if (tbGuess.getText() == null || tbGuess.getText().isEmpty()) {
            showErrorDialog("You need to type a guess first!");
            return;
        }

        String result = guiFunctions.guessWord(tbGuess.getText());
        if (!result.isEmpty()) {
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

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
            mainFrame, 
            message,
            "",
            JOptionPane.INFORMATION_MESSAGE
        );        
    }

    private void showErrorDialog(String errorMessage) {
        JOptionPane.showMessageDialog(
            mainFrame, 
            errorMessage,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
