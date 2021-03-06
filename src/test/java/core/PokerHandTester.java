package core;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PokerHandTester extends TestCase{
	/*Note: Strategize method outputs final hand as 10-digit array
		See logic.txt for hand-to-numeric conversion.
	*/

	/*STRATEGY-1: Strategize using Single-Suit Hand
		Input (Shuffled):
			AIP Hand: {D6, D7, D8, D9, D10}
		Action: No swap
		Expected Output: 10-digit array in descending order of rank
	 */
	public void test1Suit() {
		Evaluator eva = new Evaluator();
		int[] val = {10,9,8,7,6,2,2,2,2,2}; //Straight Flush, 10 Highest, Diamond
		Assert.assertArrayEquals(val, eva.strategize("D6 D8 D7 D10 D9"));
	}
	
	/*STRATEGY-2: Strategize using hand with Tuple-Value-Sum > 11 (see Logic.txt)
		Input (Shuffled):
			AIP Hand: {S7,C7,H7,S3,D3}
		Action: No swap
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void testTVSMore11() {
		Evaluator eva = new Evaluator();
		int[] val = {7,7,7,3,3,4,3,1,4,2}; //Full House, 7 Highest
		Assert.assertArrayEquals(val, eva.strategize("S3 C7 H7 S7 D3"));
	}
	
	/*STRATEGY-3: Strategize using Straight
		Input (Shuffled):
			AIP Hand: {H6, H5, C4, D3, S2}
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void testSeq5NotFlush() {
		Evaluator eva = new Evaluator();
		int[] val = {6,5,4,3,2,1,1,3,2,4}; //Straight, 6 Highest, Hearts
		Assert.assertArrayEquals(val, eva.strategize("D3 H5 S2 C4 H6"));
	}
	
	/*STRATEGY-4: Strategize using hand with 4 cards of same suit
		Input (Shuffled):
			AIP Hand: {C8, C6, S5, C4, C2}
			Deck: {C7}
		Action: Swap non-matching card (S5) for C7
		Expected Output: 10-digit array in descending order of rank
	 */
	public void test4InSuit() {
		Evaluator eva = new Evaluator();
		int[] val = {8,7,6,4,2,3,3,3,3,3}; //Straight Flush, 7 of Clubs
		Assert.assertArrayEquals(val, eva.strategize("S5 C2 C4 C8 C6 C7"));
	}

	/*STRATEGY-5: Strategize using near-Full House (7 < Tuple Sum Value < 13)
		Input (Shuffled):
			AIP Hand: {S9, S7, C7, H7, H8}
			Deck: {SQ}
		Action: Swap lowest ranked card with tuple value of 1 (H8) for SQ
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void testAlmostFH() {
		Evaluator eva = new Evaluator();
		int[] val = {12,9,7,7,7,4,4,4,3,1}; //Three of a Kind, 7 Highest
		Assert.assertArrayEquals(val, eva.strategize("S7 H7 S9 C7 H8 SQ D10"));
	}
	
	/*STRATEGY-6: Strategize with near-Straight (four values match ascending sequence)
		Input (Shuffled):
			AIP Hand: {DQ, HJ, H10, C9, S6}
			Deck: {CK}
		Action: Swap non-sequential card (S6) for CK
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void testAlmostST() {
		Evaluator eva = new Evaluator();
		int[] val = {13,12,11,10,9,3,2,1,1,3}; //Straight, King Highest, Clubs
		Assert.assertArrayEquals(val, eva.strategize("C9 H10 DQ S6 HJ CK"));
	}
	
	/*STRATEGY-7: Strategize using hand with 3 cards of same suit
		Input (Shuffled):
			AIP Hand: {DK, DQ, DJ, C7, H3}
			Deck: {D7, D4}
		Action: Swap two non-matching cards (C7, H3) for (D7, D4)
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void test3Suit() {
		Evaluator eva = new Evaluator();
		int[] val = {13,12,11,7,4,2,2,2,2,2}; //Flush, K Q J 7 4, Diamond
		Assert.assertArrayEquals(val, eva.strategize("H3 DQ DJ C7 DK D7 D4"));
	}
	
	/*STRATEGY-8: Strategize using hand with 3-card sequence
		Input (Shuffled):
			AIP Hand: {SJ, C9, C10, S6, HA}
			Deck: {HK, DQ}
		Action: Swap non-sequential cards (S6, HA) with (HK, DQ)
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void test3Seq() {
		Evaluator eva = new Evaluator();
		int[] val = {13,12,11,10,9,1,2,4,3,3}; //Straight, King of Hearts
		Assert.assertArrayEquals(val, eva.strategize("C9 C10 HA SJ S6 HK DQ"));
	}
	/*STRATEGY-9: Strategize using bad hand (Pair or worse)
		Input (Shuffled):
			AIP Hand: {D2, H2, CQ, CJ, H6}
			Deck: {S5, D5, H5}
		Action: Swap 3 lowest cards with tuple value 1 (CQ,CJ, H6) for S5, D5, H5
		Expected Output: 10-digit array in descending order of rank, then suit
	 */
	public void testBadHand() {
		Evaluator eva = new Evaluator();
		int[] val = {5,5,5,2,2,4,2,1,2,1}; //Full House, 5 Highest
		Assert.assertArrayEquals(val, eva.strategize("H6 H2 CQ CJ D2 D5 H5 S5"));
	}
	
	//NOTE: Hand values contained in logic.txt
	
	/*SCORE-1: Analyze Royal Flush
		Input (as 10-digit array):
			Hand: {DK,DQ,DJ,D10,DA)
		Expected Output: 2-digit array indicating hand value
	 */
	public void testRoyalFlush() {
		Evaluator eva = new Evaluator();
		int[] input = {13,12,11,10,1,2,2,2,2,2};
		int[] val = {10,2}; //Royal Flush, Diamond
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-2: Analyze Straight Flush
		Input (as 10-digit array):
			Hand: {D10, D9, D8, D7, D16}
		Expected Output: 3-digit array indicating hand value
	 */
	public void testStraightFlush() {
		Evaluator eva = new Evaluator();
		int[] input = {10,9,8,7,6,3,3,3,3,3};
		int[] val = {9,10,3}; //Straight Flush, 10 of Clubs
		Assert.assertArrayEquals(val, eva.analyze(input));
	}

	/*SCORE-3: Analyze Four of a Kind
		Input (as 10-digit array):
			Hand: {C5,S4,C4,D4,H4}
		Expected Output: 2-digit array indicating hand value
	 */
	public void testFourOfAKind() {
		Evaluator eva = new Evaluator();
		int[] input = {5,4,4,4,4,3,4,3,2,1};
		int[] val = {8,4}; //Four of a Kind, 4
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-4: Analyze Full House
		Input (as 10-digit array):
			Hand: {C5,S5,H5,D4,H4}
		Expected Output: 2-digit array indicating hand value
	 */
	public void testFullHouse() {
		Evaluator eva = new Evaluator();
		int[] input = {5,5,5,4,4,3,4,1,2,1};
		int[] val = {7,5}; //Full House, 5
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-5: Analyze Flush
		Input (as 10-digit array):
			Hand: {SJ,S9,S8,S4,S2}
		Expected Output: 7-digit array indicating hand value
	 */
	public void testFlush() {
		Evaluator eva = new Evaluator();
		int[] input = {11,9,8,4,2,4,4,4,4,4};
		int[] val = {6,11,9,8,4,2,4}; //Flush, 11 9 8 4 2, Spades
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-6: Analyze Straight
		Input (as 10-digit array):
			Hand: {C9,D8,S7,D6,H5}
		Expected Output: 3-digit array indicating hand value
	 */
	public void testStraight() {
		Evaluator eva = new Evaluator();
		int[] input = {9,8,7,6,5,3,2,4,2,1};
		int[] val = {5,9,3}; //Straight, 9 of Clubs
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-8: Analyze Three of a Kind
		Input (as 10-digit array):
			Hand: {CK,S7,C7,D7,D3}
		Expected Output: 2-digit array indicating hand value
	 */
	public void testThreeOfAKind() {
		Evaluator eva = new Evaluator();
		int[] input = {13,7,7,7,3,3,4,3,2,2};
		int[] val = {4,7}; //Three of a Kind, 7
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-9: Analyze Two Pairs
		Input (as 10-digit array):
			Hand: {CQ,S4,C4,C3,D3}
		Expected Output: 4-digit array indicating hand value
	 */
	public void testTwoPairs() {
		Evaluator eva = new Evaluator();
		int[] input = {12,4,4,3,3,3,4,3,3,2};
		int[] val = {3,4,4}; //Two pairs, 4, Spades
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-10: Analyze Pair
		Input (as 10-digit array):
			Hand: {C8,H7,S4,DA,HA}
		Expected Output: 3-digit array indicating hand value
	 */
	public void testPair() {
		Evaluator eva = new Evaluator();
		int[] input = {8,7,4,1,1,3,1,4,2,1};
		int[] val = {2,1,2}; //Pair, Ace of Diamonds
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	/*SCORE-11: Analyze Highest Card
		Input (as 10-digit array):
			Hand: {CJ,S8,H4,D3,S2}
		Expected Output: 3-digit array indicating hand value
	 */
	public void testHighCard() {
		Evaluator eva = new Evaluator();
		int[] input = {11,8,4,3,2,3,4,1,2,4};
		int[] val = {1,11,3}; //Highest Card, Jack of Clubs
		Assert.assertArrayEquals(val, eva.analyze(input));
	}
	
	/*SCORE-10: Take 2 hands, perform AIP swap for 2 different hand types, recognize AIP Win
		Input (Shuffled):
			Dealer Hand: {S4, C4, C3, DQ, S6} 	Pair of 4s
			AIP Hand: {SA, HA, CA, HK, H8}
				Deck: {DK}					After swap, AIP has Full House, Ace of Spades
		Expected Output: AIP Win
	*/
	public void testWin() {
		Evaluator eva = new Evaluator();
		assertEquals(true, eva.play("C4 S6 C3 S4 DQ HA SA H8 HK CA DK"));
	}
	/*SCORE-11: Take 2 hands, perform AIP swap for 2 of same hand, recognize AIP Loss
		Input (Shuffled):
			Dealer Hand: {DK, DQ, DJ, D10, D9} 	Straight Flush, King of Diamonds
			AIP Hand: {H6, H4, H3, H2, C7}
				Deck: {H5}					After swap, AIP has Straight Flush, 6 of Hearts
		Expected Output: AIP Loss
	*/
	public void testLoss() {
		Evaluator eva = new Evaluator();
		assertEquals(false, eva.play("DQ D10 D9 DJ DK H3 H2 H4 H6 C7 H5"));
	}
}
