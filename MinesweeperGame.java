package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {


    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countClosedTiles = SIDE * SIDE;
    private final static String MINE = "\uD83D\uDCA3";
    private final static String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int score;






    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y,"");
                boolean isMine = getRandomNumber(10) == 0;
                if (isMine) {
                    countMinesOnField++;
                    setCellColor(x, y, Color.SILVER);
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;

    }

    private void countMineNeighbors(){
      for(int x = 0; x < SIDE; x++)
      {
          for(int y = 0; y < SIDE; y++)
          {

              if(!gameField[y][x].isMine)
              {
                  List<GameObject> neigbours = getNeighbors(gameField[y][x]);
                  for (GameObject neigbour : neigbours) {
                      if (neigbour.isMine) {
                          gameField[y][x].countMineNeighbors++;
                      }
                  }
              }
          }
      }
    }


    private void openTile(int x, int y) {
        if (!gameField[y][x].isOpen && !isGameStopped && !gameField[y][x].isFlag) {
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else {
                if (gameField[y][x].countMineNeighbors != 0) {
                    setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                    setCellColor(x, y, Color.GREENYELLOW);
                } else {
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.TEAL);
                    List<GameObject> list = getNeighbors(gameField[y][x]);
                    for (GameObject each : list)
                        if (!each.isOpen)
                            openTile(each.x, each.y);
                }
                score += 5;
                setScore(score);
                if (countClosedTiles == countMinesOnField)
                    win();
            }
        }
    }

    private void markTile( int x, int y ) {
        if (!gameField[y][x].isOpen && ( (countFlags > 0) || gameField[y][x].isFlag ) && !isGameStopped ) {
            if (!gameField[y][x].isFlag) {
                gameField[y][x].isFlag = true;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.DARKGREEN);
                countFlags--;
            } else {
                gameField[y][x].isFlag = false;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.GRAY);
                countFlags++;
            }
        }
    }


    private void gameOver()
    {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "GAME OVER!", Color.FIREBRICK, 42);
    }


    private void win()
    {
        isGameStopped = true;
        showMessageDialog(Color.FORESTGREEN, "YOU WIN!", Color.GREEN, 50);
    }


    private void restart()
    {
        isGameStopped = false;
        score = 0;
        setScore(0);
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        createGame();

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
       if(!isGameStopped)
       {
           openTile(x,y);
       }
       else {
           restart();
           super.onMouseLeftClick(x, y);
       }
    }


    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
        super.onMouseRightClick(x, y);
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}