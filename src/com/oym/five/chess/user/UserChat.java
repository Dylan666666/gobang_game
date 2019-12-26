package com.oym.five.chess.user;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 19:52
 * 用户聊天页面
 * 聊天面板为一个TextArea视图控件，拥有一个垂直方向的滚动条。
 * 该TextArea被添加到面板中，使用 "BorderLayout" 布局格式。
 */
public class UserChat extends JPanel {
    public JTextArea chatTextArea=new JTextArea("  许多国家的人对五子棋都有不同的爱称，例如：\n" +
            "   韩国人把五子棋称为“情侣棋”，暗示情人之间下五子棋有利于增加情感的交流" +
            "；\n    欧洲人称其为“绅士棋”，代表下五子棋的君子风度胜似绅士；\n" +
            "   日本人则称其为“中老年棋”，说明五子棋适合中老年人的生理特点和思维方式。",15,18);
    public UserChat() {
        setLayout(new BorderLayout());
        chatTextArea.setAutoscrolls(true);
        chatTextArea.setSelectionColor(Color.PINK);
        chatTextArea.setCaretColor(Color.PINK);
        chatTextArea.setDisabledTextColor(Color.PINK);
        chatTextArea.setLineWrap(true);
        add(chatTextArea,BorderLayout.CENTER);
    }
}
