package com.oym.five.chess.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 19:20
 *服务器端
 */
public class ServerRunner extends Frame implements ActionListener {
    JButton clearMsgButton = new JButton("清空");
    JButton serverStatusButton = new JButton("状态");
    JButton closeServerButton = new JButton("关闭");
    Panel buttonPanel = new Panel();
    ServerMsgPanel serverMsgPanel = new ServerMsgPanel();
    ServerSocket serverSocket;
    int clientAccessNumber = 1;

    /**
     * 将客户端套接口和输出流绑定
     */
    Hashtable clientDataHash = new Hashtable(50);
    /**
     * 将客户端套接口和客户名绑定
     */
    Hashtable clientNameHash = new Hashtable(50);
    /**
     * 将游戏创建者和游戏加入者绑定
     */
    Hashtable chessPeerHash = new Hashtable(50);

    public ServerRunner() {
        super("网络游戏对战平台服务器控制平台");
        setBackground(Color.PINK);
        buttonPanel.setLayout(new FlowLayout());
        clearMsgButton.setSize(50, 30);
        buttonPanel.add(clearMsgButton);
        clearMsgButton.addActionListener(this);
        serverStatusButton.setSize(50, 30);
        buttonPanel.add(serverStatusButton);
        serverStatusButton.addActionListener(this);
        closeServerButton.setSize(50, 30);
        buttonPanel.add(closeServerButton);
        closeServerButton.addActionListener(this);
        add(serverMsgPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        pack();
        setVisible(true);
        setSize(600, 440);
        setResizable(false);
        validate();

        try {
            createServer(1234, serverMsgPanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用指定端口和面板创建服务器
     * @param port
     * @param serverMsgPanel
     * @throws IOException
     */
    public void createServer(int port, ServerMsgPanel serverMsgPanel) throws IOException {
        // 客户端套接口
        Socket clientSocket;
        // 设定当前主机
        this.serverMsgPanel = serverMsgPanel;
        try {
            serverSocket = new ServerSocket(port);
            serverMsgPanel.msgTextArea.setText("服务器启动:"
                    + InetAddress.getLocalHost() + ":"
                    + serverSocket.getLocalPort() + "\n");
            while (true) {
                // 监听客户端套接口的信息 
                clientSocket = serverSocket.accept();
                serverMsgPanel.msgTextArea.append("已连接用户:" +
                        "吴彦祖" + clientAccessNumber +"\n" + clientSocket + "\n");
                // 建立客户端输出流 
                DataOutputStream outputData = new DataOutputStream(clientSocket.getOutputStream());
                // 将客户端套接口和输出流绑定 
                clientDataHash.put(clientSocket, outputData);
                // 将客户端套接口和客户名绑定 
                clientNameHash.put(clientSocket, ("吴彦祖" + clientAccessNumber++));
                // 创建并运行服务器端线程 
                ServerThread thread = new ServerThread(clientSocket,
                        clientDataHash, clientNameHash, chessPeerHash, serverMsgPanel);
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 清空服务器信息
        if (e.getSource() == clearMsgButton) {
            serverMsgPanel.msgTextArea.setText("");
        }

        // 显示服务器信息
        if (e.getSource() == serverStatusButton) {
            try {
                serverMsgPanel.msgTextArea.append("用户信息：" + "吴彦祖"
                        + (clientAccessNumber - 1) + "\n服务器信息:"
                        + InetAddress.getLocalHost() + ":"
                        + serverSocket.getLocalPort() + "\n");
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ServerRunner();
    }
    
}
