import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GoBoom1 {
    private static final int NumPlayers = 4;
    private static final int NumCardsPerPlayer = 7;
    private static final List<String> SUITS = Arrays.asList("c", "d", "h", "s");
    private static final List<String> RANKS = Arrays.asList("K", "Q", "J", "X", "9", "8", "7", "6", "5", "4", "3", "2", "A");

    private List<String>[] players;
    private List<String> deck;
    private List<String> center;
    private int currentPlayerIndex;
    private int currentTrick;
    private int[] scores;

    public GoBoom1() {
        deck = new ArrayList<>();
        players = new List[NumPlayers];
        for (int i = 0; i < NumPlayers; i++) {
            players[i] = new ArrayList<>();
        }
        center = new ArrayList<>();
        currentPlayerIndex = -1;
        currentTrick = 1;
        scores = new int[NumPlayers];
        
    }


    public void startGame() {
        deck.clear();
        players[0] = new ArrayList<>();
        players[1] = new ArrayList<>();
        players[2] = new ArrayList<>();
        players[3] = new ArrayList<>();
        center.clear();

        initializeGame();
        dealCards();
        firstPlayer();
        printGame();
    }

    private void initializeGame() { 
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(suit + rank);
            }
        }
    }

    private void dealCards() {
        Collections.shuffle(deck);

        for (int i = 0; i < NumCardsPerPlayer; i++) {
            for (int j = 0; j < NumPlayers; j++) {
                players[j].add(deck.remove(0));
            }
        }
    }


    private void firstPlayer() { //mel
        String firstCard = deck.remove(0); // to remove 1 card from deck
        center.add(firstCard); // add the removed card to the center
        String rank = firstCard.substring(1); //checking rank to det 1st player

        if (rank.equals("A") || rank.equals("5") || rank.equals("9") || rank.equals("K")) {//task4
            currentPlayerIndex = 0; // Player1 => unique index starts from 0.
        } else if (rank.equals("2") || rank.equals("6") || rank.equals("10")) {
            currentPlayerIndex = 1; // Player2
        } else if (rank.equals("3") || rank.equals("7") || rank.equals("J")) {
            currentPlayerIndex = 2; // Player3
        } else {
            currentPlayerIndex = 3; // Player4
        }
        
        System.out.println("-----------------------------------------------------");
        System.out.println(" s = start a new game || x = exit || d = draw a card ");
        System.out.println("-----------------------------------------------------");
        System.out.println("First lead card: " + firstCard);
        System.out.println("First player: Player" + (currentPlayerIndex + 1));
        System.out.println();
    }

    private void printGame() {
        boolean gameEnd = false;

        while (!gameEnd) {
            int trickCount = center.size() - 1; 
            int currentPlayer = (currentPlayerIndex + trickCount) % 4;

            System.out.println("Trick #" + currentTrick);
            displayPlayerCards();
            displayCenter();
            displayDeck();
            displayScore();

            System.out.println("Turn: Player" + (currentPlayer+1 ));
            System.out.print("> ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equals("s")) {
                startGame();
            } else if (input.equals("x")) {
                gameEnd = true;
                continue;
            } else if (input.equals("d")) {
                if (deck.isEmpty()) {
                    System.out.println("Deck is empty. Skipping turn.");
                } else {
                    String drawnCard = deck.remove(0);
                    players[currentPlayer].add(drawnCard);
                    System.out.println("Player" + (currentPlayer + 1) + " drew a card: " + drawnCard);
                    //currentPlayerIndex = (currentPlayerIndex + 1) % 4;
                }
            } else if (input.length() == 2) {
                String suit = input.substring(0, 1);
                String rank = input.substring(1);
                String playedCard = suit.toLowerCase() + rank;

                if (isValidCard(playedCard)) {
                    players[currentPlayer].remove(playedCard);
                    center.add(playedCard);
                    System.out.println("Player" + (currentPlayer + 1) + " played: " + playedCard);

                    if (center.size() == 5) {
                        determineTrickWinner();
                        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
                        center.clear();
                    }
                } else {
                    System.out.println("Invalid card. Please play a valid card.");
                }
            } else {
                System.out.println("Invalid command. Please enter a valid command.");
            }

            if (center.size() == NumPlayers+1) {
                String leadSuit = center.get(0).substring(0, 1);
                String highestRankCard = findHighestRankCardWithSuit(leadSuit);
    
                int winnerIndex = (currentPlayer + center.indexOf(highestRankCard)) % NumPlayers+1;
                scores[winnerIndex]++;
                System.out.println("*** Player" + (winnerIndex + 1) + " wins Trick #" + currentTrick + " ***");
    
                currentTrick++;
                currentPlayer = winnerIndex;
                center.clear();
            } else {
                currentPlayer = (currentPlayer + 1) % NumPlayers;
            }
        }

            System.out.println();
    }

    private void displayPlayerCards() {
        for (int i = 0; i < NumPlayers; i++) {
            System.out.print("Player" + (i + 1) + ": ");
            System.out.println(players[i]);
        }
    }

    private void displayCenter() {
        System.out.print("Center: ");
        for (String card : center) {
            System.out.print(card + " ");
        }
        System.out.println();
    }
    
    private void displayDeck() {
        System.out.print("Deck: ");
        System.out.println(deck);
    }

    private void displayScore() {//Part 2
        System.out.println("Score: Player1 = 0 | Player2 = 0 | Player3 = 0 | Player4 = 0");
    }


    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public int getRankValue(String ranks) {
        switch (ranks) {
            case "2"  : return 2;
            case "3"  : return 3;
            case "4"  : return 4;
            case "5"  : return 5;
            case "6"  : return 6;
            case "7"  : return 7;
            case "8"  : return 8;
            case "9"  : return 9;
            case "10" : return 10;
            case "J"  : return 11;
            case "Q"  : return 12;
            case "K"  : return 13;
            case "A"  : return 14;
            default : return 0;
        }
    }

    public int getSuitValue(String suits) {
        switch (suits) {
            case "c": return 1;
            case "d": return 2;
            case "h": return 3;
            case "s": return 4;
            default : return 0;
        }
    }


    /*
        String leadCard = center.get(0);
        String leadSuit = leadCard.substring(0, 1);
        String leadRank = leadCard.substring(1);

        String suit = card.substring(0, 1);
        String rank = card.substring(1);

    */

    private boolean isValidCard(String card) {
        if (center.isEmpty()) {
            return true;
        }

        String leadSuit = center.get(0).substring(0, 1);
        String leadRank = center.get(0).substring(1);
        String cardSuit = card.substring(0, 1);
        String cardRank = card.substring(1);

        if (cardSuit.equals(leadSuit) || cardRank.equals(leadRank)) {
            return true;
        }

        for (String playerCard : players[currentPlayerIndex]) {
            String playerCardSuit = playerCard.substring(0, 1);
            String playerCardRank = playerCard.substring(1);
            if (playerCardSuit.equals(leadSuit) || playerCardRank.equals(leadRank)) {
                return false;
            }
        }

        return true;
    }

    private String findHighestRankCardWithSuit(String suit) {
        String highestRankCard = null;
        for (String card : center) {
            if (card.substring(0, 1).equals(suit)) {
                if (highestRankCard == null || compareRanks(card.substring(1), highestRankCard.substring(1)) > 0) {
                    highestRankCard = card;
                }
            }
        }
        return highestRankCard;
    }

    private int compareRanks(String rank1, String rank2) {
        List<String> RANKS = Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");
        int index1 = RANKS.indexOf(rank1);
        int index2 = RANKS.indexOf(rank2);
        return Integer.compare(index1, index2);
    
    }
    
    private void determineTrickWinner() {
        String leadCard = center.get(0);
        String leadSuit = leadCard.substring(0, 1);
        String leadRank = leadCard.substring(1);
        int winningPlayerIndex = currentPlayerIndex;
        String winningCard = leadCard;
    
        for (int i = 1; i < center.size(); i++) {
            String currentCard = center.get(i);
            String currentSuit = currentCard.substring(0, 1);
            String currentRank = currentCard.substring(1);
    
            if (currentSuit.equals(leadSuit) && compareRanks(currentRank, leadRank) > 0) {
                leadRank = currentRank;
                winningPlayerIndex = (currentPlayerIndex + i) % NumPlayers;
                winningCard = currentCard;
            }
        }
    
        System.out.println("*** Player " + (winningPlayerIndex + 1) + " wins Trick #" + currentTrick + " with " + winningCard + " ***");
        currentPlayerIndex = winningPlayerIndex;
        currentTrick++;
        displayScore();
    }
    
    

    //private boolean isHigherRank(String rank, String highestRank) {
    //    return RANKS.indexOf(rank) > RANKS.indexOf(highestRank);
    //}

    private List<String> cardOwn;
    

    public Player(List<String> cardOwn) {
        this.cardOwn = cardOwn;
    }

    public List<String> getCardOwn() {
        return cardOwn;
    }


    public static void main(String[] args) {
        GoBoom1 game = new GoBoom1();
        game.startGame();
    }
}