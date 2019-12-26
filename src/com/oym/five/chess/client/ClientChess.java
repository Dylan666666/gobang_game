package com.oym.five.chess.client;

import com.oym.five.chess.fivechess.ChessBoard;
import com.oym.five.chess.user.UserChat;
import com.oym.five.chess.user.UserController;
import com.oym.five.chess.user.UserInput;
import com.oym.five.chess.user.UserList;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static java.awt.Color.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/22 9:05
 * 客户端
 */
public class ClientChess extends Frame implements ActionListener, KeyListener {

    /**
     * 客户端套接口
     */
    public Socket clientSocket;

    /**
     * 数据输入流
     */
    public DataInputStream inputStream;

    /**
     * 数据输出流
     */
    public DataOutputStream outputStream;

    /**
     * 用户名
     */
    public String chessClientName = null;
    /**
     * 主机地址 
     */
    public String host = null;
    /**
     * 主机端口
     */
    public int port = 1234;
    /**
     * 是否在聊天
     */
    public boolean isOnChat = false;
    /**
     * 是否在下棋
     */
    public boolean isOnChess = false;
    /**
     * 游戏是否进行中
     */
    public boolean isGameConnected = false;
    /**
     * 是否为游戏创建者
     */
    public boolean isCreator = false;
    /**
     * 是否为游戏加入者
     */
    public boolean isParticipant = false;
    /**
     * 用户列表区
     */
    public UserList userListPad = new UserList();
    /**
     * 用户聊天区
     */
    protected UserChat userChatPad = new UserChat();
    /**
     * 用户操作区
     */
    public UserController userControlPad = new UserController();
    /**
     * 用户输入区
     */
    protected UserInput userInputPad = new UserInput();
    /**
     * 下棋区
     */
    ChessBoard chessBoard = new ChessBoard();
    /**
     * 面板区
     */
    private Panel southPanel = new Panel();
    private Panel centerPanel = new Panel();
    private Panel eastPanel = new Panel();

