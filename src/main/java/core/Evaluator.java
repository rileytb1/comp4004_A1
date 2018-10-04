package core;

import java.util.Arrays;

public class Evaluator {
	//Takes string of 10+ card values and determines winner between first 5 and remainder
	public boolean play(String cards) {
		//Find dividing line between first five cards and remainder
		int s = 0;
		for (int x = 0; x <cards.length(); x++) {
			if (cards.charAt(x) == ' ') s++;
			if (s == 5) break;
		}
		//Dealer gets first five cards
		String dealerCards = cards.substring(0,s);
		//AIH gets remainder: hand (first 5 cards) and deck
		String aihCards = cards.substring(s,cards.length() + 1);
		
		int[] dHand = analyze((sort(convert(dealerCards))));
		int[] aHand = analyze(strategize(aihCards));
		
		for (int x = 0; x < dHand.length; x++) {
			if (aHand[x] > dHand[x]) return true;
			else if (aHand[x] < dHand[x]) return false;
		}
		return false;
	}
	
	//Determine if swaps need to be made, then return AIH hand as 10-digit array
		//In descending order of rank, then suit, with ranks listed first
	public int[] strategize(String cards) {
		//Split AIH hand from deck
		String[] cardArr= cards.split(" ");
		String aih = String.join(" ",Arrays.copyOfRange(cardArr,0,5));
		String dk;
		if (cardArr.length > 5) dk = String.join(" ", Arrays.copyOfRange(cardArr, 5, cardArr.length));
		else dk = "";
		
		int[] iHand = convert(aih);
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
		
		//STRATEGY-3: If difference between highest and lowest card is 4, return hand as-is
		if (iHand[0] - iHand[4] == 4) return iHand;
		
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
						//Swap rank
						iHand[y -5] = deck[0];
						iHand[y] = deck[deck.length/2];
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
			iHand[i] = deck[0];
			iHand[i + 5] = deck[0 + (deck.length/2)];
			return sort(iHand);
		}
		
		//STRATEGY-6: If 1 card missing to form straight, swap misfit an return hand
		if (iHand[0] - iHand[3] < 5) {
			iHand[4] = deck[0];
			iHand[9] = deck[deck.length/2];
			return sort(iHand);
		}
		else if (iHand[1] - iHand[4] < 5) {
			iHand[0] = deck[0];
			iHand[5] = deck[deck.length/2];
			return sort(iHand);
		}
		
		//STRATEGY-7: If 3 cards in same suit, swap non-matching cards;
		//See if there exists a suit of 3
		for (int x = 0; x < 4; x++) {
			if (suitSizes[x] == 3) {
				int rep = 0;
				for (int y = 5; y < iHand.length; y++) {
					if (iHand[y] != x + 1) {
						iHand[y - 5] = deck[rep];
						iHand[y] = deck[rep + (deck.length/2)];
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
						iHand[y] = deck[rep];
						iHand[y + 5] = deck[rep + (deck.length/2)];
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
				iHand[x] = deck[rep];
				iHand[x + 5] = deck[rep+ (deck.length/2)];
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
		
	}
}
