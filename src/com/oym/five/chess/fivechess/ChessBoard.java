package com.oym.five.chess.fivechess;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 20:07
 * 棋盘
 */
public class ChessBoard extends Panel implements MouseListener, ActionListener {
    public int dis = 30;
    // 鼠标是否能使用 
    public boolean isMouseEnabled = false;
    // 是否胜利 
    public boolean isWon = false;
    // 棋子的x轴坐标位 
    public int chessX_POS = -1;
    // 棋子的y轴坐标位 
    public int chessY_POS = -1;
    // 棋子的颜色 
    public int chessColor = 1;
    // 黑棋x轴坐标位数组 
    public int chessBlack_XPOS[] = new int[200];
    // 黑棋y轴坐标位数组 
    public int chessBlack_YPOS[] = new int[200];
    // 白棋x轴坐标位数组 
    public int chessWhite_XPOS[] = new int[200];
    // 白棋y轴坐标位数组 
    public int chessWhite_YPOS[] = new int[200];
    // 黑棋数量 
    public int chessBlackCount = 0;
    // 白棋数量 
    public int chessWhiteCount = 0;
    // 黑棋获胜次数 
    public int chessBlackVicTimes = 0;
    // 白棋获胜次数 
    public int chessWhiteVicTimes = 0;
    // 套接口 
    public Socket chessSocket;
    protected DataInputStream inputData;
    protected DataOutputStream outputData;
    public String chessSelfName = null;
    public String chessPeerName = null;
    public String host = null;
    public int port = 1234;
    public TextField statusText = new TextField("请先连接服务器！！！");
    public ChessThread chessThread = new ChessThread(this);

    public ChessBoard() {
        setSize(600, 600);
        setLayout(null);
        setBackground(new Color(205, 133, 63));
        addMouseListener(this);
        add(statusText);
        statusText.setBounds(new Rectangle(80, 5, 440, 24));
        statusText.setEditable(false);
    }

    /**
     * 连接到主机
     * @param ServerIP
     * @param ServerPort
     * @return
     * @throws Exception
     */
    public boolean connectServer(String ServerIP, int ServerPort) throws Exception {
        try {
            // 取得主机端口 
            chessSocket = new Socket(ServerIP, ServerPort);
            // 取得输入流 
            inputData = new DataInputStream(chessSocket.getInputStream());
            // 取得输出流 
            outputData = new DataOutputStream(chessSocket.getOutputStream());
            chessThread.start();
            return true;
        } catch (IOException ex) {
            statusText.setText("sorry，连接失败!!! \n");
        }
        return false;
    }

    /**
     * 设定胜利时的棋盘状态
     * @param vicChessColor
     */
    public void setVicStatus(int vicChessColor) {
        // 清空棋盘 
        this.removeAll();
        // 将黑棋的位置设置到零点 
        for (int i = 0; i <= chessBlackCount; i++) {
            chessBlack_XPOS[i] = 0;
            chessBlack_YPOS[i] = 0;
        }
        // 将白棋的位置设置到零点 
        for (int i = 0; i <= chessWhiteCount; i++) {
            chessWhite_XPOS[i] = 0;
            chessWhite_YPOS[i] = 0;
        }
        // 清空棋盘上的黑棋数 
        chessBlackCount = 0;
        // 清空棋盘上的白棋数 
        chessWhiteCount = 0;
        add(statusText);
        statusText.setBounds(40, 5, 200, 24);
        // 黑棋胜
        if (vicChessColor == 1) {
            chessBlackVicTimes++;
            statusText.setText("恭喜黑方胜！！！黑VS白 " + chessBlackVicTimes + ":" + chessWhiteVicTimes
                    + ".游戏重启，等待白方...");
            // 白棋胜
        } else if (vicChessColor == -1) {
            chessWhiteVicTimes++;
            statusText.setText("恭喜白方胜！！！黑VS白 " + chessBlackVicTimes + ":" + chessWhiteVicTimes
                    + ".游戏重启,等待黑方...");
        }
    }

    /**
     * 取得指定棋子的位置
     * @param xPos
     * @param yPos
     * @param chessColor
     */
    public void setLocation(int xPos, int yPos, int chessColor) {
        // 棋子为黑棋时
        if (chessColor == 1) {
            chessBlack_XPOS[chessBlackCount] = xPos * dis;
            chessBlack_YPOS[chessBlackCount] = yPos * dis;
            chessBlackCount++;
        // 棋子为白棋时
        } else if (chessColor == -1) {
            chessWhite_XPOS[chessWhiteCount] = xPos * dis;
            chessWhite_YPOS[chessWhiteCount] = yPos * dis;
            chessWhiteCount++;
        }
    }

