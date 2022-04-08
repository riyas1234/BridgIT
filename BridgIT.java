import java.util.ArrayList;
import java.util.Arrays;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Represents a cell
class Cell {
  Color color;
  int x;
  int y;
  boolean isFilled;
  Cell left;
  Cell right;
  Cell above;
  Cell below;
  
  /*
   * Fields:
   * this.color... String
   * this.x... int
   * this.y... int
   * this.isFilled... boolean
   * this.left... Cell
   * this.right... Cell
   * this.above... Cell
   * this.below... Cell
   * Methods:
   * this.draw(WorldScene)... void
   * Methods of fields:
   * left.draw(WorldScene)... void
   * right.draw(WorldScene)... void
   * above.draw(WorldScene)... void
   * below.draw(WorldScene)... void
   */
  
  // Constructors for a single cell
  Cell(Color color, int x, int y, boolean isFilled, Cell left, Cell right, Cell above, Cell below) {
    this.color = color;
    this.x = x;
    this.y = y;
    this.isFilled = isFilled;
    this.left = left;
    this.right = right;
    this.above = above;
    this.below = below;
  }
  
  // Draws the cell
  void draw(WorldScene scene) {
    
    /*
     * Parameters:
     * scene... WorldScene
     * Methods of Parameters:
     */
    
    scene.placeImageXY(new RectangleImage(40, 40, "solid", color), x, y);
  }

  void fillCell(Color turn) {
    this.color = turn;
  }

  boolean checkUp() {
    if (this.above != null) {
      return this.color == this.above.color && this.above.checkUp();
    }
    return true;
  }

  boolean checkDown() {
    if (this.below != null) {
      return this.color == this.below.color && this.below.checkDown();
    }
    return true;
  }

  boolean checkRight() {
    if (this.right != null) {
      return this.color == this.right.color && this.right.checkRight();
    }
    return true;
  }

  boolean checkLeft() {
    if (this.left != null) {
      return this.color == this.left.color && this.left.checkLeft();
    }
    return true;
  }

}

// Represents a board of cells
class Board {
  ArrayList<Cell> cells;
  int size;
  Color one;
  Color two;
  
  /*
   * Fields:
   * this.cells... ArrayList<Cell>
   * this.size... int
   * this.one... String
   * this.two... String
   * Methods:
   * this.draw()... void
   * Methods of fields:
   */
  
  // regular constructor for the board
  Board(ArrayList<Cell> cells, int size, Color one, Color two) {
    this.cells = cells;
    this.size = size;
    this.one = one;
    this.two = two;
  }
  
  // default constructor for a new board
  Board(int size, Color one, Color two) {
    // creates empty list of cells
    this.cells = new ArrayList<Cell>();
    
    // creates all cells with correct color and x y, with all positions null
    for (int row = 0; row < size; row ++) {
      for (int col = 0; col < size; col++) {
        Cell temp = new Cell(Color.WHITE, 20 + col * 40, 20 + row * 40, 
            false, null, null, null, null);
        if (col % 2 == 1 && row % 2 == 0) {
          temp.color = one;
          temp.isFilled = true;
        }
        if (col % 2 == 0 && row % 2 == 1) {
          temp.color = two;
          temp.isFilled = true;
        }
        cells.add(temp);
      }
    }
    
    // Associated all cells with each other according to position
    for (int i = 0; i < cells.size(); i ++) {  
      if (i + 1 < cells.size() && (i + 1) % size != 0) {
        cells.get(i).right = cells.get(i + 1);
      }
      if (i - 1 >= 0 && i % size != 0) {
        cells.get(i).left = cells.get(i - 1);
      }
      if (i + size < cells.size()) {
        cells.get(i).below = cells.get(i + size);
      }
      if (i - size >= 0) {
        cells.get(i).above = cells.get(i - size);
      } 
    }
  }
  
  // Draws the board
  void draw(WorldScene scene) {
    
    /*
     * Parameters:
     * scene... WorldScene
     * Methods of Parameters:
     */
    
    for (Cell c : cells) { 
      c.draw(scene);
    }
  }

  boolean fillCell(int x, int y, Color turn) {
    for (Cell c : cells) {
      if (c.x >= x - 20 && c.x <= x + 20 && c.y >= y - 20 && c.y <= y + 20 
          && c.color == Color.WHITE 
          && c.left != null && c.right != null && c.above != null && c.below != null) {
        c.fillCell(turn);
        return true;
      }
    }
    return false;
  }

