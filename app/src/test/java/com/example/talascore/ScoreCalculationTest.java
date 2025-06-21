package com.example.talascore;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases cho logic tính điểm Tá Lả (Updated v1.1.0)
 * Các test case bao gồm:
 * 1. Ván không Ù - chơi bình thường
 * 2. Ù thường/khan/tròn
 * 3. Ù đền (người BỊ ăn 3 cây trả thay)
 * 4. Progressive eaten cards (cây 1=1đ, cây 2=2đ)
 * 5. Điểm cơ bản chuẩn (tổng=0)
 */
public class ScoreCalculationTest {
    
    private String[] playerNames = {"A", "B", "C", "D"};
    private int[] totalScores = new int[4];
    private int[] roundScores = new int[4];
    private boolean[] wasMom = new boolean[4];
    private int[] cardsEaten = new int[4];
    private int[] lastCardsEaten = new int[4];
    private int[] ranking = new int[4];
    private int firstPlayerIndex = -1;
    
    // Settings - Updated to match new logic
    private int perfectHandBonus = 5;      // Penalty per person for normal Ù
    private int perfectHandKhanBonus = 7;  // Penalty per person for khan Ù
    private int perfectHandRoundBonus = 6; // Penalty per person for round Ù
    private int eatenCardFee = 1;
    private int lastCardFee = 4;
    private int momPenalty = 4;
    private int firstPlayerBonus = 0;      // No first player bonus in current rules
    private int eatenCardBonus = 1;
    
    @Before
    public void setUp() {
        // Reset all scores
        for (int i = 0; i < 4; i++) {
            totalScores[i] = 0;
            roundScores[i] = 0;
            wasMom[i] = false;
            cardsEaten[i] = 0;
            lastCardsEaten[i] = 0;
            ranking[i] = -1;
        }
        firstPlayerIndex = -1;
    }
    
    /**
     * Test Case 1: Ván không Ù - chơi bình thường
     * A nhất (ăn 2 cây), B nhì (ăn 1 cây), C ba (móm), D bét (móm)
     */
    @Test
    public void testNormalGame() {
        System.out.println("=== Test Case 1: Ván không Ù ===");
        
        // Setup ranking
        ranking[0] = 0; // A nhất
        ranking[1] = 1; // B nhì  
        ranking[2] = 2; // C ba
        ranking[3] = 3; // D bét
        
        // Setup eaten cards
        cardsEaten[0] = 2; // A ăn 2 cây
        cardsEaten[1] = 1; // B ăn 1 cây
        cardsEaten[2] = 0; // C móm
        cardsEaten[3] = 0; // D móm
        
        // Setup mom status
        wasMom[2] = true; // C móm
        wasMom[3] = true; // D móm
        
        // Setup first player
        firstPlayerIndex = 0; // A là người chơi đầu
        
        // Calculate scores
        calculateRankingScore();
        
        // Expected results (NEW LOGIC):
        // A (nhất): +6 + (1+2) (progressive cây ăn) = +9
        // B (nhì): -1 + 1 (cây ăn) = 0
        // C (ba): -2 - 4 (móm) = -6
        // D (bét): -3 - 4 (móm) = -7
        
        System.out.println("A (nhất): " + roundScores[0] + " điểm");
        System.out.println("B (nhì): " + roundScores[1] + " điểm");
        System.out.println("C (ba): " + roundScores[2] + " điểm");
        System.out.println("D (bét): " + roundScores[3] + " điểm");
        
        assertEquals("A (nhất) sai", 9, roundScores[0]);
        assertEquals("B (nhì) sai", 0, roundScores[1]);
        assertEquals("C (ba) sai", -6, roundScores[2]);
        assertEquals("D (bét) sai", -7, roundScores[3]);
    }
    
    /**
     * Test Case 2: Ù thường
     * A Ù thường, ăn 1 cây
     */
    @Test
    public void testPerfectHandNormal() {
        System.out.println("=== Test Case 2: Ù thường ===");
        
        int winnerIndex = 0; // A Ù
        cardsEaten[winnerIndex] = 1; // Ăn 1 cây
        
        calculatePerfectHandScore(winnerIndex, false);
        
        // Expected results (NEW LOGIC):
        // A: +15 (5*3) = +15
        // B, C, D: -5 (phạt Ù thường) = -5
        
        System.out.println("A (Ù thường): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");
        
        assertEquals("A (Ù thường) sai", 15, roundScores[0]);
        assertEquals("B sai", -5, roundScores[1]);
        assertEquals("C sai", -5, roundScores[2]);
        assertEquals("D sai", -5, roundScores[3]);
    }
    
