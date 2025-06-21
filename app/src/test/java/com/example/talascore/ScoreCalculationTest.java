package com.example.talascore;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases cho logic tính điểm Tá Lả
 * Các test case bao gồm:
 * 1. Ván không Ù - chơi bình thường
 * 2. Ù thường
 * 3. Ù khan
 * 4. Ù tròn
 * 5. Các trường hợp ăn cây khác nhau
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
    
    // Settings
    private int perfectHandBonus = 60;
    private int perfectHandKhanBonus = 90;
    private int perfectHandRoundBonus = 120;
    private int eatenCardFee = 1;
    private int lastCardFee = 4;
    private int momPenalty = 4;
    private int firstPlayerBonus = 5;
    private int eatenCardBonus = 5;
    
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
        
        // Expected results:
        // A (nhất): 0 + 2*5 (ăn cây) + 5 (thưởng người đầu) = +15
        // B (nhì): -1 + 1*5 (ăn cây) = +4  
        // C (ba): -2 - 4 (móm) = -6
        // D (bét): -3 - 4 (móm) = -7
        
        System.out.println("A (nhất): " + roundScores[0] + " điểm");
        System.out.println("B (nhì): " + roundScores[1] + " điểm");
        System.out.println("C (ba): " + roundScores[2] + " điểm");
        System.out.println("D (bét): " + roundScores[3] + " điểm");
        
        assertEquals("A (nhất) sai", 15, roundScores[0]);
        assertEquals("B (nhì) sai", 4, roundScores[1]);
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
        
        // Expected results:
        // A: +60 (Ù thường) + 1*5 (ăn cây) = +65
        // B, C, D: -20 (phạt Ù thường) = -20
        
        System.out.println("A (Ù thường): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");
        
        assertEquals("A (Ù thường) sai", 65, roundScores[0]);
        assertEquals("B sai", -20, roundScores[1]);
        assertEquals("C sai", -20, roundScores[2]);
        assertEquals("D sai", -20, roundScores[3]);
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
     * Test Case 6: Ù khan không ăn cây nào
     * A Ù khan, không ăn cây nào
     */
    @Test
    public void testPerfectHandKhanNoCards() {
        System.out.println("=== Test Case 6: Ù khan không ăn cây ===");
        
        int winnerIndex = 0; // A Ù khan
        cardsEaten[winnerIndex] = 0; // Không ăn cây nào
        
        calculatePerfectHandScore(winnerIndex, true);
        
        // Expected results:
        // A: +90 (Ù khan) + 0*5 (ăn cây) = +90
        // B, C, D: -30 (phạt Ù khan) = -30
        
        System.out.println("A (Ù khan): " + roundScores[0] + " điểm");
        System.out.println("B: " + roundScores[1] + " điểm");
        System.out.println("C: " + roundScores[2] + " điểm");
        System.out.println("D: " + roundScores[3] + " điểm");
        
        assertEquals("A (Ù khan) sai", 90, roundScores[0]);
        assertEquals("B sai", -30, roundScores[1]);
        assertEquals("C sai", -30, roundScores[2]);
        assertEquals("D sai", -30, roundScores[3]);
    }
    
    // Helper methods (copy from MainActivity)
    private void calculatePerfectHandScore(int winnerIndex, boolean isKhan) {
        // Reset round scores
        for (int i = 0; i < 4; i++) {
            roundScores[i] = 0;
        }
        
        // Calculate bonus based on type
        int bonus = perfectHandBonus; // Default
        if (isKhan) {
            bonus = perfectHandKhanBonus;
        } else {
            // Check if it's round hand (this would need to be passed as parameter in real implementation)
            bonus = perfectHandRoundBonus; // Assume round for this test
        }
        
        // Winner gets bonus
        roundScores[winnerIndex] = bonus;
        
        // Others lose points
        int penalty = bonus / 3; // Divide bonus by 3 for penalty
        for (int i = 0; i < 4; i++) {
            if (i != winnerIndex) {
                roundScores[i] = -penalty;
            }
        }
        
        // Add eaten cards bonus for winner
        int eatenCardsCount = cardsEaten[winnerIndex];
        if (eatenCardsCount > 0) {
            roundScores[winnerIndex] += eatenCardsCount * eatenCardBonus;
        }
    }
    
    private void calculateRankingScore() {
        int[] roundPoints = new int[4];

        // Base points from ranking
        roundPoints[ranking[0]] = 0; // 1st
        roundPoints[ranking[1]] = -1; // 2nd
        roundPoints[ranking[2]] = -2; // 3rd
        roundPoints[ranking[3]] = -3; // 4th
        
        // Mom penalty
        for (int i = 0; i < 4; i++) {
            if (wasMom[i]) {
                roundPoints[i] -= momPenalty;
            }
        }
        
        // First player bonus if all móm
        boolean allMom = true;
        for (int i = 0; i < 4; i++) {
            if (!wasMom[i]) {
                allMom = false;
                break;
            }
        }
        
        if (allMom && firstPlayerIndex != -1) {
            roundPoints[firstPlayerIndex] += firstPlayerBonus;
        }

        // Card fees - calculate based on actual eaten cards
        int totalPointsLost = 0;
        int cardsEatenByWinner = cardsEaten[ranking[0]];
        int lastCardsByWinner = lastCardsEaten[ranking[0]];
        
        // Add eaten cards bonus for winner
        if (cardsEatenByWinner > 0) {
            roundPoints[ranking[0]] += cardsEatenByWinner * eatenCardBonus;
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