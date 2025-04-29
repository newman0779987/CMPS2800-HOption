package com.example.demofinalplease;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;

    public Deck() {
        deck = new ArrayList<Card>();
        String[] ranks = {"2","3","4","5","6","7","8","9","10","jack","queen","king","ace"};
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};

        for(String suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card dealCard() {
        return deck.remove(deck.size() - 1);
    }

    public int remainingCards() {
        return deck.size();
    }
}