  boolean checkWin() {
    boolean temp = false;
    for (Cell c : cells) {
      if ((c.checkUp() && c.checkDown()) || (c.checkRight() && c.checkLeft())) {
        temp = true;
      }
    }
    return temp;
  }
}

// Represents the BridgIT game
class BridgIT extends World {
  Board board;
  int size;
  Color one;
  Color two;
  Color turn;
  
  /*
   * Fields:
   * this.board... Board
   * this.size... int
   * this.one... Color
   * this.two... Color
   * Methods:
   * this.makeScene()... WorldScene
   * Methods of fields:
   * board.draw(WorldScene)... void
   */

  // regular constructor for the game
  BridgIT(Board board, int size, Color one, Color two) {
    this.board = board;
    this.size = size;
    this.one = one;
    this.two = two;
    this.turn = one;
  }

  // only constructs the game if the size is valid
  BridgIT(int size, Color one, Color two) {
    if (size % 2 != 0 & size >= 3) {
      this.size = size;
      this.one = one;
      this.two = two;
      this.board = new Board(size, one, two);
      this.turn = one;
    }
    else {
      throw new IllegalArgumentException("Invalid size: " + Integer.toString(size));
    }
  }

  // Makes the game scene
  public WorldScene makeScene() {
    
    /*
     * Parameters:
     * Methods of parameters:
     */
    
    WorldScene scene = new WorldScene(size * 40, size * 40);
    this.board.draw(scene);
    return scene;
  }
  
  public WorldScene lastScene(String message) {
    WorldScene scene = new WorldScene(size * 40, size *40);
    board.draw(scene);
    scene.placeImageXY(new TextImage(message, size + 20, Color.BLACK), size * 20, size * 20);
    return scene;
  }
  
  // checks key presses
  public void onKeyReleased(String key) {

    /*
     * Parameters:
     * key... String
     * Methods of Parameters:
     */

    if (key.equals("r")) {
      resetKey();
    }
  }

  // resets the board
  public void resetKey() {

    /*
     * Parameters:
     * Methods of parameters:
     */

    this.board = new Board(size, one, two);
    this.turn = one;
  }

  public void onMouseReleased(Posn posn) {

    /*
     * Parameters:
     * posn... Posn
     * Methods of parameters:
     */

    if (board.fillCell(posn.x, posn.y, turn)) {
      if (turn == one) {
        turn = two;
      }
      else {
        turn = one;
      }
    }
    
    if (board.checkWin()) {
      if (turn == one) { // swapped cuz it switches turns right before 
        this.endOfWorld("Player two wins!");
        //System.out.println("Player two wins!");
      }
      else {
        this.endOfWorld("Player one wins!");
        //System.out.println("Player one wins!");
      }
    }
  }
}

class ExamplesGames {
  BridgIT game;
  Cell cell1;
  Cell cell2;
  Cell cell3;
  Cell cell4;
  Board board1;
  Board board2;
  Board board3;
  Board board4;
  BridgIT game1;
  BridgIT game2;
  BridgIT game3;
  BridgIT game4;
  WorldScene newScene;
  WorldScene emptyScene;
  WorldScene a;
  WorldScene b;
  
  void initData() {
    game = new BridgIT(5, Color.DARK_GRAY, Color.BLUE);
    cell1 = new Cell(Color.BLUE, 50, 50, true, null, null, null, null);
    cell2 = new Cell(Color.GRAY, 100, 100, true, null, null, null, null);
    ArrayList<Cell> list = new ArrayList<Cell>(Arrays.asList(cell1, cell2));
    board1 = new Board(new ArrayList<Cell>(), 5, Color.BLUE, Color.CYAN);
    game1 = new BridgIT(board1, 5, Color.BLUE, Color.CYAN);
    board2 = new Board(list, 11, Color.BLUE, Color.CYAN);
    game2 = new BridgIT(board2, 11, Color.BLUE, Color.CYAN);
    newScene = new WorldScene(1000, 1000);
    emptyScene =  new WorldScene(1000, 1000);
    a = new WorldScene(200, 200);
    b =  new WorldScene(440, 440);
    cell3 = new Cell(Color.WHITE, 50, 50, true, cell4, cell4, cell4, cell4);
    cell4 = new Cell(Color.WHITE, 100, 100, true, null, null, null, null);
    ArrayList<Cell> list1 = new ArrayList<Cell>(Arrays.asList(cell3, cell4));
    board3 = new Board(list1, 11, Color.BLUE, Color.CYAN);
    game3 = new BridgIT(board3, 11, Color.BLUE, Color.CYAN);
    board4 = new Board(5, Color.GRAY, Color.BLUE);
    game4 =  new BridgIT(board4, 5, Color.GRAY, Color.BLUE);
  }
  
