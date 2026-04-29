package knightproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class KnightTravailsGUIWithCoordinates extends JFrame {
    
    private JButton[][] boardButtons = new JButton[8][8];
    private int[] start = {-1, -1};
    private int[] end = {-1, -1};
    private JLabel statusLabel;
    private JLabel movesLabel;
    private JLabel coordinatesLabel;
    private boolean startSelected = false;
    
    // All 8 possible knight moves
    private static final int[][] KNIGHT_MOVES = {
        {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };
    
    public KnightTravailsGUIWithCoordinates() {
        setTitle("KNIGHT'S TRAVAILS PATH FINDER");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Title Panel
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(new Color(0, 102, 204));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel mainTitle = new JLabel("KNIGHT'S TRAVAILS PATH FINDER", SwingConstants.CENTER);
        mainTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainTitle.setForeground(Color.WHITE);
        
        JLabel subTitle = new JLabel("Find the shortest knight path on 8x8 chessboard", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subTitle.setForeground(Color.YELLOW);
        
        titlePanel.add(mainTitle);
        titlePanel.add(subTitle);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create Chess Board Panel
        JPanel boardPanel = new JPanel(new GridLayout(8, 8, 1, 1));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        
        // Initialize chessboard buttons WITH COORDINATES ON EACH BLOCK
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setLayout(new BorderLayout());
                
                // Main coordinates label (larger)
                JLabel coordLabel = new JLabel("[" + row + "," + col + "]", SwingConstants.CENTER);
                coordLabel.setFont(new Font("Arial", Font.BOLD, 11));
                coordLabel.setForeground(Color.BLACK);
                
                // Algebraic notation (smaller, at bottom)
                JLabel algebraicLabel = new JLabel(toAlgebraicNotation(row, col), SwingConstants.CENTER);
                algebraicLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                algebraicLabel.setForeground(new Color(80, 80, 80));
                
                // Create a panel to hold both labels
                JPanel labelPanel = new JPanel(new BorderLayout());
                labelPanel.setOpaque(false);
                labelPanel.add(coordLabel, BorderLayout.CENTER);
                labelPanel.add(algebraicLabel, BorderLayout.SOUTH);
                
                button.add(labelPanel, BorderLayout.CENTER);
                
                // Chessboard pattern colors
                if ((row + col) % 2 == 0) {
                    button.setBackground(new Color(255, 255, 240)); // Ivory
                } else {
                    button.setBackground(new Color(169, 169, 169)); // Dark Gray
                }
                
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                
                final int r = row;
                final int c = col;
                
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onSquareClick(r, c);
                    }
                });
                
                // Add hover effect
                button.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        coordinatesLabel.setText("Hovering: [Row: " + r + ", Col: " + c + "] | " + 
                                               toAlgebraicNotation(r, c));
                        if (!isSpecialSquare(r, c)) {
                            button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                        }
                    }
                    
                    public void mouseExited(MouseEvent e) {
                        coordinatesLabel.setText("Click any square to select");
                        if (!isSpecialSquare(r, c)) {
                            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                        }
                    }
                });
                
                boardButtons[row][col] = button;
                boardPanel.add(button);
            }
        }
        
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        
        // Right Panel - Controls and Information
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        // Selection Panel
        JPanel selectionPanel = createStyledPanel("SELECTION", Color.BLUE);
        selectionPanel.setLayout(new GridLayout(2, 2, 5, 5));
        
        JLabel startLabel = new JLabel("START: ", SwingConstants.RIGHT);
        startLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel startValue = new JLabel("[Not selected]", SwingConstants.LEFT);
        startValue.setFont(new Font("Arial", Font.BOLD, 12));
        startValue.setForeground(Color.GREEN);
        
        JLabel endLabel = new JLabel("END: ", SwingConstants.RIGHT);
        endLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel endValue = new JLabel("[Not selected]", SwingConstants.LEFT);
        endValue.setFont(new Font("Arial", Font.BOLD, 12));
        endValue.setForeground(Color.RED);
        
        selectionPanel.add(startLabel);
        selectionPanel.add(startValue);
        selectionPanel.add(endLabel);
        selectionPanel.add(endValue);
        
        // Update labels when selection changes
        Thread selectionUpdater = new Thread(() -> {
            while (true) {
                if (start[0] != -1) {
                    startValue.setText("[" + start[0] + "," + start[1] + "]");
                    startValue.setForeground(Color.GREEN);
                } else {
                    startValue.setText("[Not selected]");
                    startValue.setForeground(Color.GRAY);
                }
                
                if (end[0] != -1) {
                    endValue.setText("[" + end[0] + "," + end[1] + "]");
                    endValue.setForeground(Color.RED);
                } else {
                    endValue.setText("[Not selected]");
                    endValue.setForeground(Color.GRAY);
                }
                
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        });
        selectionUpdater.setDaemon(true);
        selectionUpdater.start();
        
        // Controls Panel
        JPanel controlsPanel = createStyledPanel("CONTROLS", new Color(0, 150, 136));
        
        JTextArea instructions = new JTextArea();
        instructions.setText("INSTRUCTIONS:\n" +
                           "1. Click START square (Green)\n" +
                           "2. Click END square (Red)\n" +
                           "3. Click 'Find Path'\n" +
                           "4. Path will show in Yellow\n\n" +
                           "OR use Quick Test buttons below:");
        instructions.setFont(new Font("Arial", Font.PLAIN, 12));
        instructions.setEditable(false);
        instructions.setBackground(new Color(240, 240, 240));
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        
        controlsPanel.add(instructions);
        
        // Quick Tests Panel
        JPanel testPanel = createStyledPanel("QUICK TESTS", new Color(76, 175, 80));
        testPanel.setLayout(new GridLayout(3, 1, 5, 5));
        
        JButton test1Btn = createTestButton("Test 1: START [4,5] → END [5,6]", 
                new int[]{4,5}, new int[]{5,6}, new Color(76, 175, 80));
        JButton test2Btn = createTestButton("Test 2: START [2,2] → END [5,3]", 
                new int[]{2,2}, new int[]{5,3}, new Color(33, 150, 243));
        JButton test3Btn = createTestButton("Test 3: START [0,0] → END [7,7]", 
                new int[]{0,0}, new int[]{7,7}, new Color(244, 67, 54));
        
        testPanel.add(test1Btn);
        testPanel.add(test2Btn);
        testPanel.add(test3Btn);
        
        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton findPathBtn = new JButton("FIND PATH");
        findPathBtn.setFont(new Font("Arial", Font.BOLD, 14));
        findPathBtn.setBackground(new Color(46, 125, 50));
        findPathBtn.setForeground(Color.WHITE);
        findPathBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findShortestPath();
            }
        });
        
        JButton resetBtn = new JButton("RESET");
        resetBtn.setFont(new Font("Arial", Font.BOLD, 14));
        resetBtn.setBackground(new Color(198, 40, 40));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetBoard();
            }
        });
        
        actionPanel.add(findPathBtn);
        actionPanel.add(resetBtn);
        
        // Status Panel
        JPanel statusPanel = createStyledPanel("STATUS", new Color(121, 85, 72));
        statusPanel.setLayout(new BorderLayout());
        
        statusLabel = new JLabel("Click START square (Green)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        // Moves Panel
        JPanel movesPanel = createStyledPanel("MOVES", new Color(255, 152, 0));
        movesPanel.setLayout(new BorderLayout());
        
        movesLabel = new JLabel("Moves: 0", SwingConstants.CENTER);
        movesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        movesLabel.setForeground(Color.BLACK);
        movesLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        movesPanel.add(movesLabel, BorderLayout.CENTER);
        
        // Coordinates Display Panel
        JPanel coordPanel = createStyledPanel("COORDINATES", new Color(96, 125, 139));
        coordPanel.setLayout(new BorderLayout());
        
        coordinatesLabel = new JLabel("Click any square to select", SwingConstants.CENTER);
        coordinatesLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        coordinatesLabel.setForeground(Color.BLACK);
        coordinatesLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        coordPanel.add(coordinatesLabel, BorderLayout.CENTER);
        
        // Legend Panel
        JPanel legendPanel = createStyledPanel("LEGEND", new Color(158, 158, 158));
        legendPanel.setLayout(new GridLayout(4, 2, 5, 5));
        
        legendPanel.add(createLegendItem("START", Color.GREEN));
        legendPanel.add(createLegendItem("[Row,Col]", Color.BLACK));
        legendPanel.add(createLegendItem("END", Color.RED));
        legendPanel.add(createLegendItem("(Algebraic)", new Color(80, 80, 80)));
        legendPanel.add(createLegendItem("PATH", Color.YELLOW));
        legendPanel.add(createLegendItem("Square Hover", Color.BLUE));
        legendPanel.add(createLegendItem("MOVE #", Color.CYAN));
        legendPanel.add(createLegendItem("Selected", Color.ORANGE));
        
        // Add all panels to right panel
        rightPanel.add(selectionPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(controlsPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(testPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(actionPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(statusPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(movesPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(coordPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(legendPanel);
        
        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Set window properties
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    // Helper Methods
    private JPanel createStyledPanel(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(color, 2), 
            title, 
            0, 0, 
            new Font("Arial", Font.BOLD, 12), 
            color
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    private JButton createTestButton(String text, int[] startPos, int[] endPos, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 11));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTest(startPos, endPos);
            }
        });
        
        return button;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorLabel.setPreferredSize(new Dimension(20, 20));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        panel.add(colorLabel);
        panel.add(textLabel);
        return panel;
    }
    
    private String toAlgebraicNotation(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
    
    private boolean isSpecialSquare(int row, int col) {
        return (row == start[0] && col == start[1]) || 
               (row == end[0] && col == end[1]);
    }
    
    private void loadTest(int[] startPos, int[] endPos) {
        resetBoard();
        
        start = startPos.clone();
        end = endPos.clone();
        
        // Mark start position - keep coordinates visible
        boardButtons[start[0]][start[1]].setBackground(Color.GREEN);
        boardButtons[start[0]][start[1]].removeAll();
        
        JLabel startText = new JLabel("<html><center><b>START</b><br>[" + start[0] + "," + start[1] + "]<br>" + 
                                     toAlgebraicNotation(start[0], start[1]) + "</center></html>", SwingConstants.CENTER);
        startText.setFont(new Font("Arial", Font.BOLD, 10));
        startText.setForeground(Color.BLACK);
        boardButtons[start[0]][start[1]].add(startText, BorderLayout.CENTER);
        
        // Mark end position - keep coordinates visible
        boardButtons[end[0]][end[1]].setBackground(Color.RED);
        boardButtons[end[0]][end[1]].removeAll();
        
        JLabel endText = new JLabel("<html><center><b>END</b><br>[" + end[0] + "," + end[1] + "]<br>" + 
                                   toAlgebraicNotation(end[0], end[1]) + "</center></html>", SwingConstants.CENTER);
        endText.setFont(new Font("Arial", Font.BOLD, 10));
        endText.setForeground(Color.BLACK);
        boardButtons[end[0]][end[1]].add(endText, BorderLayout.CENTER);
        
        startSelected = true;
        statusLabel.setText("Test loaded! Click FIND PATH");
        statusLabel.setForeground(Color.BLUE);
        coordinatesLabel.setText("START: [" + start[0] + "," + start[1] + "] | END: [" + end[0] + "," + end[1] + "]");
    }
    
    private void onSquareClick(int row, int col) {
        if (!startSelected) {
            // Select START position
            start = new int[]{row, col};
            boardButtons[row][col].setBackground(Color.GREEN);
            boardButtons[row][col].removeAll();
            
            JLabel startText = new JLabel("<html><center><b>START</b><br>[" + row + "," + col + "]<br>" + 
                                         toAlgebraicNotation(row, col) + "</center></html>", SwingConstants.CENTER);
            startText.setFont(new Font("Arial", Font.BOLD, 10));
            startText.setForeground(Color.BLACK);
            boardButtons[row][col].add(startText, BorderLayout.CENTER);
            
            startSelected = true;
            statusLabel.setText("START selected at [" + row + "," + col + "]. Now click END square (Red)");
            statusLabel.setForeground(Color.RED);
            coordinatesLabel.setText("Selected START: [Row: " + row + ", Col: " + col + "]");
        } else if (end[0] == -1) {
            // Select END position
            end = new int[]{row, col};
            boardButtons[row][col].setBackground(Color.RED);
            boardButtons[row][col].removeAll();
            
            JLabel endText = new JLabel("<html><center><b>END</b><br>[" + row + "," + col + "]<br>" + 
                                       toAlgebraicNotation(row, col) + "</center></html>", SwingConstants.CENTER);
            endText.setFont(new Font("Arial", Font.BOLD, 10));
            endText.setForeground(Color.BLACK);
            boardButtons[row][col].add(endText, BorderLayout.CENTER);
            
            statusLabel.setText("Ready! Click FIND PATH");
            statusLabel.setForeground(new Color(0, 150, 0));
            coordinatesLabel.setText("START: [" + start[0] + "," + start[1] + "] | END: [" + row + "," + col + "]");
        }
    }
    
    private void findShortestPath() {
        if (start[0] == -1 || end[0] == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select both START and END positions!",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Clear any previous path
        clearPathOnly();
        
        // Find shortest path using BFS
        ArrayList<int[]> path = bfsShortestPath(start, end);
        
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No path found between the selected positions.",
                "Path Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Display the path on board
        displayPath(path);
        
        // Update status
        int moveCount = path.size() - 1;
        movesLabel.setText("Moves: " + moveCount);
        statusLabel.setText("Path found! " + moveCount + " moves");
        statusLabel.setForeground(new Color(0, 100, 0));
        
        // Show path details
        showPathDetails(path);
    }
    
    private ArrayList<int[]> bfsShortestPath(int[] start, int[] end) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[8][8];
        int[][][] parent = new int[8][8][2];
        
        // Initialize parent array
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                parent[i][j][0] = -1;
                parent[i][j][1] = -1;
            }
        }
        
        queue.add(start);
        visited[start[0]][start[1]] = true;
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            
            // Check if we reached destination
            if (current[0] == end[0] && current[1] == end[1]) {
                break;
            }
            
            // Try all knight moves
            for (int[] move : KNIGHT_MOVES) {
                int newRow = current[0] + move[0];
                int newCol = current[1] + move[1];
                
                // Check if move is valid
                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    if (!visited[newRow][newCol]) {
                        visited[newRow][newCol] = true;
                        parent[newRow][newCol][0] = current[0];
                        parent[newRow][newCol][1] = current[1];
                        queue.add(new int[]{newRow, newCol});
                    }
                }
            }
        }
        
        // Reconstruct path from end to start
        ArrayList<int[]> path = new ArrayList<>();
        int row = end[0];
        int col = end[1];
        
        // Backtrack using parent array
        Stack<int[]> stack = new Stack<>();
        while (row != -1 && col != -1) {
            stack.push(new int[]{row, col});
            int tempRow = parent[row][col][0];
            int tempCol = parent[row][col][1];
            row = tempRow;
            col = tempCol;
        }
        
        // Reverse to get correct order (start to end)
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }
        
        return path;
    }
    
    private void displayPath(ArrayList<int[]> path) {
        // Highlight each step in the path (skip start and end)
        for (int i = 1; i < path.size() - 1; i++) {
            int[] position = path.get(i);
            int row = position[0];
            int col = position[1];
            
            // Set path color
            boardButtons[row][col].setBackground(Color.YELLOW);
            boardButtons[row][col].removeAll();
            
            JLabel pathText = new JLabel("<html><center><b>" + i + "</b><br>[" + row + "," + col + "]<br>" + 
                                        toAlgebraicNotation(row, col) + "</center></html>", SwingConstants.CENTER);
            pathText.setFont(new Font("Arial", Font.BOLD, 10));
            pathText.setForeground(Color.BLACK);
            boardButtons[row][col].add(pathText, BorderLayout.CENTER);
        }
        
        // Update start and end labels
        boardButtons[start[0]][start[1]].removeAll();
        JLabel startText = new JLabel("<html><center><b>START</b><br>[" + start[0] + "," + start[1] + "]<br>" + 
                                     toAlgebraicNotation(start[0], start[1]) + "</center></html>", SwingConstants.CENTER);
        startText.setFont(new Font("Arial", Font.BOLD, 10));
        startText.setForeground(Color.BLACK);
        boardButtons[start[0]][start[1]].add(startText);
        
        boardButtons[end[0]][end[1]].removeAll();
        JLabel endText = new JLabel("<html><center><b>END</b><br>[" + end[0] + "," + end[1] + "]<br>" + 
                                   toAlgebraicNotation(end[0], end[1]) + "</center></html>", SwingConstants.CENTER);
        endText.setFont(new Font("Arial", Font.BOLD, 10));
        endText.setForeground(Color.BLACK);
        boardButtons[end[0]][end[1]].add(endText);
    }
    
    private void showPathDetails(ArrayList<int[]> path) {
        StringBuilder details = new StringBuilder();
        details.append("KNIGHT'S PATH FOUND!\n\n");
        details.append("Start: [" + start[0] + "," + start[1] + "] (" + toAlgebraicNotation(start[0], start[1]) + ")\n");
        details.append("End:   [" + end[0] + "," + end[1] + "] (" + toAlgebraicNotation(end[0], end[1]) + ")\n");
        details.append("Total Moves: " + (path.size() - 1) + "\n\n");
        details.append("Path Sequence:\n");
        
        for (int i = 0; i < path.size(); i++) {
            int[] pos = path.get(i);
            details.append("[" + pos[0] + "," + pos[1] + "] (" + toAlgebraicNotation(pos[0], pos[1]) + ")");
            if (i < path.size() - 1) {
                details.append(" → ");
            }
            if ((i + 1) % 3 == 0 && i < path.size() - 1) {
                details.append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(this, details.toString(),
            "Path Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearPathOnly() {
        // Clear only the path (keep start and end)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Skip start and end positions
                if ((row == start[0] && col == start[1]) || 
                    (row == end[0] && col == end[1])) {
                    continue;
                }
                
                // Reset to original chessboard colors and coordinates
                boardButtons[row][col].removeAll();
                boardButtons[row][col].setLayout(new BorderLayout());
                
                JLabel coordLabel = new JLabel("[" + row + "," + col + "]", SwingConstants.CENTER);
                coordLabel.setFont(new Font("Arial", Font.BOLD, 11));
                coordLabel.setForeground(Color.BLACK);
                
                JLabel algebraicLabel = new JLabel(toAlgebraicNotation(row, col), SwingConstants.CENTER);
                algebraicLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                algebraicLabel.setForeground(new Color(80, 80, 80));
                
                JPanel labelPanel = new JPanel(new BorderLayout());
                labelPanel.setOpaque(false);
                labelPanel.add(coordLabel, BorderLayout.CENTER);
                labelPanel.add(algebraicLabel, BorderLayout.SOUTH);
                
                boardButtons[row][col].add(labelPanel, BorderLayout.CENTER);
                
                if ((row + col) % 2 == 0) {
                    boardButtons[row][col].setBackground(new Color(255, 255, 240));
                } else {
                    boardButtons[row][col].setBackground(new Color(169, 169, 169));
                }
                
                boardButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }
        }
    }
    
    private void resetBoard() {
        start = new int[]{-1, -1};
        end = new int[]{-1, -1};
        startSelected = false;
        
        // Reset all squares to initial state WITH COORDINATES
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardButtons[row][col].removeAll();
                boardButtons[row][col].setLayout(new BorderLayout());
                
                // Main coordinates label
                JLabel coordLabel = new JLabel("[" + row + "," + col + "]", SwingConstants.CENTER);
                coordLabel.setFont(new Font("Arial", Font.BOLD, 11));
                coordLabel.setForeground(Color.BLACK);
                
                // Algebraic notation
                JLabel algebraicLabel = new JLabel(toAlgebraicNotation(row, col), SwingConstants.CENTER);
                algebraicLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                algebraicLabel.setForeground(new Color(80, 80, 80));
                
                JPanel labelPanel = new JPanel(new BorderLayout());
                labelPanel.setOpaque(false);
                labelPanel.add(coordLabel, BorderLayout.CENTER);
                labelPanel.add(algebraicLabel, BorderLayout.SOUTH);
                
                boardButtons[row][col].add(labelPanel, BorderLayout.CENTER);
                
                // Set chessboard pattern
                if ((row + col) % 2 == 0) {
                    boardButtons[row][col].setBackground(new Color(255, 255, 240));
                } else {
                    boardButtons[row][col].setBackground(new Color(169, 169, 169));
                }
                
                boardButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
            }
        }
        
        // Reset status
        statusLabel.setText("Click START square (Green)");
        statusLabel.setForeground(Color.BLUE);
        movesLabel.setText("Moves: 0");
        coordinatesLabel.setText("Click any square to select");
    }
    
    // MAIN METHOD
    public static void main(String[] args) {
        KnightTravailsGUIWithCoordinates gui = new KnightTravailsGUIWithCoordinates();
        gui.setVisible(true);
    }
}