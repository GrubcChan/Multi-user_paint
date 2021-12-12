package ru.suai.client.view;

import ru.suai.client.controller.ControllerClient;
import ru.suai.network.message.Message;
import ru.suai.network.message.MessageType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * Visual representation of the "Multi-user paint" application
 */

public class ViewClient {
    private final ControllerClient client;

    private RegistrationWindow registrationWindow = null;

    private PaintWindow paintWindow = null;

    private int thisWindow;

    public ViewClient(ControllerClient client) {
        this.client = client;
    }

    public void initFrameClient() {
        this.createRegistrationWindow();
    }

    public void createRegistrationWindow() {
        registrationWindow = new RegistrationWindow();
        thisWindow = 0;
    }

    public void closeRegistrationWindow() {
        registrationWindow.close();
        thisWindow = -1;
    }

    public void createPaintWindow() {
        this.paintWindow = new PaintWindow();
        thisWindow = 1;
    }

    public void connectPaintWindow(String message) {
        this.paintWindow = new PaintWindow(message);
        thisWindow = 1;
    }

    public RegistrationWindow getRegistrationWindow() {
        return this.registrationWindow;
    }

    public PaintWindow getPaintWindow() {
        return this.paintWindow;
    }

    public void errorMessage(String error){
        if(thisWindow == 0){
            registrationWindow.errorDialogWindow(error);
        } else if(thisWindow == 1){
            paintWindow.errorDialogWindow(error);
        } else{
            System.out.println("Окно не найдено");
        }
    }

    /**
     * Class describing the registration window.
     * The registration window displays the User Name
     * and Board Name input fields, as well as a list of boards created by other users.
     */
    public class RegistrationWindow {
        private final JFrame registrationFrame = new JFrame("Registration");

        private JTextField jTextFieldUsername;

        private JTextField jTextFieldBoardName;

        private JComboBox<String> jComboBoxBoardName;

        public void errorDialogWindow(String text) {
            JOptionPane.showMessageDialog(
                    registrationFrame, text,
                    "Error", JOptionPane.ERROR_MESSAGE
            );
        }

        public RegistrationWindow() {
            createRegistrationWindow();
            createTextUsername();
            createLabelMulti();
            createLabelPaint();
            createLabelUsername();
            createTextBoardName();
            createLabelBoardName();
            createComboBoxBoardName();
            createBoard();
            connectBoard();
        }

        public void close() {
            registrationFrame.dispose();
        }

        private void createRegistrationWindow() {
            registrationFrame.setBounds(677, 365, 566, 350);
            registrationFrame.setResizable(false); // нельзя менять размер окна
            registrationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // закрытие программы
            registrationFrame.setLayout(null);
            registrationFrame.setVisible(true);
            registrationFrame.getContentPane().setBackground(new Color(0x05, 0x11, 0x48));
        }


        private void createTextUsername() {
            jTextFieldUsername = new JTextField();
            jTextFieldUsername.setBounds(290, 125, 150, 25);
            jTextFieldUsername.setText("");
            jTextFieldUsername.setBackground(new Color(0x31, 0x31, 0x86));
            jTextFieldUsername.setForeground(Color.white);
            jTextFieldUsername.setFont(new Font("Helvetica", Font.PLAIN, 15));
            registrationFrame.add(jTextFieldUsername);
        }

        private void createLabelUsername() {
            JLabel jLabelUsername = new JLabel();
            jLabelUsername.setBounds(200, 120, 90, 35);
            jLabelUsername.setText("Username: ");
            jLabelUsername.setForeground(new Color(0xCF, 0x94, 0x51));
            jLabelUsername.setFont(new Font("Purisa", Font.PLAIN, 15));
            registrationFrame.add(jLabelUsername);
        }

        private void createLabelMulti() {
            JLabel jMultiText = new JLabel();
            jMultiText.setBounds(163, 20, 80, 70);
            jMultiText.setText("Multi");
            jMultiText.setForeground(new Color(0xCF, 0x94, 0x51));
            jMultiText.setFont(new Font("Purisa", Font.BOLD, 30));
            registrationFrame.add(jMultiText);
        }

