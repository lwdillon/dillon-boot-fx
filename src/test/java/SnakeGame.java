import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SnakeGame extends Application {

    private final int CELL_SIZE = 10;
    private final int WIDTH = 300;
    private final int HEIGHT = 300;

    private int x = WIDTH / 2; // initial x position
    private int y = HEIGHT / 2; // initial y position

    private int foodX; // food x position
    private int foodY; // food y position

    private int directionX = 0; // direction x
    private int directionY = 0; // direction y
    
    private int tailSize = 0; // tail size
    private int[] tailX = new int[WIDTH * HEIGHT]; // tail x position
    private int[] tailY = new int[WIDTH * HEIGHT]; // tail y position

    private Rectangle food; // food object
    private Rectangle snake; // snake object

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // create snake object
        snake = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
        snake.setFill(Color.BLACK);
        root.getChildren().add(snake);

        // create food object
        food = new Rectangle(foodX, foodY, CELL_SIZE, CELL_SIZE);
        food.setFill(Color.RED);
        root.getChildren().add(food);

        // set up game loop
        scene.setOnKeyPressed(e -> {
            // Handle key press
            KeyCode key = e.getCode();
            if (key == KeyCode.LEFT) {
                directionX = -1;
                directionY = 0;
            }
            if (key == KeyCode.RIGHT) {
                directionX = 1;
                directionY = 0;
            }
            if (key == KeyCode.UP) {
                directionX = 0;
                directionY = -1;
            }
            if (key == KeyCode.DOWN) {
                directionX = 0;
                directionY = 1;
            }
        });

        // define game logic
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Handle game loop
                // move snake
                x += directionX * CELL_SIZE;
                y += directionY * CELL_SIZE;

                // check if the snake has eaten the food
                if (x == foodX && y == foodY) {
                    tailSize++;
                    // generate new food
                    foodX = (int) (Math.random() * (WIDTH - CELL_SIZE)) / CELL_SIZE * CELL_SIZE;
                    foodY = (int) (Math.random() * (HEIGHT - CELL_SIZE)) / CELL_SIZE * CELL_SIZE;
                    food.relocate(foodX, foodY);
                }

                // update tail position
                for (int i = tailSize; i > 0; i--) {
                    tailX[i] = tailX[i - 1];
                    tailY[i] = tailY[i - 1];
                }
                tailX[0] = x;
                tailY[0] = y;

                // draw tail
                for (int i = 0; i < tailSize; i++) {
                    Rectangle tailCell = new Rectangle(tailX[i], tailY[i], CELL_SIZE, CELL_SIZE);
                    tailCell.setFill(Color.BLACK);
                    root.getChildren().add(tailCell);
                }

                // relocate snake
                snake.relocate(x, y);
            }
        }.start();
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}