
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GoBoom {
    private static final int NumPlayers = 4;
    private static final int NumCardsPerPlayer = 7;
    private static final int TargetScore = 100;
    private Deck deck;
    private List<Card> center;
    private int currentPlayerIndex;
    private int currentTrick;
    private List<Card>[] players;
    private int[] scores;
    Map<String, List<Card>> player;

    public GoBoom() {
        deck = new Deck();
        center = new ArrayList<>();
        currentPlayerIndex = -1;
        currentTrick = 1;
        scores = new int[NumPlayers];
        players = new List[NumPlayers];
        player = new LinkedHashMap<>();
        for (int i = 0; i < NumPlayers; i++) {
            players[i] = new ArrayList<>();
            player.put("Player" + (i + 1), players[i]);
        }
    }

    public void dealCard() {
        for (int i = 0; i < NumPlayers; i++) {
            for (int j = 0; j < NumCardsPerPlayer; j++) {
                players[i].add(deck.remove());
            }
        }
    }

    public void reset() {
        currentTrick = 1;

        for (int i = 0; i < NumPlayers; i++) {
            scores[i] = 0;
        }
        center.clear();
    }

    public void startGame() {
        reset(); // Reset scores, round, and trick
        deck.clear();
        deck = new Deck();
        for (int i = 0; i < NumPlayers; i++) {
            players[i].clear();
        }
        center.clear();
        dealCard();
        firstPlayer();
        play();
    }

    private void firstPlayer() { // mel
        Card centerCard = deck.remove(); // to remove 1 card from deck
        center.add(centerCard); // add the removed card to the center
        String rank = centerCard.getRank(); // checking rank to det 1st player

        if (rank.equals("A") || rank.equals("5") || rank.equals("9") || rank.equals("K")) {// task4
            currentPlayerIndex = 0; // Player1 => unique index starts from 0.
        } else if (rank.equals("2") || rank.equals("6") || rank.equals("10")) {
            currentPlayerIndex = 1; // Player2
        } else if (rank.equals("3") || rank.equals("7") || rank.equals("J")) {
            currentPlayerIndex = 2; // Player3
        } else {
            currentPlayerIndex = 3; // Player4
        }

        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                " s = start a new game || x = exit || d = draw a card || Save = save current game || Load = load previous game || Reset = reset the game");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("First lead card: " + centerCard);
        System.out.println("First player: Player" + (currentPlayerIndex + 1));
        System.out.println();
    }

    public void printState() {
        System.out.println("Trick #" + currentTrick);
        for (Map.Entry<String, List<Card>> entry : player.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        System.out.println("Center:" + center);
        System.out.println("Deck:" + deck);
        System.out.println("Score: Player1 = " + scores[0] + " | Player2 = " + scores[1] + " | Player3 = " + scores[2]
                + " | Player4 = " + scores[3]);
    }

    public void play() {
        boolean gameEnd = false;
        int turnCount = 1;
        
        while (!gameEnd) {
            printState();
            System.out.println("Turn : Player" + (currentPlayerIndex + 1));
            System.out.print(">");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equals("s")||input.equals("S")) {
                startGame();
                gameEnd = true;
            } else if (input.equals("x")||input.equals("X")) {
                System.out.println("One of the irresponsible player quited the game, so GAME OVER...");
                gameEnd = true;
                continue;
            } else if (input.equals("d")||input.equals("D")) {
                if (deck.size() == 0) {
                    System.out.println("Deck is empty. Skipping turn.");
                    currentPlayerIndex = (currentPlayerIndex + 1) % NumPlayers;
                    turnCount++;
                    if (turnCount == 4) {
                        determineTrickWinner();
                        turnCount = 1;
                        continue;
                    }
                } else {
                    Card drawnCard = deck.remove();
                    players[currentPlayerIndex].add(drawnCard);
                    System.out.println("Player" + (currentPlayerIndex + 1) + " drew a card: " + drawnCard);
                }
            } else if (input.equals("Reset")) {
                reset();
                System.out.println("Score and trick is being reseted");
            } else if (input.equals("Save")) {
                System.out.println("Enter name for the save file. Example: game_state.txt");
                System.out.print(">");
                String fileName = scanner.nextLine();
                saveGame(fileName);
            } else if (input.equals("Load")) {
                System.out.println("Enter name for the save file. Example: game_state.txt");
                System.out.print(">");
                String fileName = scanner.nextLine();
                loadGame(fileName);
            } else if (input.length() == 2 || input.length() == 3) {
                Card playedCard = Card.fromString(input);

                if (isValidCard(playedCard, currentPlayerIndex)) {

                    players[currentPlayerIndex].remove(playedCard);
                    center.add(playedCard);
                    System.out.println("Player" + (currentPlayerIndex + 1) + " played: " + playedCard);

                    if (players[currentPlayerIndex].isEmpty()) {
                        System.out.println("Player" + (currentPlayerIndex + 1) + " announces: 'Boom!' Game over!");
                        calculateScore();
                        if (isGameEnd()) {
                            gameEnd = true;
                            continue;
                        } else {
                            startRound();
                        }
                    }

                    currentPlayerIndex = (currentPlayerIndex + 1) % NumPlayers;

                    if (turnCount == 4) {
                        determineTrickWinner();
                        turnCount = 1;
                        continue;
                    } else {
                        turnCount++;
                    }
                } else {
                    System.out.println("Invalid card. Choose another card or draw some cards to play a valid card.");
                }
            }

            else {
                System.out.println("Invalid command. Please enter a valid command.");
            }
        }

    }

    private boolean isValidCard(Card card, int currentPlayer) {
        if (center.isEmpty()) {
            return true;
        }

        String leadSuit = center.get(0).getSuit();
        String leadRank = center.get(0).getRank();
        String cardSuit = card.getSuit();
        String cardRank = card.getRank();

        if (cardSuit.equals(leadSuit) || cardRank.equals(leadRank)) {
            for (Card playerCard : players[currentPlayer]) {
                if (playerCard.equals(card)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int compareRanks(String rank1, String rank2) {
        List<String> RANKS = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
        int index1 = RANKS.indexOf(rank1);
        int index2 = RANKS.indexOf(rank2);
        return Integer.compare(index1, index2);

    }

    private void determineTrickWinner() {
        Card leadCard = center.get(0);
        String leadSuit = leadCard.getSuit();
        String leadRank = leadCard.getRank();
        int winningPlayerIndex = currentPlayerIndex;
        Card winningCard = leadCard;

        for (int i = 1; i < center.size(); i++) {
            Card currentCard = center.get(i);
            String currentSuit = currentCard.getSuit();
            String currentRank = currentCard.getRank();

            if (currentSuit.equals(leadSuit) && compareRanks(currentRank, leadRank) > 0) {
                leadRank = currentRank;
                winningPlayerIndex = (currentPlayerIndex + i) % NumPlayers;
                winningCard = currentCard;
            }
        }

        if(currentTrick == 1) {
            winningPlayerIndex = (winningPlayerIndex + 3) % NumPlayers;
        }
        
        System.out.println("*** Player " + (winningPlayerIndex + 1) + " wins Trick #" + currentTrick + " with "
                + winningCard + " ***");
        currentPlayerIndex = winningPlayerIndex;
        currentTrick++;
        center.clear();
    }

    private void calculateScore() {
        for (int i = 0; i < NumPlayers; i++) {
            int score = 0;
            for (Card card : players[i]) {
                score += card.getRankValue(card.getRank());
            }
            scores[i] = score;
        }
    }

    public void startRound() {
        currentTrick = 1;
        deck.clear();
        deck = new Deck();
        for (int i = 0; i < NumPlayers; i++) {
            players[i].clear();
        }
        center.clear();
        dealCard();
        firstPlayer();
        System.out.println("Starting a new round!");
    }

    public void resetCenter() {
        center.clear();
    }

    public boolean isGameEnd() {
        for (int i = 0; i < NumPlayers; i++) {
            if (scores[i] >= TargetScore) {
                System.out.println("Player" + (i + 1) + " wins the game!");
                return true;
            }
        }
        return false;
    }

    public void saveGame(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            deck.save(oos);
            oos.writeObject(center);
            oos.writeInt(currentPlayerIndex);
            oos.writeInt(currentTrick);
            oos.writeObject(players);
            oos.writeObject(scores);
            oos.writeObject(player);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Failed to save the game: " + e.getMessage());
        }
    }

    public void loadGame(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            deck.load(ois);
            center = (List<Card>) ois.readObject();
            currentPlayerIndex = ois.readInt();
            currentTrick = ois.readInt();
            players = (List<Card>[]) ois.readObject();
            scores = (int[]) ois.readObject();
            player = (Map<String, List<Card>>) ois.readObject();
            System.out.println("Game loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load the game: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        GoBoom game = new GoBoom();
        game.startGame();
    }
}
