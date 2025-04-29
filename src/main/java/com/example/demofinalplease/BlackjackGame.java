package com.example.demofinalplease;

public class BlackjackGame {
    private final Deck deck;
    private final Player player;
    private final Player dealer;
    private boolean gameActive;

    public BlackjackGame() {
        deck = new Deck();
        player = new Player();
        dealer = new Player();
        gameActive = false;
    }

    public void startGame() {
        dealInitialCards();
        gameActive = true;
    }

    private void dealInitialCards() {
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        dealer.addCard(deck.dealCard());
    }

    public void playerHit() {
        if (gameActive && player.getScore() < 21) {
            player.addCard(deck.dealCard());
            if (player.getScore() >= 21) {
                gameActive = false;
            }
        }
    }

    public void playerStand() {
        if (gameActive) {
            gameActive = false;
            dealerTurn();
        }
    }

    private void dealerTurn() {
        while (dealer.getScore() < 21) {
            if (player.getScore() > dealer.getScore() && player.getScore() <= 21) {
                dealer.addCard(deck.dealCard());
            } else if (player.getScore() == 21 && dealer.getScore() < 21) {
                dealer.addCard(deck.dealCard());
            } else {
                break;
            }
        }
    }

    public String determineWinner() {
        int playerScore = player.getScore();
        int dealerScore = dealer.getScore();

        if (playerScore > 21) {
            return "You lost! You busted.";
        } else if (dealerScore > 21) {
            return "You win! Dealer busted.";
        } else if (playerScore > dealerScore) {
            return "You win!";
        } else if (dealerScore > playerScore) {
            return "You lose!";
        } else {
            return "It's a tie.";
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Player getDealer() {
        return dealer;
    }

    public boolean isGameActive() {
        return gameActive;
    }
}