        private void createLabelPaint() {
            JLabel jPaintText = new JLabel();
            jPaintText.setText("Paint");
            jPaintText.setBackground(Color.black);
            jPaintText.setBounds(253, 20, 150, 70);
            jPaintText.setForeground(Color.white);
            jPaintText.setFont(new Font("URW Bookman", Font.BOLD + Font.ITALIC, 55));
            registrationFrame.add(jPaintText);
        }

        private void createTextBoardName() {
            jTextFieldBoardName = new JTextField();
            jTextFieldBoardName.setBounds(125, 205, 120, 25);
            jTextFieldBoardName.setText("");
            jTextFieldBoardName.setBackground(new Color(0x31, 0x31, 0x86));
            jTextFieldBoardName.setForeground(Color.white);
            jTextFieldBoardName.setFont(new Font("Helvetica", Font.PLAIN, 15));
            registrationFrame.add(jTextFieldBoardName);
        }

        private void createLabelBoardName() {
            JLabel jLabelBoardName = new JLabel();
            jLabelBoardName.setBounds(30, 200, 95, 35);
            jLabelBoardName.setText("BoarName: ");
            jLabelBoardName.setForeground(new Color(0xCF, 0x94, 0x51));
            jLabelBoardName.setFont(new Font("Purisa", Font.PLAIN, 15));
            registrationFrame.add(jLabelBoardName);
        }

        private void createComboBoxBoardName() {
            JLabel jLabelBoardNameCombo = new JLabel();
            jLabelBoardNameCombo.setBounds(313, 200, 95, 35);
            jLabelBoardNameCombo.setText("BoarName: ");
            jLabelBoardNameCombo.setForeground(new Color(0xCF, 0x94, 0x51));
            jLabelBoardNameCombo.setFont(new Font("Purisa", Font.PLAIN, 15));
            jComboBoxBoardName = new JComboBox<>(client.getBoards().toArray(new String[0]));
            jComboBoxBoardName.setBackground(new Color(0x31, 0x31, 0x86));
            jComboBoxBoardName.setBounds(408, 200, 150, 30);
            jComboBoxBoardName.setForeground(Color.white);
            jComboBoxBoardName.setFont(new Font("Purisa", Font.PLAIN, 15));
            registrationFrame.add(jComboBoxBoardName);
            registrationFrame.add(jLabelBoardNameCombo);
        }

