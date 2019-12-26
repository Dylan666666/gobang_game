package com.oym.five.chess.fivechess;

import java.awt.*;

/**
 * @Author: Mr_OO
 * @Date: 2019/12/21 21:41
 * 黑棋
 */
public class ChessBlack extends Canvas {

    /**
     * 黑棋所属的棋盘
     */
    private ChessBoard chessBoard;

    public ChessBlack(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    @Override
    public void paint(Graphics g) {
        //画棋子
        g.setColor(Color.BLACK);
        g.fillOval(0,0,30,30);
    }
}
