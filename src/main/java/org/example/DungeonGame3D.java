package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class DungeonGame3D extends JFrame {

    private int[][] dungeonMap = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}
    };

    private double playerX = 1.5, playerY = 1.5;
    private double playerAngle = 0;
    private int playerHP = 100;
    private final double moveSpeed = 0.1;
    private final double rotSpeed = Math.PI / 8;

    private DungeonPanel3D dungeonPanel3D;
    private JProgressBar hpProgressBar;

    private ArrayList<Monster> monsters = new ArrayList<>();
    private Random rand = new Random();

    public DungeonGame3D() {
        setTitle("Dungeon Game - First Person View with Controls");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel untuk menampilkan view 3D
        dungeonPanel3D = new DungeonPanel3D();
        add(dungeonPanel3D, BorderLayout.CENTER);

        // Panel kontrol navigasi
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Listener untuk kontrol keyboard
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
                dungeonPanel3D.repaint();
            }
        });

        // Spawn beberapa monster
        spawnMonsters(3);

        setVisible(true);
    }

    private void spawnMonsters(int numMonsters) {
        for (int i = 0; i < numMonsters; i++) {
            int x = rand.nextInt(dungeonMap[0].length);
            int y = rand.nextInt(dungeonMap.length);
            if (dungeonMap[y][x] == 0) { // Pastikan spawn di tempat kosong
                monsters.add(new Monster(x + 0.5, y + 0.5, rand.nextInt(50) + 30, rand.nextInt(20) + 5));
            } else {
                i--; // Ulangi spawn jika tidak valid
            }
        }
    }

    private void movePlayer(double distance) {
        double nextX = playerX + Math.cos(playerAngle) * distance;
        double nextY = playerY + Math.sin(playerAngle) * distance;

        if (dungeonMap[(int) nextY][(int) nextX] == 0) {
            playerX = nextX;
            playerY = nextY;
        }
    }

    private void rotatePlayer(double angle) {
        playerAngle += angle;
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W: // Maju
                movePlayer(moveSpeed);
                break;
            case KeyEvent.VK_S: // Mundur
                movePlayer(-moveSpeed);
                break;
            case KeyEvent.VK_A: // Putar kiri
                rotatePlayer(-rotSpeed);
                break;
            case KeyEvent.VK_D: // Putar kanan
                rotatePlayer(rotSpeed);
                break;
            case KeyEvent.VK_SPACE: // Tombol interaksi (serang)
                attackMonster();
                break;
        }
    }

    private void attackMonster() {
        for (Monster monster : monsters) {
            if (Math.abs(monster.x - playerX) < 1.5 && Math.abs(monster.y - playerY) < 1.5) { // Cek jarak dekat
                int damage = rand.nextInt(15) + 5; // Damage acak antara 5 hingga 20
                monster.takeDamage(damage);
                System.out.println("Menyerang monster dengan damage: " + damage);
                if (monster.isDead()) {
                    System.out.println("Monster mati!");
                    monsters.remove(monster);
                    spawnMonsters(1); // Spawn monster baru setelah mati
                }
                break;
            }
        }
    }

    private class DungeonPanel3D extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            draw3DView(g);
        }

        private void draw3DView(Graphics g) {
            int width = getWidth();
            int height = getHeight();
            int halfHeight = height / 2;

            for (int x = 0; x < width; x++) {
                double rayAngle = (playerAngle - Math.PI / 6) + (x / (double) width) * (Math.PI / 3);

                double rayX = Math.cos(rayAngle);
                double rayY = Math.sin(rayAngle);

                double distanceToWall = 0;
                boolean hitWall = false;

                while (!hitWall && distanceToWall < 16) {
                    distanceToWall += 0.1;
                    int testX = (int) (playerX + rayX * distanceToWall);
                    int testY = (int) (playerY + rayY * distanceToWall);

                    if (testX < 0 || testX >= dungeonMap[0].length || testY < 0 || testY >= dungeonMap.length) {
                        hitWall = true;
                        distanceToWall = 16;
                    } else if (dungeonMap[testY][testX] == 1) {
                        hitWall = true;
                    }
                }

                int lineHeight = (int) (height / distanceToWall);
                int drawStart = halfHeight - lineHeight / 2;
                int drawEnd = halfHeight + lineHeight / 2;

                g.setColor(Color.GRAY);
                g.drawLine(x, drawStart, x, drawEnd);
            }

            // Gambar monster
            g.setColor(Color.RED);
            for (Monster monster : monsters) {
                if (Math.abs(monster.x - playerX) < 5 && Math.abs(monster.y - playerY) < 5) { // Tampilkan monster dekat player
                    g.fillRect((int) (monster.x * 50), (int) (monster.y * 50), 20, 20); // Gambar monster sebagai kotak
                }
            }
        }
    }

    private class Monster {
        double x, y;
        int hp, damage;

        public Monster(double x, double y, int hp, int damage) {
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.damage = damage;
        }

        public void takeDamage(int amount) {
            this.hp -= amount;
        }

        public boolean isDead() {
            return hp <= 0;
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Panel untuk tombol navigasi di kanan bawah
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(3, 3));

        JButton btnUp = new JButton("↑");
        JButton btnLeft = new JButton("←");
        JButton btnRight = new JButton("→");
        JButton btnDown = new JButton("↓");
        JButton btnInteract = new JButton("Attack");

        btnUp.setPreferredSize(new Dimension(50, 50));
        btnLeft.setPreferredSize(new Dimension(50, 50));
        btnRight.setPreferredSize(new Dimension(50, 50));
        btnDown.setPreferredSize(new Dimension(50, 50));
        btnInteract.setPreferredSize(new Dimension(50, 50));

        btnUp.addActionListener(e -> {
            movePlayer(moveSpeed);
            dungeonPanel3D.repaint();
        });

        btnDown.addActionListener(e -> {
            movePlayer(-moveSpeed);
            dungeonPanel3D.repaint();
        });

        btnLeft.addActionListener(e -> {
            rotatePlayer(-rotSpeed);
            dungeonPanel3D.repaint();
        });

        btnRight.addActionListener(e -> {
            rotatePlayer(rotSpeed);
            dungeonPanel3D.repaint();
        });

        btnInteract.addActionListener(e -> attackMonster());

        navPanel.add(new JLabel());
        navPanel.add(btnUp);
        navPanel.add(new JLabel());
        navPanel.add(btnLeft);
        navPanel.add(btnInteract);
        navPanel.add(btnRight);
        navPanel.add(new JLabel());
        navPanel.add(btnDown);
        navPanel.add(new JLabel());

        // Panel untuk menampilkan HP
        JPanel hpPanel = new JPanel();
        hpPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Membuat progress bar HP
        hpProgressBar = new JProgressBar(0, 100);
        hpProgressBar.setValue(playerHP);
        hpProgressBar.setStringPainted(true);
        hpProgressBar.setForeground(Color.RED);
        hpPanel.add(new JLabel("HP:"));
        hpPanel.add(hpProgressBar);

        // Menambahkan panel navigasi ke kanan bawah
        panel.add(navPanel, BorderLayout.EAST);
        panel.add(hpPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DungeonGame3D::new);
    }
}