        private void createBoard() {
            JButton jButtonCreateBoard = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("createBoard.png"))));
            jButtonCreateBoard.setBounds(30, 250, 222, 50);
            jButtonCreateBoard.setBackground(new Color(0x31, 0x31, 0x86));
            jButtonCreateBoard.setOpaque(false);
            jButtonCreateBoard.addActionListener(event -> {
                String username = jTextFieldUsername.getText();
                String nameBoard = jTextFieldBoardName.getText();
                /* Username missing */
                if (username.equals("")) {
                    registrationFrame.repaint();
                    return;
                }
                /* No board name */
                if (nameBoard.equals("")) {
                    registrationFrame.repaint();
                    return;
                }
                /* Create */
                try {
                    client.getTcpConnection().send(new Message(MessageType.CREATE_BOARD, username + " " + nameBoard));
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(registrationFrame, "Error " + err.getMessage());
                    client.closeTCPConnection();
                }
            });
            registrationFrame.add(jButtonCreateBoard);
        }

        private void connectBoard() {
            JButton jButtonConnectBoard = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("joinBoard.png"))));
            jButtonConnectBoard.setBounds(313, 250, 222, 50);
            jButtonConnectBoard.setOpaque(false);
            jButtonConnectBoard.setBackground(new Color(0x31, 0x31, 0x86));
            jButtonConnectBoard.addActionListener(event -> {
                String username = jTextFieldUsername.getText();
                String nameBoard = (String) jComboBoxBoardName.getSelectedItem();
                /* Username missing */
                if (username.equals("")) {
                    System.out.println();
                    registrationFrame.repaint();
                    return;
                }
                /* No board name */
                if (Objects.equals(nameBoard, "")) {
                    registrationFrame.repaint();
                    return;
                }
                /* Connection */
                try {
                    client.getTcpConnection().send(new Message(MessageType.CONNECT_BOARD, username + " " + nameBoard));
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(registrationFrame, "Error " + err.getMessage());
                    client.closeTCPConnection();
                }
            });
            registrationFrame.add(jButtonConnectBoard);
        }
    }

    /**
     * Drawing window class
     */
    public class PaintWindow {
        private PaintFrame paintFrame;
        private PaintPanel japan;
        private JButton colorButton;
        private JSlider jSliderSize;
        private JColorChooser tcc;
        private BufferedImage image;
        private int mode = 0;
        private int xPad;
        private int xf;
        private int yf;
        private int yPad;
        private boolean pressed = false;
        private Color mainColor;
        private String fileName;
        private Graphics2D graphics;

        public void errorDialogWindow(String text) {
            JOptionPane.showMessageDialog(
                    paintFrame, text,
                    "Error", JOptionPane.ERROR_MESSAGE
            );
            close();
        }
        public PaintWindow() {
            createPaintFrame();
            createMenuBar();
            createPaintPanel();
            createToolBarInstrumentions();
            createToolBarColor();
            createChangeListener();
            createJapanListener();
        }

        public PaintWindow(String message) {
            createPaintFrame();
            createMenuBar();
            createPaintPanel();
            createToolBarInstrumentions();
            createToolBarColor();
            createChangeListener();
            createJapanListener();
            connectBoard(message);
        }

        public void close(){
            paintFrame.dispose();
        }

        private void createPaintFrame() {
            paintFrame = new PaintFrame("Multi-user Paint");
            paintFrame.setBounds(525, 271, 870, 538);
            paintFrame.setResizable(false);
            paintFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            paintFrame.setBackground(new Color(0x05, 0x11, 0x48));
            mainColor = Color.black;
        }

        private void createMenuBar() {
            JMenuBar menuBar = new JMenuBar();
            menuBar.setBorderPainted(false);
            menuBar.setBackground(new Color(0x05, 0x11, 0x48));
            paintFrame.setJMenuBar(menuBar);
            menuBar.setBounds(0, 0, 350, 30);
            JMenu fileMenu = new JMenu("File");
            fileMenu.setBackground(Color.white);
            fileMenu.setForeground(Color.white);
            menuBar.add(fileMenu);
            Action saveAction = new AbstractAction("Save") {
                public void actionPerformed(ActionEvent event) {
                    try {
                        JFileChooser jf = new JFileChooser();
                        /* creating file filters */
                        TextFileFilter pngFilter = new TextFileFilter(".png");
                        TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                        if (fileName == null) {
                            /* adding filters */
                            jf.addChoosableFileFilter(pngFilter);
                            jf.addChoosableFileFilter(jpgFilter);
                            int result = jf.showSaveDialog(null);
                            if (result == JFileChooser.APPROVE_OPTION) {
                                fileName = jf.getSelectedFile().getAbsolutePath();
                            }
                        }
                        /* Checking which filter is selected */
                        if (jf.getFileFilter() == pngFilter) {
                            ImageIO.write(image, "png", new File(fileName + ".png"));
                        } else {
                            ImageIO.write(image, "jpeg", new File(fileName + ".jpg"));
                        }
                        client.getTcpConnection().send(new Message(MessageType.SAVE_IMAGE));
                    } catch (IOException ex) {
                        errorDialogWindow("I / O error");
                    }
                }
            };
            JMenuItem saveMenu = new JMenuItem(saveAction);
            fileMenu.add(saveMenu);
            Action saveAsAction = new AbstractAction("Save as...") {
                public void actionPerformed(ActionEvent event) {
                    try {
                        JFileChooser jf = new JFileChooser();
                        /* creating file filters */
                        TextFileFilter pngFilter = new TextFileFilter(".png");
                        TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                        /* adding filters */
                        jf.addChoosableFileFilter(pngFilter);
                        jf.addChoosableFileFilter(jpgFilter);
                        int result = jf.showSaveDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            fileName = jf.getSelectedFile().getAbsolutePath();
                        }
                        /* Checking which filter is selected */
                        if (jf.getFileFilter() == pngFilter) {
                            ImageIO.write(image, "png", new File(fileName + ".png"));
                        } else {
                            ImageIO.write(image, "jpeg", new File(fileName + ".jpg"));
                        }
                        client.getTcpConnection().send(new Message(MessageType.SAVE_IMAGE));
                    } catch (IOException ex) {
                        errorDialogWindow("I / O error");
                    }
                }
            };
            JMenuItem saveAsMenu = new JMenuItem(saveAsAction);
            fileMenu.add(saveAsMenu);
        }

        private void createPaintPanel() {
            japan = new PaintPanel();
            japan.setBounds(30, 30, 260, 260);
            japan.setBackground(Color.white);
            japan.setOpaque(true);
            paintFrame.add(japan);
        }

        private void createToolBarInstrumentions() {
            JToolBar toolbar = new JToolBar("Toolbar", JToolBar.VERTICAL);
            toolbar.setBounds(0, -15, 30, 510);
            toolbar.setBackground(Color.DARK_GRAY);
            toolbar.setBorderPainted(false);

            JButton penButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("pen.png"))));
            penButton.setBackground(Color.DARK_GRAY);
            penButton.setBorderPainted(false);
            penButton.addActionListener(event -> mode = 0);
            toolbar.add(penButton);

            JButton brushButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("brush.png"))));
            brushButton.setBackground(Color.DARK_GRAY);
            brushButton.setBorderPainted(false);
            brushButton.addActionListener(event -> mode = 1);
            toolbar.add(brushButton);

            JButton eraserButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("eraser.png"))));
            eraserButton.setBackground(Color.DARK_GRAY);
            eraserButton.setBorderPainted(false);
            eraserButton.addActionListener(event -> mode = 2);
            toolbar.add(eraserButton);

            JButton textButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("text.png"))));
            textButton.setBackground(Color.DARK_GRAY);
            textButton.setBorderPainted(false);
            textButton.addActionListener(event -> mode = 3);
            toolbar.add(textButton);

            JButton lineButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("line.png"))));
            lineButton.setBackground(Color.DARK_GRAY);
            lineButton.setBorderPainted(false);
            lineButton.addActionListener(event -> mode = 4);
            toolbar.add(lineButton);

            JButton ovalButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("oval.png"))));
            ovalButton.setBackground(Color.DARK_GRAY);
            ovalButton.setBorderPainted(false);
            ovalButton.addActionListener(event -> mode = 5);
            toolbar.add(ovalButton);

            JButton rectButton = new JButton(new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("rect.png"))));
            rectButton.setBackground(Color.DARK_GRAY);
            rectButton.setBorderPainted(false);
            rectButton.addActionListener(event -> mode = 6);
            toolbar.add(rectButton);

            paintFrame.add(toolbar);
        }

        private void createToolBarColor() {
            JToolBar colorBar = new JToolBar("ColorBar", JToolBar.HORIZONTAL);
            colorBar.setBounds(15, 0, 860, 30);
            colorBar.setBackground(Color.DARK_GRAY);
            colorBar.setBorderPainted(false);

            colorButton = new JButton();
            colorButton.setBackground(mainColor);
            colorButton.setBounds(15, 5, 20, 20);
            colorButton.addActionListener(event -> {
                ColorDialog colorDialog = new ColorDialog(paintFrame, "Color selection");
                colorDialog.setBounds(480, 270, 680, 400);
                colorDialog.setVisible(true);
            });
            colorBar.add(colorButton);

            JButton redButton = new JButton();
            redButton.setBackground(Color.red);
            redButton.setBounds(40, 5, 15, 15);
            redButton.addActionListener(event -> {
                mainColor = Color.red;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(redButton);

            JButton orangeButton = new JButton();
            orangeButton.setBackground(Color.orange);
            orangeButton.setBounds(60, 5, 15, 15);
            orangeButton.addActionListener(event -> {
                mainColor = Color.orange;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(orangeButton);

            JButton yellowButton = new JButton();
            yellowButton.setBackground(Color.yellow);
            yellowButton.setBounds(80, 5, 15, 15);
            yellowButton.addActionListener(event -> {
                mainColor = Color.yellow;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(yellowButton);

            JButton greenButton = new JButton();
            greenButton.setBackground(Color.green);
            greenButton.setBounds(100, 5, 15, 15);
            greenButton.addActionListener(event -> {
                mainColor = Color.green;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(greenButton);

            JButton blueButton = new JButton();
            blueButton.setBackground(Color.blue);
            blueButton.setBounds(120, 5, 15, 15);
            blueButton.addActionListener(event -> {
                mainColor = Color.blue;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(blueButton);

            JButton cyanButton = new JButton();
            cyanButton.setBackground(Color.cyan);
            cyanButton.setBounds(140, 5, 15, 15);
            cyanButton.addActionListener(event -> {
                mainColor = Color.cyan;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(cyanButton);

            JButton magentaButton = new JButton();
            magentaButton.setBackground(Color.magenta);
            magentaButton.setBounds(160, 5, 15, 15);
            magentaButton.addActionListener(event -> {
                mainColor = Color.magenta;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(magentaButton);

            JButton whiteButton = new JButton();
            whiteButton.setBackground(Color.white);
            whiteButton.setBounds(180, 5, 15, 15);
            whiteButton.addActionListener(event -> {
                mainColor = Color.white;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(whiteButton);

            JButton blackButton = new JButton();
            blackButton.setBackground(Color.black);
            blackButton.setBounds(200, 5, 15, 15);
            blackButton.addActionListener(event -> {
                mainColor = Color.black;
                colorButton.setBackground(mainColor);
            });
            colorBar.add(blackButton);

            jSliderSize = new JSlider(1, 50, 10);
            jSliderSize.setBounds(230, 5, 100, 20);
            jSliderSize.setBackground(Color.DARK_GRAY);
            jSliderSize.setPaintTicks(true);
            jSliderSize.setMinorTickSpacing(10);
            jSliderSize.setPaintTrack(true);
            jSliderSize.setMajorTickSpacing(10);
            colorBar.add(jSliderSize);

            colorBar.setLayout(null);

            paintFrame.add(colorBar);
        }

        private void createChangeListener() {
            tcc = new JColorChooser(mainColor);
            tcc.getSelectionModel().addChangeListener(e -> {
                mainColor = tcc.getColor();
                colorButton.setBackground(mainColor);
            });
        }

        private void createJapanListener() {
            japan.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    try {
                        if (pressed) {
                            String message;
                            switch (mode) {
                                /* Pen */
                                case 0:
                                    message = mainColor.getRGB() + " " + xPad + " " + yPad + " " + e.getX() + " " + e.getY() + " " + jSliderSize.getValue();
                                    client.getTcpConnection().send(new Message(MessageType.PAINTING_PEN, message));
                                    break;
                                /* Brush */
                                case 1:
                                    message = mainColor.getRGB() + " " + xPad + " " + yPad + " " + e.getX() + " " + e.getY() + " " + jSliderSize.getValue();
                                    client.getTcpConnection().send(new Message(MessageType.PAINTING_BRUSH, message));
                                    break;
                                /* Eraser */
                                case 2:
                                    message = xPad + " " + yPad + " " + e.getX() + " " + e.getY() + " " + jSliderSize.getValue();
                                    client.getTcpConnection().send(new Message(MessageType.PAINTING_ERASER, message));
                                    break;
                            }
                            xPad = e.getX();
                            yPad = e.getY();
                        }
                        client.getTcpConnection().send(new Message(MessageType.PAINTING_REPAINT));
                    } catch (IOException err) {
                        errorDialogWindow( err.getMessage());
                    }
                }
            });
            japan.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        String message;
                        switch (mode) {
                            /* Pen */
                            case 0:
                                message = mainColor.getRGB() + " " + xPad + " " + yPad + " " + (xPad + 1) + " " + (yPad + 1) + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_PEN, message));
                                break;
                            /* Brush */
                            case 1:
                                message = mainColor.getRGB() + " " + xPad + " " + yPad + " " + (xPad + 1) + " " + (yPad + 1) + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_BRUSH, message));
                                break;
                            /* Eraser */
                            case 2:
                                message = xPad + " " + yPad + " " + (xPad + 1) + " " + (yPad + 1) + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_ERASER, message));
                                break;
                            /* Text */
                            case 3:
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_TEXT, "REQUEST_FOCUS"));
                                break;
                        }
                        xPad = e.getX();
                        yPad = e.getY();
                        pressed = true;
                        client.getTcpConnection().send(new Message(MessageType.PAINTING_REPAINT));
                    } catch (IOException ex) {
                        errorDialogWindow( ex.getMessage());
                    }
                }

                public void mousePressed(MouseEvent e) {
                    xPad = e.getX();
                    yPad = e.getY();
                    xf = e.getX();
                    yf = e.getY();
                    pressed = true;
                }

                public void mouseReleased(MouseEvent e) {
                    try {
                        /* Calculation rectangle and oval */
                        String message;
                        int x1 = xf, x2 = xPad, y1 = yf, y2 = yPad;
                        if (xf > xPad) {
                            x2 = xf;
                            x1 = xPad;
                        }
                        if (yf > yPad) {
                            y2 = yf;
                            y1 = yPad;
                        }
                        switch (mode) {
                            /* Liner */
                            case 4:
                                message = mainColor.getRGB() + " " + xf + " " + yf + " " + e.getX() + " " + e.getY() + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_LINE, message));
                                break;
                            /* Oval */
                            case 5:
                                message = mainColor.getRGB() + " " + x1 + " " + y1 + " " + (x2 - x1) + " " + (y2 - y1) + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_OVAL, message));
                                break;
                            /* Rect */
                            case 6:
                                message = mainColor.getRGB() + " " + x1 + " " + y1 + " " + (x2 - x1) + " " + (y2 - y1) + " " + jSliderSize.getValue();
                                client.getTcpConnection().send(new Message(MessageType.PAINTING_RECT, message));
                                break;
                        }
                        xf = 0;
                        yf = 0;
                        pressed = false;
                        client.getTcpConnection().send(new Message(MessageType.PAINTING_REPAINT));
                    } catch (IOException ex) {
                        errorDialogWindow( ex.getMessage());
                    }
                }
            });
            japan.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    try {
                        client.getTcpConnection().send(new Message(MessageType.PAINTING_TEXT, "REQUEST_FOCUS"));
                    } catch (IOException ex) {
                        errorDialogWindow(ex.getMessage());
                    }
                }

                public void keyTyped(KeyEvent e) {
                    try {
                        if (mode == 3) {
                            String message;
                            String str = "";
                            str += e.getKeyChar();
                            message = mainColor.getRGB() + " " + str + " " + xPad + " " + yPad + " " + jSliderSize.getValue();
                            client.getTcpConnection().send(new Message(MessageType.PAINTING_TEXT, message));
                            xPad += 10;
                            client.getTcpConnection().send(new Message(MessageType.PAINTING_TEXT, "REQUEST_FOCUS"));
                            client.getTcpConnection().send(new Message(MessageType.PAINTING_REPAINT));
                        }
                    } catch (IOException ex) {
                        errorDialogWindow(ex.getMessage());
                    }
                }
            });
            paintFrame.addComponentListener(new ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    japan.setSize(paintFrame.getWidth() - 40, paintFrame.getHeight() - 40);
                    BufferedImage tempImage = new BufferedImage(japan.getWidth(), japan.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D d2 = tempImage.createGraphics();
                    d2.setColor(Color.white);
                    d2.fillRect(0, 0, japan.getWidth(), japan.getHeight());
                    tempImage.setData(image.getRaster());
                    image = tempImage;
                    japan.repaint();
                }
            });
            paintFrame.setLayout(null);
            paintFrame.setVisible(true);
        }

        public void connectBoard(String message) {
            String[] splitMessage = message.split(" ", 413340);
            int[] rgbArray = new int[413340];
            for (int i = 0; i < rgbArray.length - 1; i++) {
                rgbArray[i] = Integer.parseInt(splitMessage[i]);
            }
            image = new BufferedImage(830, 498, BufferedImage.TYPE_INT_RGB);
            image.setRGB(0, 0, 830, 498, rgbArray, 0, 830);
            paintFrame.setSize(image.getWidth() + 40, image.getHeight() + 40);
            japan.setSize(image.getWidth(), image.getHeight());
            japan.repaint();
            graphics = image.createGraphics();
            japan.repaint();
        }

        private class ColorDialog extends JDialog {
            public ColorDialog(JFrame owner, String title) {
                super(owner, title, true);
                add(tcc);
                setSize(200, 200);
            }
        }

        /**
         * Application window
         */
        private class PaintFrame extends JFrame {
            public void paint(Graphics g) {
                super.paint(g);
            }

            public PaintFrame(String title) {
                super(title);
            }
        }

        /**
         * Drawing panel
         */
        private class PaintPanel extends JPanel {
            public PaintPanel() {
            }

            public void paintComponent(Graphics g) {
                if (image == null) {
                    image = new BufferedImage(830, 498, BufferedImage.TYPE_INT_RGB);
                    graphics = image.createGraphics();
                    graphics.setColor(Color.white);
                    graphics.fillRect(0, 0, 830, 498);
                }
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
            }
        }

        /**
         * Image filter
         */
        private class TextFileFilter extends FileFilter {
            private final String ext;

            public TextFileFilter(String ext) {
                this.ext = ext;
            }

            public boolean accept(java.io.File file) {
                if (file.isDirectory()) return true;
                return (file.getName().endsWith(ext));
            }

            public String getDescription() {
                return "*" + ext;
            }
        }

        public void drawPen(int color, int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(size));
            graphics.drawLine(xPad, yPad, getX, getY);
        }

        public void drawBrash(int color, int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(size));
            graphics.fillOval(getX - (size/2), getY - (size/2), size, size);
        }

        public void drawEraser(int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(Color.white);
            graphics.setStroke(new BasicStroke(3.0f * size));
            graphics.drawLine(xPad, yPad, getX, getY);
        }

        public void drawText(int color, String str, int xPad, int yPad, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(2.0f * size));
            graphics.setFont(new Font("Arial", Font.PLAIN, 15));
            graphics.drawString(str, xPad, yPad);
        }

        public void drawTextRequestFocus() {
            japan.requestFocus();
        }

        public void drawLine(int color, int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(size));
            graphics.drawLine(xPad, yPad, getX, getY);
        }

        public void drawOval(int color, int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(size));
            graphics.drawOval(xPad, yPad, getX, getY);
        }

        public void drawRect(int color, int xPad, int yPad, int getX, int getY, int size) {
            graphics = image.createGraphics();
            graphics.setColor(new Color(color));
            graphics.setStroke(new BasicStroke(size));
            graphics.drawRect(xPad, yPad, getX, getY);
        }

        public void drawRepaint() {
            japan.repaint();
        }
    }
}