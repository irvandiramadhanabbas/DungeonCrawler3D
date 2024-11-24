package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DungeonGame extends JFrame {

    // Grid dungeon 2D (0 = kosong, 1 = dinding)
    private int[][] dungeonMap = {
            {1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 1},
            {1, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1}
    };

    // Posisi awal pemain
    private int playerX = 1;
    private int playerY = 1;

    // Panel untuk menampilkan dungeon
    private DungeonPanel dungeonPanel;

    public DungeonGame() {
        setTitle("Dungeon Game 3D Style");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Membuat panel untuk dungeon
        dungeonPanel = new DungeonPanel();
        add(dungeonPanel, BorderLayout.CENTER);

        // Menambahkan listener untuk kontrol keyboard
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        movePlayer(0, -1);
                        break;
                    case KeyEvent.VK_DOWN:
                        movePlayer(0, 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        movePlayer(-1, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        movePlayer(1, 0);
                        break;
                }
                dungeonPanel.repaint(); // Refresh tampilan
            }
        });

        // Menambahkan panel navigasi
        JPanel navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Fungsi untuk menggerakkan pemain
    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        // Cek apakah posisi baru berada di dalam batas peta dan bukan dinding
        if (newX >= 0 && newX < dungeonMap[0].length && newY >= 0 && newY < dungeonMap.length && dungeonMap[newY][newX] == 0) {
            playerX = newX;
            playerY = newY;
        }
    }

    // Membuat panel navigasi untuk menggerakkan pemain
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Tombol-tombol navigasi
        JButton upButton = new JButton("↑");
        JButton downButton = new JButton("↓");
        JButton leftButton = new JButton("←");
        JButton rightButton = new JButton("→");

        // Action Listener untuk tombol-tombol navigasi
        upButton.addActionListener(e -> {
            movePlayer(0, -1);
            dungeonPanel.repaint();
        });

        downButton.addActionListener(e -> {
            movePlayer(0, 1);
            dungeonPanel.repaint();
        });

        leftButton.addActionListener(e -> {
            movePlayer(-1, 0);
            dungeonPanel.repaint();
        });

        rightButton.addActionListener(e -> {
            movePlayer(1, 0);
            dungeonPanel.repaint();
        });

        // Layout Tombol Navigasi dalam GridBagLayout
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(upButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(leftButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(new JLabel(" "), gbc); // Spacer kosong

        gbc.gridx = 2;
        gbc.gridy = 1;
        panel.add(rightButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(downButton, gbc);

        return panel;
    }

    // Panel khusus untuk menampilkan dungeon dan tampilan 3D sederhana
    private class DungeonPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);

            // Menggambar dungeon dalam bentuk 3D sederhana
            draw3DView(g);
        }

        // Fungsi menggambar tampilan 3D sederhana
        private void draw3DView(Graphics g) {
            g.setColor(Color.WHITE);

            // Menggambar kotak 3D sederhana yang merepresentasikan peta
            int tileSize = 50;
            for (int row = 0; row < dungeonMap.length; row++) {
                for (int col = 0; col < dungeonMap[row].length; col++) {
                    if (dungeonMap[row][col] == 1) {
                        g.setColor(Color.GRAY);
                        g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                    } else if (row == playerY && col == playerX) {
                        g.setColor(Color.BLUE); // Pemain
                        g.fillOval(col * tileSize + 10, row * tileSize + 10, tileSize - 20, tileSize - 20);
                    } else {
                        g.setColor(Color.DARK_GRAY);
                        g.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DungeonGame::new);
    }
}
