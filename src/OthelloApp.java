import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class OthelloApp extends JFrame {

    private DiscButton[][] buttons = new DiscButton[8][8];
    private char[][] board = new char[8][8];
    private char currentPlayer = 'B';
    private String blackPlayerName, whitePlayerName;
    private JLabel status = new JLabel();
    private JLabel score = new JLabel();

    public OthelloApp() {
        setTitle("Othello Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Board
        JPanel board = new JPanel(new GridLayout(8,8));
        for (int i=0;i<8;i++) {
            for (int j=0;j<8;j++) {
                final int x=i;
                final int y=j;
                buttons[i][j]=new DiscButton();
                buttons[i][j].setPreferredSize(new Dimension(50,50));
                buttons[i][j].setBackground(Color.GREEN);
                buttons[i][j].setBorder(new LineBorder(Color.BLACK));
                buttons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        handleMove(x,y);
                    }
                });
                board.add(buttons[i][j]);
            }
        }

        // Info panel
        JPanel info = new JPanel(new GridLayout(2,1));
        info.add(status);
        info.add(score);

        // Control panel
        JPanel control = new JPanel();
        JButton newGameButton = new JButton("New Game");
        JButton saveButton = new JButton("Save");
        JButton loadButton = new JButton("Load");
        JComboBox<String> saveList = new JComboBox<>();
        control.add(newGameButton);
        control.add(saveButton);
        control.add(loadButton);
        control.add(saveList);

        add(board, BorderLayout.CENTER);
        add(info, BorderLayout.NORTH);
        add(control, BorderLayout.SOUTH);

        // Button actions
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { NewGame(); }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { saveGame(saveList); }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadGame(saveList); }
        });

        // Window setup
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        NewGame();
    }

    private void NewGame() {
        blackPlayerName = JOptionPane.showInputDialog("Black player:");
        whitePlayerName = JOptionPane.showInputDialog("White player:");
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++)
                board[i][j]=' ';
        board[3][3]='W';
        board[3][4]='B';
        board[4][3]='B';
        board[4][4]='W';
        currentPlayer='B';
        updateBoard();
    }

    private void handleMove(int x,int y) {
        if (!isValidMove(x,y,currentPlayer)) return;
        makeMove(x,y,currentPlayer);
        currentPlayer=(currentPlayer=='B')?'W':'B';
        if (!hasValidMove(currentPlayer)) {
            String winner=(currentPlayer=='B')?whitePlayerName:blackPlayerName;
            JOptionPane.showMessageDialog(this,"Game over!\nWinner: "+winner+"\n"+getScore());
            NewGame();
            return;
        }
        updateBoard();
    }

    private void updateBoard() {
        Color boardGreen = new Color(0, 100, 0);      // darker background
        Color highlight = new Color(85, 107, 47);     // olive highlight

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                buttons[i][j].setPiece(board[i][j]);
                if (isValidMove(i, j, currentPlayer)) {
                    buttons[i][j].setBackground(highlight);
                } else {
                    buttons[i][j].setBackground(boardGreen);
                }
                buttons[i][j].setBorder(new LineBorder(Color.BLACK));
            }
        }
        // Update labels
        String playerName = (currentPlayer=='B')?blackPlayerName:whitePlayerName;
        String color = (currentPlayer=='B')?"Black":"White";
        status.setText(playerName+" ("+color+") turn");
        score.setText(getScore());
    }

    private boolean isValidMove(int row,int col,char player) {
        if (board[row][col]!=' ') return false;
        char opponent = (player=='B')?'W':'B';
        for (int dx=-1;dx<=1;dx++) {
            for (int dy=-1;dy<=1;dy++) {
                if (dx==0 && dy==0) continue;
                int x=row+dx; int y=col+dy;
                boolean found=false;
                while (x>=0 && x<8 && y>=0 && y<8 && board[x][y]==opponent) {
                    found=true; x+=dx; y+=dy;
                }
                if (found && x>=0 && x<8 && y>=0 && y<8 && board[x][y]==player) return true;
            }
        }
        return false;
    }

    private void makeMove(int row,int col,char player) {
        board[row][col]=player;
        char opponent=(player=='B')?'W':'B';
        for (int dx=-1;dx<=1;dx++) {
            for (int dy=-1;dy<=1;dy++) {
                if (dx==0 && dy==0) continue;
                int x=row+dx; int y=col+dy;
                ArrayList<Point> toFlip=new ArrayList<Point>();
                while (x>=0 && x<8 && y>=0 && y<8 && board[x][y]==opponent) {
                    toFlip.add(new Point(x,y));
                    x+=dx; y+=dy;
                }
                if (x>=0 && x<8 && y>=0 && y<8 && board[x][y]==player) {
                    for (Point p:toFlip) board[p.x][p.y]=player;
                }
            }
        }
    }

    private boolean hasValidMove(char player) {
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++)
                if (isValidMove(i,j,player)) return true;
        return false;
    }

    private String getScore() {
        int black=0, white=0;
        for (int i=0;i<8;i++)
            for (int j=0;j<8;j++) {
                if (board[i][j]=='B') black++;
                else if (board[i][j]=='W') white++;
            }
        return blackPlayerName+" (Black): "+black+" | "+whitePlayerName+" (White): "+white;
    }

    // --- Save / Load ---
    private void saveGame(JComboBox<String> saveList) {
        try {
            String saveName = JOptionPane.showInputDialog("Save name:");
            if (saveName==null || saveName.isEmpty()) return;
            File f = new File(saveName+".txt");
            PrintWriter pw = new PrintWriter(f);
            pw.println(currentPlayer);
            pw.println(blackPlayerName);
            pw.println(whitePlayerName);
            for (int i=0;i<8;i++) {
                for (int j=0;j<8;j++)
                    pw.print(board[i][j]);
                pw.println();
            }
            pw.close();
            JOptionPane.showMessageDialog(this,"Game saved!");
            saveList.addItem(f.getName());
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Error saving game."); }
    }

    private void loadGame(JComboBox<String> saveList) {
        String fileName = (String) saveList.getSelectedItem();
        if (fileName==null) return;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            currentPlayer = br.readLine().charAt(0);
            blackPlayerName = br.readLine();
            whitePlayerName = br.readLine();
            for (int i=0;i<8;i++) {
                String line = br.readLine();
                for (int j=0;j<8;j++)
                    board[i][j]=line.charAt(j);
            }
            br.close();
            updateBoard();
        } catch (Exception e) { JOptionPane.showMessageDialog(this,"Error loading game."); }
    }

    public static void main(String[] args) {
        new OthelloApp();
    }
}


