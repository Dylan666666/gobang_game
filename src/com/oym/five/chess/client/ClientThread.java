package com.oym.five.chess.client;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/22 9:05
 * 客户端多线程
 */
public class ClientThread extends Thread {
    private ClientChess clientChess;

    public ClientThread(ClientChess clientChess) {
        this.clientChess = clientChess;
    }

    public void dealWithMsg(String msgReceived) {
        // 若取得的信息为用户列表
        if (msgReceived.startsWith("/userlist ")) {  
            StringTokenizer userToken = new StringTokenizer(msgReceived, " ");
            int userNumber = 0;
            // 清空客户端用户列表 
            clientChess.userListPad.userList.removeAll();
            // 清空客户端用户下拉框 
            clientChess.userInputPad.userChoice.removeAll();
            // 给客户端用户下拉框添加一个选项 
            clientChess.userInputPad.userChoice.addItem("所有用户");
            // 当收到的用户信息列表中存在数据时
            while (userToken.hasMoreTokens()) {  
                // 取得用户信息
                String user = userToken.nextToken(" ");  
                // 用户信息有效时 
                if (userNumber > 0 && !user.startsWith("[inchess]")) { 
                    // 将用户信息添加到用户列表中
                    clientChess.userListPad.userList.add(user); 
                    // 将用户信息添加到用户下拉框中 
                    clientChess.userInputPad.userChoice.addItem(user); 
                }
                userNumber++;
            }
            // 下拉框默认选中所有人 
            clientChess.userInputPad.userChoice.setSelectedIndex(0);
            // 收到的信息为用户本名时
        } else if (msgReceived.startsWith("/yourname ")) {
            // 取得用户本名 
            clientChess.chessClientName = msgReceived.substring(10); 
            // 设置程序的标题 
            clientChess.setTitle("美女么么哒五子棋 " + "用户名:" + clientChess.chessClientName);
            // 收到的信息为拒绝用户时
        } else if (msgReceived.equals("/reject")) {  
            try {
                clientChess.chessBoard.statusText.setText("不能加入游戏!");
                clientChess.userControlPad.cancelButton.setEnabled(false);
                clientChess.userControlPad.joinButton.setEnabled(true);
                clientChess.userControlPad.createButton.setEnabled(true);
            } catch (Exception ef) {
                clientChess.userChatPad.chatTextArea
                        .setText("Cannot close!");
            }
            clientChess.userControlPad.joinButton.setEnabled(true);
            // 收到信息为游戏中的等待时
        } else if (msgReceived.startsWith("/peer ")) {  
            clientChess.chessBoard.chessPeerName = msgReceived.substring(6);
            // 若用户为游戏建立者
            if (clientChess.isCreator) {  
                // 设定其为黑棋先行
                clientChess.chessBoard.chessColor = 1;  
                clientChess.chessBoard.isMouseEnabled = true;
                clientChess.chessBoard.statusText.setText("黑方下...");
                // 若用户为游戏加入者 
            } else if (clientChess.isParticipant) { 
                // 设定其为白棋后性
                clientChess.chessBoard.chessColor = -1; 
                clientChess.chessBoard.statusText.setText("游戏开始，等待对手...");
            }
            // 收到信息为胜利信息 
        } else if (msgReceived.equals("/youwin")) { 
            clientChess.isOnChess = false;
            clientChess.chessBoard.setVicStatus(clientChess.chessBoard.chessColor);
            clientChess.chessBoard.statusText.setText("对手溜了！！！");
            clientChess.chessBoard.isMouseEnabled = false;
            // 收到信息为成功创建游戏
        } else if (msgReceived.equals("/OK")) {  
            clientChess.chessBoard.statusText.setText("游戏已创建，等待对手中...");
            // 收到信息错误
        } else if (msgReceived.equals("/error")) {  
            clientChess.userChatPad.chatTextArea.append("发送错误，退出程序...\n");
        } else {
            clientChess.userChatPad.chatTextArea.append(msgReceived + "\n");
            clientChess.userChatPad.chatTextArea.setCaretPosition(
                    clientChess.userChatPad.chatTextArea.getText().length());
        }
    }

    @Override
    public void run() {
        String message = "";
        try {
            while (true) {
                // 等待聊天信息，进入wait状态 
                message = clientChess.inputStream.readUTF();
                dealWithMsg(message);
            }
        }
        catch (IOException es){}
    }
}