    /**
     * 构造方法，创建界面
     */
    public ClientChess() {
        super("美女么么哒五子棋");
        setLayout(new BorderLayout());
        host = userControlPad.ipInputted.getText();
        
        eastPanel.setLayout(new BorderLayout());
        eastPanel.add(userListPad, BorderLayout.NORTH);
        eastPanel.add(userChatPad, BorderLayout.CENTER);
        eastPanel.setBackground(new Color(238, 154, 73));

        userInputPad.contentInputted.addKeyListener(this);

        chessBoard.host =  (userControlPad.ipInputted.getText());
        centerPanel.add(chessBoard, BorderLayout.CENTER);
        centerPanel.add(userInputPad, BorderLayout.SOUTH);
        centerPanel.setBackground(new Color(238, 154, 73));
        userControlPad.connectButton.addActionListener(this);
        userControlPad.createButton.addActionListener(this);
        userControlPad.joinButton.addActionListener(this);
        userControlPad.cancelButton.addActionListener(this);
        userControlPad.exitButton.addActionListener(this);
        userControlPad.createButton.setEnabled(false);
        userControlPad.joinButton.setEnabled(false);
        userControlPad.cancelButton.setEnabled(false);

        southPanel.add(userControlPad, BorderLayout.CENTER);
        southPanel.setBackground(PINK);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 聊天中
                if (isOnChat) {
                    // 关闭客户端套接口
                    try {
                        clientSocket.close();
                    }
                    catch (Exception ed){}
                }

                if (isOnChess || isGameConnected) {
                    // 下棋中 
                    try {
                        // 关闭下棋端口 
                        chessBoard.chessSocket.close();
                    }
                    catch (Exception ee){}
                }
                System.exit(0);
            }
        });

        add(eastPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        pack();
        setSize(1000, 700);
        setVisible(true);
        setResizable(false);
        this.validate();
    }

    /**
     * 按指定的IP地址和端口连接到服务器
     * @param serverIP
     * @param serverPort
     * @return
     * @throws Exception
     */
    public boolean connectToServer(String serverIP, int serverPort) throws Exception {
        try {
            // 创建客户端套接口 
            clientSocket = new Socket(serverIP, serverPort);
            // 创建输入流 
            inputStream = new DataInputStream(clientSocket.getInputStream());
            // 创建输出流 
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            // 创建客户端线程 
            ClientThread clientThread = new ClientThread(this);
            // 启动线程，等待聊天信息 
            clientThread.start();
            isOnChat = true;
            return true;
        } catch (IOException ex) {
            userChatPad.chatTextArea.setText("Sorry,无法连接!!!\n");
        }
        return false;
    }

    /**
     * 客户端事件处理
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        // 连接到主机按钮单击事件
        if (e.getSource() == userControlPad.connectButton) {
            // 取得主机地址
            host = chessBoard.host = userControlPad.ipInputted.getText();
            try {
                // 成功连接到主机时，设置客户端相应的界面状态
                if (connectToServer(host, port)) {
                    userChatPad.chatTextArea.setText("");
                    userControlPad.connectButton.setEnabled(false);
                    userControlPad.createButton.setEnabled(true);
                    userControlPad.joinButton.setEnabled(true);
                    chessBoard.statusText.setText("连接成功，请等待!!!");
                }
            } catch (Exception ei) {
                userChatPad.chatTextArea.setText("Sorry,不能连接!!!\n");
            }
        }

        // 离开游戏按钮单击事件
        if (e.getSource() == userControlPad.exitButton) {
            // 若用户处于聊天状态中
            if (isOnChat) {
                try {
                    // 关闭客户端套接口 
                    clientSocket.close();
                }
                catch (Exception ed){}
            }

            // 若用户处于游戏状态中 
            if (isOnChess || isGameConnected) {
                try {
                    // 关闭游戏端口 
                    chessBoard.chessSocket.close();
                }
                catch (Exception ee){}
            }
            System.exit(0);
        }

        // 加入游戏按钮单击事件
        if (e.getSource() == userControlPad.joinButton) {
            // 取得要加入的游戏
            String selectedUser = userListPad.userList.getSelectedItem();
            // 若未选中要加入的用户，或选中的用户已经在游戏，则给出提示信息 
            if (selectedUser == null || selectedUser.startsWith("[inchess]") ||
                    selectedUser.equals(chessClientName)) {
                chessBoard.statusText.setText("必须选择一个用户!");
            } else {
                // 执行加入游戏的操作 
                try {
                    // 若游戏套接口未连接
                    if (!isGameConnected) {
                        // 若连接到主机成功
                        if (chessBoard.connectServer(chessBoard.host, chessBoard.port)) {
                            isGameConnected = true;
                            isOnChess = true;
                            isParticipant = true;
                            userControlPad.createButton.setEnabled(false);
                            userControlPad.joinButton.setEnabled(false);
                            userControlPad.cancelButton.setEnabled(true);
                            chessBoard.chessThread.sendMessage("/joingame "
                                    + userListPad.userList.getSelectedItem() + " "
                                    + chessClientName);
                        }
                    } else {
                        // 若游戏端口连接中 
                        isOnChess = true;
                        isParticipant = true;
                        userControlPad.createButton.setEnabled(false);
                        userControlPad.joinButton.setEnabled(false);
                        userControlPad.cancelButton.setEnabled(true);
                        chessBoard.chessThread.sendMessage("/joingame "
                                + userListPad.userList.getSelectedItem() + " "
                                + chessClientName);
                    }
                } catch (Exception ee) {
                    isGameConnected = false;
                    isOnChess = false;
                    isParticipant = false;
                    userControlPad.createButton.setEnabled(true);
                    userControlPad.joinButton.setEnabled(true);
                    userControlPad.cancelButton.setEnabled(false);
                    userChatPad.chatTextArea.setText("不能连接: \n" + ee);
                }
            }
        }

        // 创建游戏按钮单击事件 
        if (e.getSource() == userControlPad.createButton) {
            try {
                // 若游戏端口未连接
                if (!isGameConnected) {
                    if (chessBoard.connectServer(chessBoard.host, chessBoard.port)) {
                        // 若连接到主机成功 
                        isGameConnected = true;
                        isOnChess = true;
                        isCreator = true;
                        userControlPad.createButton.setEnabled(false);
                        userControlPad.joinButton.setEnabled(false);
                        userControlPad.cancelButton.setEnabled(true);
                        chessBoard.chessThread.sendMessage("/creatgame " + "[inchess]" + chessClientName);
                    }
                } else {
                    // 若游戏端口连接中 
                    isOnChess = true;
                    isCreator = true;
                    userControlPad.createButton.setEnabled(false);
                    userControlPad.joinButton.setEnabled(false);
                    userControlPad.cancelButton.setEnabled(true);
                    chessBoard.chessThread.sendMessage("/creatgame "
                            + "[inchess]" + chessClientName);
                }
            } catch (Exception ec) {
                isGameConnected = false;
                isOnChess = false;
                isCreator = false;
                userControlPad.createButton.setEnabled(true);
                userControlPad.joinButton.setEnabled(true);
                userControlPad.cancelButton.setEnabled(false);
                ec.printStackTrace();
                userChatPad.chatTextArea.setText("Sorry,不能连接: \n" + ec);
            }
        }

        // 退出游戏按钮单击事件 
        if (e.getSource() == userControlPad.cancelButton) {
            // 游戏中
            if (isOnChess) {
                chessBoard.chessThread.sendMessage("/giveup " + chessClientName);
                chessBoard.setVicStatus(-1 * chessBoard.chessColor);
                userControlPad.createButton.setEnabled(true);
                userControlPad.joinButton.setEnabled(true);
                userControlPad.cancelButton.setEnabled(false);
                chessBoard.statusText.setText("请选择创建房间或加入游戏!!!");
            } if (!isOnChess) {
                // 非游戏中 
                userControlPad.createButton.setEnabled(true);
                userControlPad.joinButton.setEnabled(true);
                userControlPad.cancelButton.setEnabled(false);
                chessBoard.statusText.setText("请选择创建房间或加入游戏!!!");
            }
            isParticipant = isCreator = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        TextField inputwords = (TextField) e.getSource();
        // 处理回车按键事件
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // 给所有人发信息 
            if (userInputPad.userChoice.getSelectedItem().equals("所有用户")) {
                try {
                    // 发送信息 
                    outputStream.writeUTF(inputwords.getText());
                    inputwords.setText("");
                } catch (Exception ea) {
                    userChatPad.chatTextArea.setText("Sorry,不能连接到服务器!\n");
                    userListPad.userList.removeAll();
                    userInputPad.userChoice.removeAll();
                    inputwords.setText("");
                    userControlPad.connectButton.setEnabled(true);
                }
            } else {
                // 给指定人发信息 
                try {
                    outputStream.writeUTF("/" + userInputPad.userChoice.getSelectedItem()
                            + " " + inputwords.getText());
                    inputwords.setText("");
                } catch (Exception ea) {
                    userChatPad.chatTextArea.setText("Sorry,不能连接到服务器!\n");
                    userListPad.userList.removeAll();
                    userInputPad.userChoice.removeAll();
                    inputwords.setText("");
                    userControlPad.connectButton.setEnabled(true);
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new ClientChess();
    }
}