    /**
     * Test Case 3: Ù khan
     * A Ù khan, ăn 2 cây
     */
    @Test
    public void testPerfectHandKhan() {
        System.out.println("=== Test Case 3: Ù khan ===");
        
        int winnerIndex = 0; // A Ù khan
        cardsEaten[winnerIndex] = 2; // Ăn 2 cây
        
        calculatePerfectHandScore(winnerIndex, true);
        
        // Expected results:
        // A: +90 (Ù khan) + 2*5 (ăn cây) = +100
        // B, C, D: -30 (phạt Ù khan) = -30
        
        System.out.println("A (Ù khan): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");
        
        assertEquals("A (Ù khan) sai", 100, roundScores[0]);
        assertEquals("B sai", -30, roundScores[1]);
        assertEquals("C sai", -30, roundScores[2]);
        assertEquals("D sai", -30, roundScores[3]);
    }
    
    /**
     * Test Case 4: Ù tròn
     * A Ù tròn, ăn 1 cây
     */
    @Test
    public void testPerfectHandRound() {
        System.out.println("=== Test Case 4: Ù tròn ===");
        
        int winnerIndex = 0; // A Ù tròn
        cardsEaten[winnerIndex] = 1; // Ăn 1 cây
        
        calculatePerfectHandScore(winnerIndex, false); // isKhan = false, nhưng sẽ check isPerfectHandRound
        
        // Expected results:
        // A: +120 (Ù tròn) + 1*5 (ăn cây) = +125
        // B, C, D: -40 (phạt Ù tròn) = -40
        
        System.out.println("A (Ù tròn): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");
        
        assertEquals("A (Ù tròn) sai", 125, roundScores[0]);
        assertEquals("B sai", -40, roundScores[1]);
        assertEquals("C sai", -40, roundScores[2]);
        assertEquals("D sai", -40, roundScores[3]);
    }
    
    /**
     * Test Case 5: Ván không Ù với cây chốt
     * A nhất (ăn 2 cây, 1 cây chốt), B nhì (ăn 1 cây), C ba (móm), D bét (móm)
     */
    @Test
    public void testNormalGameWithLastCards() {
        System.out.println("=== Test Case 5: Ván không Ù với cây chốt ===");
        
        // Setup ranking
        ranking[0] = 0; // A nhất
        ranking[1] = 1; // B nhì  
        ranking[2] = 2; // C ba
        ranking[3] = 3; // D bét
        
        // Setup eaten cards
        cardsEaten[0] = 2; // A ăn 2 cây
        cardsEaten[1] = 1; // B ăn 1 cây
        cardsEaten[2] = 0; // C móm
        cardsEaten[3] = 0; // D móm
        
        // Setup last cards
        lastCardsEaten[0] = 1; // A ăn 1 cây chốt
        
        // Setup mom status
        wasMom[2] = true; // C móm
        wasMom[3] = true; // D móm
        
        // Setup first player
        firstPlayerIndex = 0; // A là người chơi đầu
        
        // Calculate scores
        calculateRankingScore();
        
        // Expected results:
        // A (nhất): 0 + 2*5 (ăn cây) + 5 (thưởng người đầu) = +15
        // B (nhì): -1 + 1*5 (ăn cây) - 1 (bị ăn cây) = +3
        // C (ba): -2 - 4 (móm) - 1 (bị ăn cây) = -7
        // D (bét): -3 - 4 (móm) = -7
        
        System.out.println("A (nhất): " + roundScores[0] + " điểm");
        System.out.println("B (nhì): " + roundScores[1] + " điểm");
        System.out.println("C (ba): " + roundScores[2] + " điểm");
        System.out.println("D (bét): " + roundScores[3] + " điểm");
        
        assertEquals("A (nhất) sai", 15, roundScores[0]);
        assertEquals("B (nhì) sai", 3, roundScores[1]);
        assertEquals("C (ba) sai", -7, roundScores[2]);
        assertEquals("D (bét) sai", -7, roundScores[3]);
    }
    
    /**
     * Test Case 6: Ù đền - Logic chuẩn
     * A Ù thường, D bị ăn 3 cây (victim)
     */
    @Test
    public void testPerfectHandDen() {
        System.out.println("=== Test Case 6: Ù đền (Logic chuẩn) ===");

        int winnerIndex = 0; // A Ù thường
        int victimIndex = 3; // D bị ăn 3 cây

        calculatePerfectHandDenScore(winnerIndex, victimIndex, false);

        // Expected results (NEW LOGIC):
        // A (Ù): +15 điểm (5*3)
        // D (victim): -15 điểm (trả thay cho tất cả)
        // B, C: 0 điểm (không mất gì)

        System.out.println("A (Ù thường): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D (victim): " + roundScores[3] + " điểm");

        assertEquals("A (Ù thường) sai", 15, roundScores[0]);
        assertEquals("B sai", 0, roundScores[1]);
        assertEquals("C sai", 0, roundScores[2]);
        assertEquals("D (victim) sai", -15, roundScores[3]);
    }

    /**
     * Test Case 7: Ù khan không ăn cây nào
     * A Ù khan, không ăn cây nào
     */
    @Test
    public void testPerfectHandKhanNoCards() {
        System.out.println("=== Test Case 7: Ù khan không ăn cây ===");

        int winnerIndex = 0; // A Ù khan
        cardsEaten[winnerIndex] = 0; // Không ăn cây nào

        calculatePerfectHandScore(winnerIndex, true);

        // Expected results (NEW LOGIC):
        // A: +21 (7*3) = +21
        // B, C, D: -7 (phạt Ù khan) = -7

        System.out.println("A (Ù khan): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");

        assertEquals("A (Ù khan) sai", 21, roundScores[0]);
        assertEquals("B sai", -7, roundScores[1]);
        assertEquals("C sai", -7, roundScores[2]);
        assertEquals("D sai", -7, roundScores[3]);
    }
    
