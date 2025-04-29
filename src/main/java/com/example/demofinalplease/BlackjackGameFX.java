package com.example.demofinalplease;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class BlackjackGameFX extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double CARD_WIDTH = 100;
    private static final double CARD_HEIGHT = 150;
    private BlackjackGame game;
    private Pane root;
    private Text statusText;
    private Image backImage;
    private Rectangle backgroundRect;
    private Timeline backgroundAnimation;
    private Button startButton, hitButton, standButton, tryAgainButton;
    private HBox buttonBox;
    private ImageView logoView;
    private Timeline logoAnimation;
    private Clip backgroundMusicClip;
    private Clip clickSoundClip;
    private Clip winFanfareClip;
    private Clip loseFanfareClip;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        game = new BlackjackGame();

        // Setup audio using javax.sound.sampled
        setupAudio();

        // Setup tiled background
        setupBackground();

        // Status text, centered between cards, dynamically centered horizontally
        statusText = new Text(WIDTH / 2, (200 + (HEIGHT - CARD_HEIGHT - 50)) / 2, "Click Start to begin");
        statusText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        statusText.setFill(Color.WHITE);
        statusText.setStroke(Color.BLACK);
        statusText.setStrokeWidth(1.5);
        statusText.setEffect(new DropShadow(2, Color.BLACK));
        statusText.setTextAlignment(TextAlignment.CENTER);
        Font casino = Font.loadFont(getClass().getResourceAsStream("/fonts/CasinoFlat.ttf"), 35);
        statusText.setFont(casino);
        statusText.setId("status-text");

        // Dynamically center text when content changes
        statusText.textProperty().addListener((obs, oldValue, newValue) -> {
            double textWidth = statusText.getBoundsInLocal().getWidth();
            statusText.setX(WIDTH / 2 - textWidth / 2);
        });
        // Initial centering
        double initialTextWidth = statusText.getBoundsInLocal().getWidth();
        statusText.setX(WIDTH / 2 - initialTextWidth / 2);

        // Setup logo
        setupLogo();

        // Button container for Hit and Stand, centered on x-axis
        buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPrefWidth(WIDTH);
        buttonBox.setLayoutY(HEIGHT - 50);
        buttonBox.setId("button-box");

        // Start button, centered individually
        startButton = new Button("Start");
        startButton.setPrefWidth(100);
        startButton.setLayoutX(WIDTH / 2 - 50);
        startButton.setLayoutY(HEIGHT - 50);
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        startButton.setOnAction(e -> {
            if (clickSoundClip != null) playClip(clickSoundClip);
            startGame();
        });

        // Hit button
        hitButton = new Button("Hit");
        hitButton.setPrefWidth(100);
        hitButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
        hitButton.setOnMouseEntered(e -> hitButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        hitButton.setOnMouseExited(e -> hitButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        hitButton.setOnAction(e -> {
            if (clickSoundClip != null) playClip(clickSoundClip);
            hit();
        });
        hitButton.setVisible(false);

        // Stand button
        standButton = new Button("Stand");
        standButton.setPrefWidth(100);
        standButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
        standButton.setOnMouseEntered(e -> standButton.setStyle("-fx-background-color: #FFB300; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        standButton.setOnMouseExited(e -> standButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        standButton.setOnAction(e -> {
            if (clickSoundClip != null) playClip(clickSoundClip);
            stand();
        });
        standButton.setVisible(false);

        // Try Again button, centered individually
        tryAgainButton = new Button("Try Again");
        tryAgainButton.setPrefWidth(100);
        tryAgainButton.setLayoutX(WIDTH / 2 - 50);
        tryAgainButton.setLayoutY(HEIGHT - 50);
        tryAgainButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;");
        tryAgainButton.setOnMouseEntered(e -> tryAgainButton.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        tryAgainButton.setOnMouseExited(e -> tryAgainButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 5px;"));
        tryAgainButton.setOnAction(e -> {
            if (clickSoundClip != null) playClip(clickSoundClip);
            restartGame();
        });
        tryAgainButton.setVisible(false);

        buttonBox.getChildren().addAll(hitButton, standButton);
        root.getChildren().addAll(backgroundRect, logoView, statusText, buttonBox, startButton, tryAgainButton);

        primaryStage.setTitle("Blackjack Game");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (backgroundMusicClip != null) {
                backgroundMusicClip.stop();
                backgroundMusicClip.close();
            }
        });
        primaryStage.show();
    }

    private void setupAudio() {
        // Background music
        try {
            backgroundMusicClip = loadClip("/audio/background_music.wav");
            if (backgroundMusicClip != null) {
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            System.err.println("Warning: background_music.wav not found or unsupported; skipping background music. Error: " + e.getMessage());
        }

        // Click sound
        try {
            clickSoundClip = loadClip("/audio/click.wav");
        } catch (Exception e) {
            System.err.println("Warning: click.wav not found or unsupported; skipping click sound. Error: " + e.getMessage());
        }

        // Win fanfare
        try {
            winFanfareClip = loadClip("/audio/win_fanfare.wav");
        } catch (Exception e) {
            System.err.println("Warning: win_fanfare.wav not found or unsupported; skipping win fanfare. Error: " + e.getMessage());
        }

        // Lose fanfare
        try {
            loseFanfareClip = loadClip("/audio/lose_fanfare.wav");
        } catch (Exception e) {
            System.err.println("Warning: lose_fanfare.wav not found or unsupported; skipping lose fanfare. Error: " + e.getMessage());
        }
    }

    private Clip loadClip(String resourcePath) throws Exception {
        System.out.println("Attempting to load audio resource: " + resourcePath);
        InputStream audioStream = getClass().getResourceAsStream(resourcePath);
        if (audioStream == null) {
            System.out.println("Resource not found on classpath: " + resourcePath);
            System.out.println("Classpath: " + System.getProperty("java.class.path"));
            throw new Exception("Resource not found: " + resourcePath);
        }
        // Wrap in BufferedInputStream to support mark/reset
        BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            audioInputStream.close();
            bufferedStream.close();
            System.out.println("Successfully loaded audio resource: " + resourcePath);
            return clip;
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio format for: " + resourcePath + ". Error: " + e.getMessage());
            throw new Exception("Unsupported audio format: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error loading audio for: " + resourcePath + ". Error: " + e.getMessage());
            throw e;
        }
    }

    private void playClip(Clip clip) {
        if (clip != null) {
            // Rewind to start
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void setupBackground() {
        try {
            Image backgroundImage = new Image(getClass().getResourceAsStream("/background.png"));
            ImagePattern pattern = new ImagePattern(backgroundImage, 0, 0, 40, 40, false);
            backgroundRect = new Rectangle(0, 0, WIDTH, HEIGHT);
            backgroundRect.setFill(pattern);
            backgroundRect.setId("background-rect");

            backgroundAnimation = new Timeline(
                    new KeyFrame(Duration.millis(50), e -> {
                        ImagePattern current = (ImagePattern) backgroundRect.getFill();
                        double x = current.getX() + 1;
                        double y = current.getY() + 1;
                        if (x >= 40) x -= 40;
                        if (y >= 40) y -= 40;
                        backgroundRect.setFill(new ImagePattern(backgroundImage, x, y, 40, 40, false));
                    })
            );
            backgroundAnimation.setCycleCount(Timeline.INDEFINITE);
            backgroundAnimation.play();
        } catch (Exception e) {
            System.err.println("Warning: background.png not found; using gray background.");
            backgroundRect = new Rectangle(0, 0, WIDTH, HEIGHT);
            backgroundRect.setFill(Color.GRAY);
            backgroundRect.setId("background-rect");
        }
    }

    private void setupLogo() {
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/logo.png"));
            logoView = new ImageView(logoImage);
            logoView.setFitWidth(200);
            logoView.setFitHeight(100);
            logoView.setX(WIDTH / 2 - 100);
            double baseY = 100;
            logoView.setY(baseY);
            logoView.setId("logo-view");

            logoAnimation = new Timeline(
                    new KeyFrame(Duration.millis(16), e -> {
                        double time = System.currentTimeMillis() % 2000 / 2000.0;
                        double offset = 10 * Math.sin(2 * Math.PI * time);
                        logoView.setY(baseY + offset);
                    })
            );
            logoAnimation.setCycleCount(Timeline.INDEFINITE);
            logoAnimation.play();
        } catch (Exception e) {
            System.err.println("Warning: logo.png not found; skipping logo.");
            logoView = new ImageView();
            logoView.setVisible(false);
        }
    }

    private void startGame() {
        System.out.println("Starting game: Setting hitButton and standButton visible");
        game.startGame();
        renderGame();
        statusText.setText("Dealers hand\n Your move: Hit or Stand\nYour Hand");
        hitButton.setVisible(true);
        standButton.setVisible(true);
        startButton.setVisible(false);
        tryAgainButton.setVisible(false);
        logoView.setVisible(false);
        logoAnimation.pause();
        System.out.println("Hit button visible: " + hitButton.isVisible() + ", Stand button visible: " + standButton.isVisible());
    }

    private void hit() {
        System.out.println("Hit button clicked");
        game.playerHit();
        renderGame();
        if (!game.isGameActive()) {
            endGame();
        }
    }

    private void stand() {
        System.out.println("Stand button clicked");
        game.playerStand();
        renderGame();
        endGame();
    }

    private void endGame() {
        String result = game.determineWinner();
        statusText.setText(result);
        hitButton.setVisible(false);
        standButton.setVisible(false);
        startButton.setVisible(false);
        tryAgainButton.setVisible(true);
        logoView.setVisible(false);
        // Play win or lose fanfare based on result
        if (result.toLowerCase().contains("win") && winFanfareClip != null) {
            playClip(winFanfareClip);
        } else if ((result.toLowerCase().contains("lose") || result.toLowerCase().contains("bust")) && loseFanfareClip != null) {
            playClip(loseFanfareClip);
        }
        System.out.println("End game: Hit button visible: " + hitButton.isVisible() + ", Stand button visible: " + standButton.isVisible());
    }

    private void restartGame() {
        System.out.println("Restarting game: Starting new game directly");
        game = new BlackjackGame();
        root.getChildren().removeIf(node -> (node instanceof ImageView && node != logoView) || (node instanceof Rectangle && node != backgroundRect));
        startGame();
        System.out.println("Restart: Hit button visible: " + hitButton.isVisible() + ", Stand button visible: " + standButton.isVisible());
    }

    private void renderGame() {
        root.getChildren().removeIf(node -> {
            boolean isCardImage = node instanceof ImageView && node != logoView;
            boolean isCardPlaceholder = node instanceof Rectangle && node != backgroundRect;
            if (isCardImage || isCardPlaceholder) {
                System.out.println("Removing node: " + node.getClass().getSimpleName());
            }
            return isCardImage || isCardPlaceholder;
        });

        try {
            // Render player's hand
            ArrayList<Card> playerHand = getHandCards(game.getPlayer());
            System.out.println("Player hand size: " + playerHand.size());
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image image = loadCardImage(card);
                System.out.println("Player card " + i + ": " + card.toString() + ", Image loaded: " + (image != null));
                ImageView cardView;
                if (image == null) {
                    Rectangle placeholder = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
                    placeholder.setFill(Color.GRAY);
                    placeholder.setStroke(Color.BLACK);
                    placeholder.setX(50 + i * (CARD_WIDTH + 10));
                    placeholder.setY(HEIGHT - CARD_HEIGHT - 50);
                    root.getChildren().add(placeholder);
                    cardView = new ImageView(); // Empty ImageView to satisfy initialization
                } else {
                    cardView = new ImageView(image);
                    root.getChildren().add(cardView);
                }
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);
                cardView.setX(50 + i * (CARD_WIDTH + 10));
                cardView.setY(HEIGHT - CARD_HEIGHT - 50);
                cardView.setVisible(true);
            }

            // Render dealer's hand
            ArrayList<Card> dealerHand = getHandCards(game.getDealer());
            System.out.println("Dealer hand size: " + dealerHand.size());
            for (int i = 0; i < dealerHand.size(); i++) {
                ImageView cardView;
                if (i == 0 && game.isGameActive() && backImage != null) {
                    cardView = new ImageView(backImage);
                    System.out.println("Dealer hidden card: back.png");
                    root.getChildren().add(cardView);
                } else {
                    Image image = loadCardImage(dealerHand.get(i));
                    System.out.println("Dealer card " + i + ": " + dealerHand.get(i).toString() + ", Image loaded: " + (image != null));
                    if (image == null) {
                        Rectangle placeholder = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
                        placeholder.setFill(Color.GRAY);
                        placeholder.setStroke(Color.BLACK);
                        placeholder.setX(50 + i * (CARD_WIDTH + 10));
                        placeholder.setY(50);
                        root.getChildren().add(placeholder);
                        cardView = new ImageView(); // Empty ImageView to satisfy initialization
                    } else {
                        cardView = new ImageView(image);
                        root.getChildren().add(cardView);
                    }
                }
                cardView.setFitWidth(CARD_WIDTH);
                cardView.setFitHeight(CARD_HEIGHT);
                cardView.setX(50 + i * (CARD_WIDTH + 10));
                cardView.setY(50);
                cardView.setVisible(true);
            }
        } catch (Exception e) {
            System.err.println("Error rendering cards: " + e.getMessage());
            statusText.setText("Error rendering cards. Check console.");
        }
    }

    private Image loadCardImage(Card card) {
        String filename = card.getRank() + "_of_" + card.getSuit() + ".png";
        try {
            Image image = new Image(getClass().getResourceAsStream("/cards/" + filename));
            System.out.println("Loaded image: /cards/" + filename);
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image: /cards/" + filename);
            return null;
        }
    }

    private ArrayList<Card> getHandCards(Player player) {
        Hand hand = player.getHandObject();
        try {
            java.lang.reflect.Field cardsField = Hand.class.getDeclaredField("cards");
            cardsField.setAccessible(true);
            return (ArrayList<Card>) cardsField.get(hand);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access Hand.cards", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}