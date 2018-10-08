package core;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Evaluator {
	 public static void main(String[] args) {
		 Scanner r = new Scanner(System.in);
		 Evaluator eva = new Evaluator();
		 System.out.println("Current working directory: " + System.getProperty("user.dir"));
		 System.out.println("Please enter the path of the file you wish to play:");
		 String fn = r.next();
		 eva.readFile(fn);
		 r.close();
	 }
	
	public void readFile(String filepath) {
		System.out.println("Reading file...\n");
		try (BufferedReader read = new BufferedReader(new FileReader(filepath))) {
			String l;
			int c = 1;
			while ((l = read.readLine()) != null) {
				System.out.println("ACCEPTANCE TEST " + Integer.toString(c));
				System.out.println("CARDS: " + l);
				if (play(l) == true) {
					System.out.println("AIP Wins!\n");
				}
				else System.out.println("Dealer Wins!\n");
				c++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done reading, Goodbye!");
	}
	//Takes string of 10+ card values and determines winner between first 5 and remainder
	public boolean play(String cards) {
		//Find dividing line between first five cards and remainder
		int s = 0;
		int i = 0;
		for (int x = 0; x < cards.length(); x++) {
			if (cards.charAt(x) == ' ') s++;
			if (s == 5) {
				i = x; 
				break;
			}
		}
		//Dealer gets first five cards
		String dealerCards = cards.substring(0,i);
		System.out.println("Dealer's Hand: " + dealerCards);
		//AIP gets remainder: hand (first 5 cards) and deck
		String aipCards = cards.substring(i + 1,cards.length());
		
		int[] dHand = analyze((sort(convert(dealerCards))));
		int[] aHand = analyze(strategize(aipCards));
		
		for (int x = 0; x < dHand.length; x++) {
			if (aHand[x] > dHand[x]) return true;
			else if (aHand[x] < dHand[x]) return false;
		}
		return false;
	}
	
	//Determine if swaps need to be made, then return AIP hand as 10-digit array
		//In descending order of rank, then suit, with ranks listed first
	public int[] strategize(String cards) {
		//Split AIP hand from deck
		String[] cardArr= cards.split(" ");
		String aip = String.join(" ",Arrays.copyOfRange(cardArr,0,5));
		System.out.println("AIP's Hand: " + aip);
		String dk;
		if (cardArr.length > 5) dk = String.join(" ", Arrays.copyOfRange(cardArr, 5, cardArr.length));
		else dk = "";
		
		int[] iHand = convert(aip);
		int[] deck;
		if (dk.length() >1) deck = convert(dk);
		else deck = new int[0];
		iHand = sort(iHand);
		
		//STRATEGY-1:If hand is of a single suit, return hand as-is
		Boolean oneSuit = true;
		for (int x = 5; x < iHand.length - 1; x++) {
			if (iHand[x] != iHand[x+1]) {
				oneSuit = false;
				break;
			}
		}
		if (oneSuit == true) {
			return iHand;
		}
		
		//STRATEGY-2: If hand has tuple-value-sum > 11 (FH or 4ofaK), return hand as-is
		//determine tuple-value-sum
		int tvs = 0;
		int[] tuples = {0,0,0,0,0};
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y <5; y++) {
				if (iHand[x] == iHand[y]) tuples[x]++;	
			}
			tvs += tuples[x];
		}
		if (tvs > 11) return iHand;
		
		//STRATEGY-3: If hand is straight, return as-is
		Boolean seq5 = true;
		for (int x = 1; x < 5; x++) {
			if (iHand[0]-iHand[x] != x) {
				seq5 = false;
				break;
			}
		}
		if (seq5 == true) return iHand;
		
		//STRATEGY-4: If four cards share same suit, swap misfit card and return hand
		int[] suitSizes = {0,0,0,0};
		//Find number of cards in each suit
		for (int x = 0; x < 4; x++) {
			for (int y = 5; y < iHand.length; y++) {
				if (iHand[y] == x+1) suitSizes[x] +=1;
			}
		}
		//Check if four cards share same suit
		for (int x = 0; x < 4; x++) {
			if (suitSizes[x] == 4) {
				//Find index of non-matching card
				for (int y = 5; y < iHand.length; y++) {
					//Swap card
					if (iHand[y] != x+1) {
						printDiscard(iHand[y-5], iHand[y]);
						iHand[y -5] = deck[0];
						iHand[y] = deck[deck.length/2];
						printAdd(iHand[y-5], iHand[y]);
						break;
					}
				}
				//Return new hand, sorted
				return sort(iHand);
			}
		}
		
		//STRATEGY-5: If 7 < tuple-value-sum <13, hand is 2pair or triple
			//Swap lowest non-tuple card
		if (tvs > 7) {
			int r = 14;
			int i = -1;
			//Find index of lowest non-tuple card
			for (int x = 0; x < tuples.length; x++) {
				if ((tuples[x] == 1) && (iHand[x] < r)){
					r = iHand[x];
					i = x;
				}
			}
			//Swap card
			printDiscard(iHand[i], iHand[i+5]);
			iHand[i] = deck[0];
			iHand[i + 5] = deck[0 + (deck.length/2)];
			printAdd(iHand[i], iHand[i+5]);
			return sort(iHand);
		}
		
		//STRATEGY-6: If 1 card missing to form straight, swap misfit an return hand
		for (int x = 0; x < 2; x++) {
			Boolean[] seq = {false, false, false, false, false};
			seq[x] = true;
			int s = 1;
			for (int y = x + 1; y < x + 4; y++) {
				if (iHand[y] == iHand[x] - (y-x)) {
					seq[y] = true;
					s++;
				}
				else break;
			}
			if (s == 4) {
				for (int z = 0; z < 5; z++) {
					if (seq[z] == false) {
						printDiscard(iHand[z],iHand[z+5]);
						iHand[z] = deck[0];
						iHand[z + 5] = deck[deck.length/2];
						printAdd(iHand[z],iHand[z+5]);
						return sort(iHand);
					}
				}
			}
		}
		
		//STRATEGY-7: If 3 cards in same suit, swap non-matching cards;
		//See if there exists a suit of 3
		for (int x = 0; x < 4; x++) {
			if (suitSizes[x] == 3) {
				int rep = 0;
				for (int y = 5; y < iHand.length; y++) {
					if (iHand[y] != x + 1) {
						printDiscard(iHand[y-5],iHand[y]);
						iHand[y - 5] = deck[rep];
						iHand[y] = deck[rep + (deck.length/2)];
						printAdd(iHand[y-5],iHand[y]);
						rep++;
						if (rep > 1) break;
					}
				}
				return sort(iHand);
			}
		}
		//STRATEGY-8: If 3 cards in same sequence), swap non-matching cards
		for (int x = 0; x < 3; x++) {

			if ((iHand[x + 1] == iHand[x] -1) && (iHand[x + 2] == iHand[x] -2)) {
				int rep = 0;
				for(int y = 0; y < 5; y++) {
					if ((y < x) || (y > x + 2)) {
						printDiscard(iHand[y], iHand[y+5]);
						iHand[y] = deck[rep];
						iHand[y + 5] = deck[rep + (deck.length/2)];
						printAdd(iHand[y], iHand[y+5]);
						rep++;
						if (rep > 1) break;
					}
				}
				return sort(iHand);
			}
		}
		//STRATEGY-9: Replace 3 lowest cards with tuple value 1
		int rep = 0;
		for (int x = 4; x >= 0; x--) {
			if (tuples[x] == 1) {
				printDiscard(iHand[x],iHand[x+5]);
				iHand[x] = deck[rep];
				iHand[x + 5] = deck[rep+ (deck.length/2)];
				printAdd(iHand[x],iHand[x+5]);
				rep++;
				if (rep > 2) break;
			}
		}
		return sort(iHand);
	}
	
	//Returns card string as array of integers
	private int[] convert(String cards) {
		String[] sArr = cards.split(" ");
		int[] hand = new int[sArr.length*2];
		Arrays.toString(sArr);
		for (int x = 0; x < sArr.length; x++) {
			int r;
			if (sArr[x].charAt(1) == 'A') r = 1;
			else if (sArr[x].charAt(1) == 'J') r = 11;
			else if (sArr[x].charAt(1) == 'Q') r = 12; //Does not catch Q for some reason
			else if (sArr[x].charAt(1) == 'K') r = 13;
			else {
				r = Integer.parseInt(sArr[x].substring(1,sArr[x].length()));
			}
			hand[x] = r;
		}
		//Get card suits
		for (int x = 0; x < sArr.length; x++) {
			int s;
			switch (sArr[x].charAt(0)) {
				case 'H': s = 1; break;
				case 'D': s = 2; break;
				case 'C': s = 3; break;
				case 'S': s = 4; break;
				default: s = -1;
			}
			hand[x + (hand.length/2)] = s;
		}
		return hand;
	}
	//Sort cards by descending order of rank, then suit
	private int[] sort(int[] cards) {
		//Sort by rank
		int r;
		int s;
        for (int i = 1; i < (cards.length/2); i++) {
            for(int j = i ; j > 0 ; j--){
                if(cards[j] > cards[j-1]){
                    r = cards[j];
                    s = cards[j+(cards.length/2)];
                    cards[j] = cards[j-1];
                    cards[j+(cards.length/2)] = cards[j+(cards.length/2)-1];
                    cards[j-1] = r;
                    cards[j+(cards.length/2)-1] = s;
                }
            }
        }
        //Sort cards of matching rank by suit
        for (int i = 1 + (cards.length/2); i < cards.length; i++) {
            for(int j = i ; j > (cards.length/2); j--){
                if((cards[j - (cards.length/2)] == cards[j - (cards.length/2) -1]) &&
                		(cards[j] > cards[j-1])){
                    r = cards[j];
                    s = cards[j-(cards.length/2)];
                    cards[j] = cards[j-1];
                    cards[j-(cards.length/2)] = cards[j-(cards.length/2)-1];
                    cards[j-1] = r;
                    cards[j-(cards.length/2)-1] = s;
                }
            }
        }
        return cards;
	}
	
	//Determine value of hand based on card values
	public int[] analyze(int[] cards) {

		//Check if hand is flush-variant
		Boolean oneSuit = true;
		for (int x = 5; x < cards.length - 1; x++) {
			if (cards[x] != cards[x+1]) {
				oneSuit = false;
				break;
			}
		}
		if (oneSuit == true) {
			//SCORE-1: Evaluate Royal Flush
			if ((cards[0] == 13) && (cards[3] == 10) && (cards[4] == 1)){
				int[] score = {10,0};
				score[1] = cards[5];
				return score;
			}
			//SCORE-2: Evaluate Straight Flush
			Boolean seq5 = true;
			for (int x = 1; x < 5; x++) {
				if (cards[0]-cards[x] != x) {
					seq5 = false;
					break;
				}
			}
			if (seq5 == true) {
				int[] score = {9,0,0};
				score[1] = cards[0];
				score[2] = cards[5];
				return score;
			}
			//SCORE-5: Evaluate Flush
			int[] score = {6,0,0,0,0,0,0};
			for (int x = 1; x < 7; x++) {
				score[x] = cards[x-1];
			}
			return score;
		}
		//Find tuple-value-sum
		int tvs = 0;
		int[] tuples = {0,0,0,0,0};
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y <5; y++) {
				if (cards[x] == cards[y]) tuples[x]++;	
			}
			tvs += tuples[x];
		}
		//Smooth tuple table for most valuable data only
		for (int x = 4; x > 0; x--) {
			if ((tuples[x-1] == tuples[x]) &&
					(cards[x-1] == cards[x])) {
				tuples[x] = 0;
			}
		}
		
		//SCORE-3: Evaluate Four-of-a-Kind
		if (tvs == 17) {
			int[] score = {8,0};
			for(int x = 0; x < 5; x++) {
				if (tuples[x] == 4) {
					score[1] = cards[x];
					return score;
				}
			}
		}
		//SCORE-4: Evaluate Full House
		else if (tvs == 13) {
			int[] score = {7,0};
			for(int x = 0; x < 5; x++) {
				if (tuples[x] == 3) {
					score[1] = cards[x];
					return score;
				}
			}
		}
		//SCORE-7: Evaluate Three-of-a-Kind
		else if (tvs == 11) {
			int[] score = {4,0};
			for(int x = 0; x < 5; x++) {
				if (tuples[x] == 3) {
					score[1] = cards[x];
					return score;
				}
			}
		}
		//SCORE-8: Evaluate Two Pairs
		else if (tvs == 9) {
			int[] score = {3,0,0};
			for(int x = 0; x < 5; x++) {
				if (tuples[x] == 2) {
					score[1] = cards[x];
					score[2] = cards[x+5];
					return score;
				}
			}
		}
		//SCORE-9: Evaluate Pair
		else if (tvs == 7) {
			int[] score = {2,0,0};
			for(int x = 0; x < 5; x++) {
				if (tuples[x] == 2) {
					score[1] = cards[x];
					score[2] = cards[x+5];
					return score;
				}
			}
		}
		//No Tuples
		//SCORE-6 Evaluate Straight
		Boolean seq5 = true;
		for (int x = 1; x < 5; x++) {
			if (cards[0]-cards[x] != x) {
				seq5 = false;
				break;
			}
		}
		int[] score = {0,0,0};
		
		if (seq5 == true) score[0] = 5;
		
		//SCORE-10: Evaluate High Card
		else score[0] = 1;
		
		score[1] = cards[0];
		score[2] = cards[5];
		return score;
	}
	
	private void printDiscard(int rank, int suit) {
		String r;
		String s;
		switch (rank) {
			case 1: r = "A"; break;
			case 11: r = "J"; break;
			case 12: r = "Q"; break;
			case 13: r = "K"; break;
			default: r = Integer.toString(rank); break;
		}
		switch (suit) {
			case 1: s = "H"; break;
			case 2: s = "D"; break;
			case 3: s = "C"; break;
			default: s = "S"; break;
		}
		System.out.println("DISCARDING: " + s + r);
	}
	private void printAdd(int rank, int suit) {
		String r;
		String s;
		switch (rank) {
			case 1: r = "A"; break;
			case 11: r = "J"; break;
			case 12: r = "Q"; break;
			case 13: r = "K"; break;
			default: r = Integer.toString(rank); break;
		}
		switch (suit) {
			case 1: s = "H"; break;
			case 2: s = "D"; break;
			case 3: s = "C"; break;
			default: s = "S"; break;
		}
		System.out.println("ADDING: " + s + r);
	}
}