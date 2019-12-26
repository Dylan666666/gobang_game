package com.oym.five.chess.fivechess;

import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 21:46
 * 白棋
 */
public class ChessWhite extends Canvas {

    /**
     * 白棋所在棋盘
     */
    ChessBoard chessBoard;

    public ChessWhite(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    @Override
    public void paint(Graphics g) {
        //画棋子
        g.setColor(Color.WHITE);
        g.fillOval(0,0,30,30);
    }
}
