package com.oym.five.chess.user;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 19:58
 * 用户聊天信息输入
 * 面板包含两个视图控件 
 * contentInputted 为 TextField控件，用户可以在其中输入聊天信息
 */
public class UserInput extends JPanel {
    public JTextField contentInputted = new JTextField("",0);
    public JComboBox userChoice = new JComboBox();

    public UserInput() { }
}
