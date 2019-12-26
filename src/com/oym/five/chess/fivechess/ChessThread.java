package com.oym.five.chess.fivechess;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 20:14
 * 棋盘多线程
 */
public class ChessThread extends Thread {

    /**
     * 当前线程的棋盘 
     */
    private ChessBoard thisBoard;

    public ChessThread(ChessBoard thisBoard) {
        this.thisBoard = thisBoard;
    }

    /**
     * 处理取得的信息
     * @param msgReceived
     */
    public void dealWithMsg(String msgReceived) {
        // 收到的信息为下棋
        if (msgReceived.startsWith("/chess")) {
            StringTokenizer userMsgToken = new StringTokenizer(msgReceived, " ");
            // 表示棋子信息的数组、0索引为：x坐标；1索引位：y坐标；2索引位：棋子颜色 
            String[] chessInfo = { "-1", "-1", "0" };
            // 标志位
            int i = 0;
            String chessInfoToken;
            while (userMsgToken.hasMoreTokens()) {
                chessInfoToken = userMsgToken.nextToken(" ");
                if (i >= 1 && i <= 3) {
                    chessInfo[i - 1] = chessInfoToken;
                }
                i++;
            }
            thisBoard.paintNetChessPoint(Integer.parseInt(chessInfo[0]), Integer
                    .parseInt(chessInfo[1]), Integer.parseInt(chessInfo[2]));
            // 收到的信息为改名
        } else if (msgReceived.startsWith("/yourname ")) {
            thisBoard.chessSelfName = msgReceived.substring(10);
            // 收到的为错误信息
        } else if (msgReceived.equals("/error")) {
            thisBoard.statusText.setText("用户不存在，请重新加入!!!");
        }
    }

    /**
     * 发送信息
     * @param sentMessage
     */
    public void sendMessage(String sentMessage) {
        try {
            thisBoard.outputData.writeUTF(sentMessage);
        } catch (Exception ea) {
            ea.printStackTrace();;
        }
    }

    @Override
    public void run() {
        String msgReceived = "";
        try {
            // 等待信息输入
            while (true) {
                msgReceived = thisBoard.inputData.readUTF();
                dealWithMsg(msgReceived);
            }
        } catch (IOException es){}
    }

}
