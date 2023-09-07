
 package com.lw.fx.client;


 import javafx.animation.AnimationTimer;
 import javafx.beans.DefaultProperty;
 import javafx.beans.property.BooleanProperty;
 import javafx.beans.property.BooleanPropertyBase;
 import javafx.beans.property.ObjectProperty;
 import javafx.beans.property.ObjectPropertyBase;
 import javafx.collections.ObservableList;
 import javafx.event.ActionEvent;
 import javafx.geometry.VPos;
 import javafx.scene.Node;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;
 import javafx.scene.input.MouseEvent;
 import javafx.scene.layout.Pane;
 import javafx.scene.layout.Region;
 import javafx.scene.paint.Color;
 import javafx.scene.shape.Rectangle;
 import javafx.scene.text.TextAlignment;

 import java.util.function.Consumer;


 /**
  * User: hansolo
  * Date: 01.02.21
  * Time: 13:38
  */
 @DefaultProperty("children")
 public class CanvasControl extends Region {
     private static final double PREFERRED_WIDTH = Region.USE_COMPUTED_SIZE;
     private static final double PREFERRED_HEIGHT = Region.USE_COMPUTED_SIZE;
     private static final double MINIMUM_WIDTH = Region.USE_COMPUTED_SIZE;
     private static final double MINIMUM_HEIGHT = Region.USE_COMPUTED_SIZE;
     private static final double MAXIMUM_WIDTH = Double.MAX_VALUE;
     private static final double MAXIMUM_HEIGHT = Double.MAX_VALUE;
     private static final Color DEFAULT_BACKGROUND_COLOR = Color.web("#3a609be6");
     private static final Color DEFAULT_HIGHLIGHT_COLOR = Color.web("#ffffff80");
     private static final Color DEFAULT_FOREGROUND_COLOR = Color.WHITESMOKE;

     private  int smallLineSpacing = 10; // 每隔10像素绘制一条竖线
     private  int bigLineSpacing = 100; // 每隔100像素绘制一条竖线

     private static final long UPDATE_INTERVAL = 100_000;
     private String userAgentStyleSheet;
     private double aspectRatio;
     private boolean keepAspect;
     private double width;
     private double height;
     private Canvas canvas;
     private GraphicsContext ctx;
     private Rectangle clip;
     private Pane pane;
     private boolean hovered;
     private boolean pressed;
     private ObjectProperty<Color> backgroundColor;
     private ObjectProperty<Color> foregroundColor;
     private Consumer<ActionEvent> actionConsumer;


     // ******************** Constructors **************************************
     public CanvasControl() {

         this.aspectRatio = PREFERRED_HEIGHT / PREFERRED_WIDTH;
         this.keepAspect = true;
         this.hovered = false;
         this.pressed = false;

         this.backgroundColor = new ObjectPropertyBase<>(DEFAULT_BACKGROUND_COLOR) {
             @Override
             protected void invalidated() {
                 redraw();
             }

             @Override
             public Object getBean() {
                 return CanvasControl.this;
             }

             @Override
             public String getName() {
                 return "backgroundColorTop";
             }
         };
         this.foregroundColor = new ObjectPropertyBase<>(DEFAULT_FOREGROUND_COLOR) {
             @Override
             protected void invalidated() {
                 redraw();
             }

             @Override
             public Object getBean() {
                 return CanvasControl.this;
             }

             @Override
             public String getName() {
                 return "foregroundColor";
             }
         };


         initGraphics();
         registerListeners();
     }


     // ******************** Initialization ************************************
     private void initGraphics() {
         if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
                 Double.compare(getHeight(), 0.0) <= 0) {
             if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                 setPrefSize(getPrefWidth(), getPrefHeight());
             } else {
                 setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
             }
         }

         getStyleClass().add("canvas-control");

         canvas = new Canvas(getPrefWidth(), getPrefHeight());
         canvas.setPickOnBounds(true);

         ctx = canvas.getGraphicsContext2D();
         ctx.setTextBaseline(VPos.CENTER);
         ctx.setTextAlign(TextAlignment.CENTER);

         clip = new Rectangle();
         canvas.setClip(clip);

         pane = new Pane(canvas);

         getChildren().setAll(pane);
     }

     private void registerListeners() {
         widthProperty().addListener(o -> resize());
         heightProperty().addListener(o -> resize());
         canvas.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
             hovered = true;
             redraw();
         });
         canvas.addEventFilter(MouseEvent.MOUSE_EXITED, e -> {
             hovered = false;
             redraw();
         });
         canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
             pressed = true;
             redraw();
             if (null == actionConsumer) {
                 return;
             }
             actionConsumer.accept(new ActionEvent());
         });
         canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
             pressed = false;
             redraw();
         });
     }


     // ******************** Methods *******************************************
     @Override
     protected double computeMinWidth(final double height) {
         return MINIMUM_WIDTH;
     }

     @Override
     protected double computeMinHeight(final double width) {
         return MINIMUM_HEIGHT;
     }

     @Override
     protected double computePrefWidth(final double height) {
         return super.computePrefWidth(height);
     }

     @Override
     protected double computePrefHeight(final double width) {
         return super.computePrefHeight(width);
     }

     @Override
     protected double computeMaxWidth(final double height) {
         return MAXIMUM_WIDTH;
     }

     @Override
     protected double computeMaxHeight(final double width) {
         return MAXIMUM_HEIGHT;
     }

     @Override
     public ObservableList<Node> getChildren() {
         return super.getChildren();
     }


     public Color getBackgroundColor() {
         return backgroundColor.get();
     }

     public void setBackgroundColor(final Color color) {
         backgroundColor.set(color);
     }

     public ObjectProperty<Color> backgroundColorProperty() {
         return backgroundColor;
     }

     public Color getForegroundColor() {
         return foregroundColor.get();
     }

     public void setForegroundColor(final Color color) {
         foregroundColor.set(color);
     }

     public ObjectProperty<Color> foregroundColorProperty() {
         return foregroundColor;
     }



     public void setOnAction(final Consumer<ActionEvent> actionConsumer) {
         this.actionConsumer = actionConsumer;
     }


     // ******************** Layout *******************************************
     @Override
     public void layoutChildren() {
         super.layoutChildren();
     }



     private void resize() {
         width = getWidth() - getInsets().getLeft() - getInsets().getRight();
         height = getHeight() - getInsets().getTop() - getInsets().getBottom();


         if (width > 0 && height > 0) {
             pane.setMaxSize(width, height);
             pane.setPrefSize(width, height);
//             pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

             clip.setX(1);
             clip.setY(1);
             clip.setWidth(width - 2);
             clip.setHeight(height - 2);

             canvas.setWidth(width);
             canvas.setHeight(height);


             redraw();
         }
     }

     private void redraw() {


         ctx.clearRect(0, 0, width, height);
         // Background
         ctx.save(); // inner shadow
         if (pressed) {
         } else {
         }
         ctx.stroke();
         //scale
         ctx.setFill(Color.BLACK);
         ctx.fillRect(0, 0, width, 20);
         ctx.fillRect(0, 0, 20, height);
         ctx.stroke();


         // 循环绘制小间隔和大间隔竖线
         for (int x = 20; x <= width; x += smallLineSpacing) {
             ctx.setStroke(Color.WHITE);
             // 如果x是大间隔的倍数，绘制大间隔竖线
             if (x % bigLineSpacing == 0) {
                 ctx.setFill(Color.WHITE);
                 ctx.strokeLine(x, 0, x, 20);
                 ctx.fillText(x+"", x+15, 10);
                 ctx.strokeLine(0, x, 20, x);
                 ctx.save();

                 // 旋转文字并绘制

                 ctx.translate(10, x+15); // 移动原点到文字位置
                 ctx.rotate(90); // 旋转90度以使文字竖直
                 ctx.strokeText(x+"", 0, 0); // 绘制文字
                 ctx.restore(); // 恢复GraphicsContext状态
             }else {
                 ctx.setFill(Color.WHITE);
                 ctx.strokeLine(x, 16, x, 20);
                 ctx.strokeLine(16, x, 20, x);
                 ctx.restore(); // 恢复GraphicsContext状态
             }
         }



     }



 }