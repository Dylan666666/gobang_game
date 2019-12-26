package com.oym.five.chess.server;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 19:28
 * 服务器信息输出面板
 */
public class ServerMsgPanel extends Panel {
    public TextArea msgTextArea = new TextArea("", 30, 71,
            TextArea.SCROLLBARS_VERTICAL_ONLY);
    public JLabel statusLabel = new JLabel("当前用户连接数:", Label.LEFT );
    public Panel msgPanel = new Panel();
    public Panel statusPanel = new Panel();
    public ServerMsgPanel() {
        setSize(600, 600);
        setBackground(Color.PINK);
        setLayout(new BorderLayout());
        msgPanel.setLayout(new FlowLayout());
        msgPanel.setSize(600, 600);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setSize(600, 600);
        msgPanel.add(msgTextArea);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(msgPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);
    }
}
