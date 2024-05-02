import java.io.Serializable;

public class Card implements Serializable{
    
    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getRankValue(String rank) {
        switch (rank) {
            case "2"  : return 2;
            case "3"  : return 3;
            case "4"  : return 4;
            case "5"  : return 5;
            case "6"  : return 6;
            case "7"  : return 7;
            case "8"  : return 8;
            case "9"  : return 9;
            case "10" : return 10;
            case "J"  : return 10;
            case "Q"  : return 10;
            case "K"  : return 10;
            case "A"  : return 1;
            default : return 0;
        }
    }
        
    public String toString() {
        return suit + rank;
    }

    public static Card fromString(String cardString) {
        String suit = cardString.substring(0,1);
        String rank = cardString.substring(1);
        return new Card(suit, rank);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Card otherCard = (Card) obj;
        return rank.equals(otherCard.rank) && suit.equals(otherCard.suit);
    }
}