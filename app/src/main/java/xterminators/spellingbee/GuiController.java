package xterminators.spellingbee;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.Paths;

public class GuiController {

    private JFrame m_frame;
    private JPanel m_panel;
    private JTextField tbGuess;
    private UserFunctions m_functions;
    private Font standardFont;
    
    public void InitUI() {
        final int FRAME_WIDTH = 700;
        final int FRAME_HEIGHT = 400;
        final int PUZZLE_LEFT_X = 50;
        final int PUZZLE_TOP_Y = 100;
        final int PZL_BTN_WIDTH = 50;
        final int PZL_BTN_HEIGHT = 50;

        m_frame = new JFrame("Spelling Bee");
        m_panel = new JPanel();
        m_functions = new UserFunctions();
        standardFont = new Font("Helvetica", Font.BOLD, 16);

        try {
            String imagePath = Paths.get("app", "src", "main", "resources", "bee_icon.png").toString();
            Image iconImage = javax.imageio.ImageIO.read(new File(imagePath));
            m_frame.setIconImage(iconImage);
        } catch (IOException ex) {
            //ignore, icon just won't get set
        }

        int puzzleWidth = (PZL_BTN_WIDTH * 3) + 20;

        JLabel lbGuess = new JLabel("Guess:");
        lbGuess.setFont(standardFont);
        lbGuess.setBounds(PUZZLE_LEFT_X, PUZZLE_TOP_Y - 70, puzzleWidth, 20);
        m_panel.add(lbGuess);

        tbGuess = new JTextField();
        tbGuess.setFont(standardFont);
        // The puzzle is three buttons wide with 10 units of space between each
        tbGuess.setBounds(PUZZLE_LEFT_X, PUZZLE_TOP_Y - 50, puzzleWidth, 20);
        m_panel.add(tbGuess);

        // When the puzzle gets loaded, all of these buttons need to get 
        // the appropriate letters assigned to their text fields.
        // Until a puzzle is loaded, they'll have placeholders.

        JButton letter1 = createButton("1", PUZZLE_LEFT_X, PUZZLE_TOP_Y + 30, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter1.addActionListener(this::letterButtonClick);

        JButton letter2 = createButton("2", PUZZLE_LEFT_X + 60, PUZZLE_TOP_Y, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter2.addActionListener(this::letterButtonClick);

        JButton letter3 = createButton("3", PUZZLE_LEFT_X + 120, PUZZLE_TOP_Y + 30, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter3.addActionListener(this::letterButtonClick);

        JButton letterPrimary = createButton("P", PUZZLE_LEFT_X + 60, PUZZLE_TOP_Y + 60, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letterPrimary.addActionListener(this::letterButtonClick);
        letterPrimary.setBackground(Color.yellow);
        
        JButton letter4 = createButton("4", PUZZLE_LEFT_X + 120, PUZZLE_TOP_Y + 90, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter4.addActionListener(this::letterButtonClick);

        JButton letter5 = createButton("5", PUZZLE_LEFT_X + 60, PUZZLE_TOP_Y + 120, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter5.addActionListener(this::letterButtonClick);

        JButton letter6 = createButton("6", PUZZLE_LEFT_X, PUZZLE_TOP_Y + 90, PZL_BTN_WIDTH, PZL_BTN_HEIGHT);
        letter6.addActionListener(this::letterButtonClick);
        
        m_panel.setBackground(Color.gray);
        m_frame.setContentPane(m_panel);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - FRAME_WIDTH) / 2;
        int centerY = (int) (screenSize.getHeight() - FRAME_HEIGHT) / 2;
        m_frame.setLocation(centerX, centerY);
        m_frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        m_frame.setLayout(null);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setVisible(true);
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
     * Creates a new button and adds it to m_panel.
     * 
     * @param text The text that should be on the button.
     * @param x The X coordinate of the button.
     * @param y The Y coordinate of the button.
     * @param buttonWidth How wide the button should be.
     * @param buttonHeight How tall the button should be.
     * 
     * @return The newly created button.
     */
    private JButton createButton(String text, int x, int y, int buttonWidth, int buttonHeight) {
        JButton newButton = new JButton(text);
        newButton.setBounds(x, y, buttonWidth, buttonHeight);
        newButton.setBackground(Color.white);
        newButton.setFont(standardFont);
        m_panel.add(newButton);

        return newButton;
    }
}
