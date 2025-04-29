package com.example.demofinalplease;

public class Player {
    private Hand hand;

    public Player() {
        hand = new Hand();
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public int getScore() {
        return hand.getScore();
    }

    public String getHand() {
        return hand.toString();
    }

    // Added method to return Hand object
    public Hand getHandObject() {
        return hand;
    }
}