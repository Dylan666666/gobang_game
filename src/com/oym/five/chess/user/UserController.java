package com.oym.five.chess.user;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 19:54
 * 用户操作面板
 * （创建游戏,加入游戏等功能实现）
 */
public class UserController extends JPanel {
    private JLabel ipLabel = new JLabel("IP地址:",JLabel.LEFT);
    public JTextField ipInputted = new JTextField("localhost",8);
    public JButton connectButton = new JButton("连接");
    public JButton createButton = new JButton("等待挑战");
    public JButton joinButton = new JButton("加入挑战");
    public JButton cancelButton = new JButton("投降~");
    public JButton exitButton = new JButton("溜~~");
    public UserController() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.PINK);
        add(ipLabel);
        add(ipInputted);
        add(connectButton);
        add(createButton);
        add(joinButton);
        add(cancelButton);
        add(exitButton);
    }
}