    /**
     * 五子棋核心算法
     * @param xPos
     * @param yPos
     * @param chessColor
     * @return
     */
    public boolean checkVicStatus(int xPos, int yPos, int chessColor) {
        // 连接棋子数
        int chessLinkedCount = 1;
        // 用于比较是否要继续遍历一个棋子的相邻网格 
        int chessLinkedCompare = 1;
        // 要比较的棋子在数组中的索引位置
        int chessToCompareIndex = 0;
        // 相邻网格的位置
        int closeGrid = 1;
        // 黑棋时
        if (chessColor == 1) {
            // 将该棋子自身算入的话，初始连接数为1
            chessLinkedCount = 1;
            //以下每对for循环语句为一组，因为下棋的位置能位于中间而非两端
            
            // 遍历相邻4个网格
            // 判断当前下的棋子的右边4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                // 遍历棋盘上所有黑棋子
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if (((xPos + closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos * dis) == chessBlack_YPOS[chessToCompareIndex])) {
                        // 连接数加1 
                        chessLinkedCount = chessLinkedCount + 1;
                        // 五子相连时，胜利 
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                    // 若中间有一个棋子非黑棋，则会进入此分支，此时无需再遍历
                } else {
                    break;
                }
            }
            
            // 判断当前下的棋子的左边4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if (((xPos - closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && (yPos * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            // 进入新的一组for循环时要将连接数等重置 
            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            // 判断当前下的棋子的上边4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if ((xPos * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }
            
            // 判断当前下的棋子的下边4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if ((xPos * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    // 判断当前下的棋子的左上方向4个棋子是否都为黑棋 
                    if (((xPos - closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    // 判断当前下的棋子的右下方向4个棋子是否都为黑棋 
                    if (((xPos + closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            // 判断当前下的棋子的右上方向4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if (((xPos + closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }
            
            // 判断当前下的棋子的左下方向4个棋子是否都为黑棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessBlackCount; chessToCompareIndex++) {
                    if (((xPos - closeGrid) * dis == chessBlack_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessBlack_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }
        // 白棋的情况 
        } else if (chessColor == -1) {
            chessLinkedCount = 1;
            // 判断当前下的棋子的右边4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos + closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && (yPos * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            // 判断当前下的棋子的左边4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos - closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && (yPos * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            // 判断当前下的棋子的上边4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if ((xPos * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            // 判断当前下的棋子的下边4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if ((xPos * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            // 判断当前下的棋子的左上方向4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos - closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            // 判断当前下的棋子的右下方向4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos + closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            chessLinkedCount = 1;
            chessLinkedCompare = 1;
            // 判断当前下的棋子的右上方向4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos + closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos + closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return true;
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }

            // 判断当前下的棋子的左下方向4个棋子是否都为白棋
            for (closeGrid = 1; closeGrid <= 4; closeGrid++) {
                for (chessToCompareIndex = 0; chessToCompareIndex <= chessWhiteCount; chessToCompareIndex++) {
                    if (((xPos - closeGrid) * dis == chessWhite_XPOS[chessToCompareIndex])
                            && ((yPos - closeGrid) * dis == chessWhite_YPOS[chessToCompareIndex])) {
                        chessLinkedCount++;
                        if (chessLinkedCount == 5) {
                            return (true);
                        }
                    }
                }
                if (chessLinkedCount == (chessLinkedCompare + 1)) {
                    chessLinkedCompare++;
                } else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 画棋盘
     */
    @Override
    public void paint(Graphics g) {
        for (int i = 40; i <= 580; i = i + 30) {
            g.drawLine(40, i, 580, i);
        }


        for (int j = 40; j <= 580; j = j + 30) {
            g.drawLine(j, 40, j, 580);
        }
        g.fillOval(127, 127, 8, 8);
        g.fillOval(487, 127, 8, 8);
        g.fillOval(127, 487, 8, 8);
        g.fillOval(487, 487, 8, 8);
        g.fillOval(307, 307, 8, 8);
    }

    /**
     * 画棋子 
     * @param xPos
     * @param yPos
     * @param chessColor
     */
    public void paintChessPoint(int xPos, int yPos, int chessColor) {
        ChessBlack chessBlack = new ChessBlack(this);
        ChessWhite chessWhite = new ChessWhite(this);
        // 黑棋
        if (chessColor == 1 && isMouseEnabled) {
            // 设置棋子的位置 
            setLocation(xPos, yPos, chessColor);
            // 取得当前局面状态 
            isWon = checkVicStatus(xPos, yPos, chessColor);
            // 非胜利状态
            if (isWon == false) {
                chessThread.sendMessage("/" + chessPeerName + " /chess "
                        + xPos + " " + yPos + " " + chessColor);
                // 将棋子添加到棋盘中
                this.add(chessBlack);
                // 设置棋子边界 
                chessBlack.setBounds(xPos * dis -4, yPos * dis -3, 30, 30);
                statusText.setText("黑(第" + chessBlackCount + "步)" + xPos + " " + yPos + ",轮到白方.");
                // 将鼠标设为不可用 
                isMouseEnabled = false;
                // 胜利状态
            } else {
                chessThread.sendMessage("/" + chessPeerName + " /chess "
                        + xPos + " " + yPos + " " + chessColor);
                this.add(chessBlack);
                chessBlack.setBounds(xPos * dis -4, yPos * dis -3, 30, 30);
                // 调用胜利方法，传入参数为黑棋胜利
                setVicStatus(1);
                isMouseEnabled = false;
            }
            // 白棋 
        } else if (chessColor == -1 && isMouseEnabled) {
            setLocation(xPos, yPos, chessColor);
            isWon = checkVicStatus(xPos, yPos, chessColor);
            if (isWon == false) {
                chessThread.sendMessage("/" + chessPeerName + " /chess "
                        + xPos + " " + yPos + " " + chessColor);
                this.add(chessWhite);
                chessWhite.setBounds(xPos * dis -4, yPos * dis-3 , 30, 30);
                statusText.setText("白(第" + chessWhiteCount + "步)" + xPos + " " + yPos + ",轮到黑方.");
                isMouseEnabled = false;
            } else {
                chessThread.sendMessage("/" + chessPeerName + " /chess "
                        + xPos + " " + yPos + " " + chessColor);
                this.add(chessWhite);
                chessWhite.setBounds(xPos * dis-4 , yPos * dis -3, 30, 30);
                // 调用胜利方法，传入参数为白棋 
                setVicStatus(-1);
                isMouseEnabled = false;
            }
        }
    }

    /**
     * 画网络棋盘
     * @param xPos
     * @param yPos
     * @param chessColor
     */
    public void paintNetChessPoint(int xPos, int yPos, int chessColor) {
        ChessBlack chessBlack = new ChessBlack(this);
        ChessWhite chessWhite = new ChessWhite(this);
        setLocation(xPos, yPos, chessColor);
        if (chessColor == 1) {
            isWon = checkVicStatus(xPos, yPos, chessColor);
            if (isWon == false) {
                this.add(chessBlack);
                chessBlack.setBounds(xPos * dis-4 , yPos * dis -3, 30, 30);
                statusText.setText("黑(第" + chessBlackCount + "步)" + xPos + " " + yPos + ",轮到白方.");
                isMouseEnabled = true;
            } else {
                chessThread.sendMessage("/" + chessPeerName + " /victory " + chessColor);
                this.add(chessBlack);
                chessBlack.setBounds(xPos * dis -4, yPos * dis-3, 30, 30);
                setVicStatus(1);
                isMouseEnabled = true;
            }
        } else if (chessColor == -1) {
            isWon = checkVicStatus(xPos, yPos, chessColor);
            if (isWon == false) {
                this.add(chessWhite);
                chessWhite.setBounds(xPos * dis-4, yPos * dis-3, 30, 30);
                statusText.setText("白(第" + chessWhiteCount + "步)" + xPos + " " + yPos + ",轮到黑方.");
                isMouseEnabled = true;
            } else {
                chessThread.sendMessage("/" + chessPeerName + " /victory " + chessColor);
                this.add(chessWhite);
                chessWhite.setBounds(xPos * dis-4, yPos * dis-3, 30, 30);
                setVicStatus(-1);
                isMouseEnabled = true;
            }
        }
    }

    /**
     * 捕获下棋事件
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
            chessX_POS = e.getX();
            System.out.println(chessX_POS);
            chessY_POS = e.getY();
            System.out.println(chessY_POS);
            int a = (chessX_POS) / dis, b = (chessY_POS) / dis;
            System.out.println("a="+a);
            System.out.println("b="+b);
            // 下棋位置不正确时，不执行任何操作 
            if (chessX_POS / dis < 2 || chessY_POS / dis < 2
                    || chessX_POS / dis > 19 || chessY_POS / dis > 19) {
            } else {
                // 画棋子
                paintChessPoint(a, b, chessColor);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