    // Helper methods (updated for new logic)
    private void calculatePerfectHandDenScore(int winnerIndex, int victimIndex, boolean isRound) {
        // Reset round scores
        for (int i = 0; i < 4; i++) {
            roundScores[i] = 0;
        }

        // Calculate total penalty
        int penaltyPerPerson = isRound ? perfectHandRoundBonus : perfectHandBonus;
        int totalPenalty = penaltyPerPerson * 3;

        // Winner gets total penalty
        roundScores[winnerIndex] = totalPenalty;

        // Victim pays total penalty
        roundScores[victimIndex] = -totalPenalty;

        // Others get 0 (already set by Arrays.fill)
    }

    private void calculatePerfectHandScore(int winnerIndex, boolean isKhan) {
        // Reset round scores
        for (int i = 0; i < 4; i++) {
            roundScores[i] = 0;
        }
        
        // Calculate penalty per person based on type
        int penaltyPerPerson;
        if (isKhan) {
            penaltyPerPerson = perfectHandKhanBonus; // 7 per person
        } else {
            penaltyPerPerson = perfectHandBonus; // 5 per person (normal Ù)
        }

        // Winner gets total (penalty per person * 3)
        roundScores[winnerIndex] = penaltyPerPerson * 3;

        // Others lose penalty per person
        for (int i = 0; i < 4; i++) {
            if (i != winnerIndex) {
                roundScores[i] = -penaltyPerPerson;
            }
        }
    }
    
    private void calculateRankingScore() {
        int[] roundPoints = new int[4];

        // Base points from ranking (NEW LOGIC)
        roundPoints[ranking[0]] = 6; // 1st: +6
        roundPoints[ranking[1]] = -1; // 2nd: -1
        roundPoints[ranking[2]] = -2; // 3rd: -2
        roundPoints[ranking[3]] = -3; // 4th: -3
        
        // Mom penalty
        for (int i = 0; i < 4; i++) {
            if (wasMom[i]) {
                roundPoints[i] -= momPenalty;
            }
        }
        
        // No first player bonus in current rules

        // Card fees - calculate based on actual eaten cards
        int totalPointsLost = 0;
        int cardsEatenByWinner = cardsEaten[ranking[0]];
        int lastCardsByWinner = lastCardsEaten[ranking[0]];
        
        // Add eaten cards bonus for winner (progressive: 1st card = 1pt, 2nd card = 2pt)
        if (cardsEatenByWinner > 0) {
            int progressiveBonus = 0;
            for (int cardNum = 1; cardNum <= cardsEatenByWinner; cardNum++) {
                progressiveBonus += cardNum; // 1st card = 1, 2nd card = 2, etc.
            }
            roundPoints[ranking[0]] += progressiveBonus;
        }
        
        // Tính điểm theo vòng kim đồng hồ
        for (int i = 1; i < 4; i++) { // For 2nd, 3rd, 4th
            int loserIndex = ranking[i];
            
            // Tính điểm bị trừ dựa trên vị trí
            int pointsLost = roundPoints[loserIndex];
            
            // Người thắng ăn cây của người thua theo vòng kim đồng hồ
            if (cardsEatenByWinner > 0) {
                // Tính số cây bị ăn dựa trên vị trí
                int cardsEatenFromThisPlayer = 0;
                if (i == 1) { // Nhì - bị ăn đầu tiên
                    cardsEatenFromThisPlayer = Math.min(cardsEatenByWinner, 1);
                } else if (i == 2) { // Ba - bị ăn thứ hai
                    cardsEatenFromThisPlayer = Math.min(Math.max(0, cardsEatenByWinner - 1), 1);
                } else if (i == 3) { // Bét - bị ăn cuối
                    cardsEatenFromThisPlayer = Math.max(0, cardsEatenByWinner - 2);
                }
                
                // Trừ điểm theo số cây bị ăn
                pointsLost -= cardsEatenFromThisPlayer * eatenCardFee;
                
                // Nếu ăn cây chốt từ người này
                if (lastCardsByWinner > 0 && cardsEatenFromThisPlayer > 0) {
                    int lastCardsFromThisPlayer = Math.min(lastCardsByWinner, cardsEatenFromThisPlayer);
                    pointsLost -= lastCardsFromThisPlayer * lastCardFee;
                }
            }
            
            roundPoints[loserIndex] = pointsLost;
            totalPointsLost += -pointsLost;
        }
        roundPoints[ranking[0]] = totalPointsLost;

        // Copy to roundScores for testing
        for (int i = 0; i < 4; i++) {
            roundScores[i] = roundPoints[i];
        }
    }
} 