  void testBigBang(Tester t) {
    initData();
    this.game.bigBang(this.game.size * 40, this.game.size * 40);
  }
  
  void testDraw(Tester t) {
    initData();
    t.checkExpect(emptyScene, new WorldScene(1000, 1000));
    cell1.draw(emptyScene);
    newScene.placeImageXY(new RectangleImage(40, 40, "solid", cell1.color), 50, 50);
    t.checkExpect(emptyScene, newScene);
    
    initData();
    t.checkExpect(emptyScene, new WorldScene(1000, 1000));
    cell1.draw(emptyScene);
    cell2.draw(emptyScene);
    newScene.placeImageXY(new RectangleImage(40, 40, "solid", cell1.color), 50, 50);
    newScene.placeImageXY(new RectangleImage(40, 40, "solid", cell2.color), 100, 100);
    t.checkExpect(emptyScene, newScene);
  }

  void testMakeScene(Tester t) {
    initData();
    b.placeImageXY(new RectangleImage(40, 40, "solid", cell1.color), 50, 50);
    b.placeImageXY(new RectangleImage(40, 40, "solid", cell2.color), 100, 100);
    t.checkExpect(game1.makeScene(), a);
    t.checkExpect(game2.makeScene(), b);
  }

  boolean checkConstructorException(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Invalid size: 10"), "BridgIT",
        10, Color.CYAN, Color.BLUE)
        && t.checkConstructorException(new IllegalArgumentException("Invalid size: 12"), "BridgIT",
            12, Color.CYAN, Color.BLUE)
        && t.checkConstructorException(new IllegalArgumentException("Invalid size: 12"), "BridgIT",
            11, Color.CYAN, Color.BLUE);
  }
  
  void testLastScene(Tester t) {
    initData();
    b.placeImageXY(new RectangleImage(40, 40, "solid", cell1.color), 50, 50);
    b.placeImageXY(new RectangleImage(40, 40, "solid", cell2.color), 100, 100);
    b.placeImageXY(new TextImage("Player one wins!", 31, Color.BLACK), 220, 220);
    a.placeImageXY(new TextImage("Player two wins!", 25, Color.BLACK), 100, 100);
    t.checkExpect(game1.lastScene("Player two wins!"), a);
    t.checkExpect(game2.lastScene("Player one wins!"), b);
  }
  
  void testFillCell(Tester t) {
    initData();
    t.checkExpect(cell3.color, Color.WHITE);
    t.checkExpect(cell4.color, Color.WHITE);
    board3.fillCell(55, 45, Color.BLUE);
    board3.fillCell(110, 90, Color.BLUE);
    t.checkExpect(cell3.color, Color.BLUE);
    t.checkExpect(cell4.color, Color.WHITE);
  }
  
  void testCheckRight(Tester t) {
    initData();
    board4.fillCell(60, 60, Color.BLUE);
    board4.fillCell(140, 60, Color.BLUE);
    t.checkExpect(board4.cells.get(6).checkRight(), true);  
    t.checkExpect(board4.cells.get(10).checkRight(), false);
  }
  
  void testCheckLeft(Tester t) {
    initData();
    board4.fillCell(60, 60, Color.BLUE);
    board4.fillCell(140, 60, Color.BLUE);
    t.checkExpect(board4.cells.get(10).checkLeft(), true);  
    t.checkExpect(board4.cells.get(12).checkLeft(), false);
  }
  
  void testCheckUp(Tester t) {
    initData();
    board4.fillCell(60, 60, Color.GRAY);
    board4.fillCell(60, 140, Color.GRAY);
    t.checkExpect(board4.cells.get(16).checkUp(), true);  
    t.checkExpect(board4.cells.get(4).checkUp(), true);  
    t.checkExpect(board4.cells.get(20).checkUp(), false);  
  }
  
  void testCheckDown(Tester t) {
    initData();
    board4.fillCell(60, 60, Color.GRAY);
    board4.fillCell(60, 140, Color.GRAY);
    t.checkExpect(board4.cells.get(1).checkDown(), true);  
    t.checkExpect(board4.cells.get(24).checkDown(), true);  
    t.checkExpect(board4.cells.get(4).checkDown(), false);  
  }
  
  void testCheckWin(Tester t) {
    initData();
    t.checkExpect(board4.checkWin(), false);
    board4.fillCell(60, 60, Color.GRAY);
    t.checkExpect(board4.checkWin(), false);
    board4.fillCell(60, 140, Color.GRAY);
    t.checkExpect(board4.checkWin(), true);
  }  
}


