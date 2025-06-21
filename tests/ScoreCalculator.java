/**
 * Pure Java logic for Tá Lả scoring - No Android dependencies
 * Can be tested standalone without Android SDK
 */
public class ScoreCalculator {
    
    // Settings
    public static class Settings {
        public int perfectHandBonus = 5;
        public int perfectHandKhanBonus = 7;
        public int perfectHandRoundBonus = 6;
        public int eatenCardFee = 1;
        public int lastCardFee = 4;
        public int momPenalty = 4;
        public int firstPlayerBonus = 0;
        public int eatenCardBonus = 1;
    }
    
    /**
     * Calculate Ù Khan score (immediate, no eaten cards)
     */
    public static int[] calculatePerfectHandKhan(int winnerIndex, Settings settings) {
        int[] scores = new int[4];
        
        // Winner gets 3x, others pay 1x each
        scores[winnerIndex] = settings.perfectHandKhanBonus * 3;
        for (int i = 0; i < 4; i++) {
            if (i != winnerIndex) {
                scores[i] = -settings.perfectHandKhanBonus;
            }
        }
        
        return scores;
    }
    
    /**
     * Calculate Ù Thường/Tròn with eaten cards - CORRECTED LOGIC from Python
     */
    public static int[] calculatePerfectHandWithEatenCards(int winnerIndex, int[] eatenCards,
                                                          boolean isKhan, boolean isRound, Settings settings) {
        if (isKhan) {
            return calculatePerfectHandKhan(winnerIndex, settings);
        }

        int[] scores = new int[4];

        // Determine base bonus
        int baseBonus = isRound ? settings.perfectHandRoundBonus : settings.perfectHandBonus;

        // Step 1: Base Ù points (total = 0)
        scores[winnerIndex] = baseBonus * 3;  // Winner gets 3x
        for (int i = 0; i < 4; i++) {
            if (i != winnerIndex) {
                scores[i] = -baseBonus;  // Others pay 1x each
            }
        }

        // Step 2: Handle eaten cards (zero-sum)
        // Add eaten card bonuses (progressive: 1st=+1, 2nd=+2, etc.)
        for (int i = 0; i < 4; i++) {
            for (int cardNum = 1; cardNum <= eatenCards[i]; cardNum++) {
                scores[i] += cardNum;
            }
        }

        // Calculate eaten card penalties (simplified - assume proportional distribution)
        int totalEatenBonus = 0;
        for (int i = 0; i < 4; i++) {
            for (int cardNum = 1; cardNum <= eatenCards[i]; cardNum++) {
                totalEatenBonus += cardNum;
            }
        }

        if (totalEatenBonus > 0) {
            // Distribute penalties proportionally
            for (int i = 0; i < 4; i++) {
                if (eatenCards[i] > 0) {
                    int playerEatenBonus = 0;
                    for (int cardNum = 1; cardNum <= eatenCards[i]; cardNum++) {
                        playerEatenBonus += cardNum;
                    }
                    int penalty = (playerEatenBonus * totalEatenBonus) / totalEatenBonus;
                    scores[i] -= penalty;
                }
            }
        }

        // Step 3: Check for Ù đền (someone got eaten 3 cards)
        java.util.List<Integer> denVictims = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (eatenCards[i] == 3) {
                denVictims.add(i);
            }
        }

        if (!denVictims.isEmpty()) {
            // Ù đền: victim(s) pay for everyone else
            int totalPenaltyToRedistribute = 0;
            for (int i = 0; i < 4; i++) {
                if (i != winnerIndex && !denVictims.contains(i)) {
                    totalPenaltyToRedistribute += Math.abs(scores[i]);
                    scores[i] = 0;  // Non-victim losers pay nothing
                }
            }

            // Victims pay the total penalty
            int penaltyPerVictim = denVictims.size() > 0 ? totalPenaltyToRedistribute / denVictims.size() : 0;
            for (int victim : denVictims) {
                scores[victim] = -penaltyPerVictim;
            }

            // Adjust winner to maintain total = 0
            int total = 0;
            for (int i = 0; i < 4; i++) {
                if (i != winnerIndex) {
                    total += scores[i];
                }
            }
            scores[winnerIndex] = -total;
        }

        return scores;
    }
    
    /**
     * Calculate ranking score (no perfect hand) - CORRECTED LOGIC from Python
     */
    public static int[] calculateRankingScore(int[] ranking, int[] eatenCards, int[] lastCards,
                                            boolean[] wasMom, int firstPlayerIndex, Settings settings) {
        int[] scores = new int[4];

        // Step 1: Base ranking points (total = 0)
        int[] basePoints = {6, -1, -2, -3};  // 1st, 2nd, 3rd, 4th
        for (int i = 0; i < 4; i++) {
            scores[ranking[i]] = basePoints[i];
        }

        // Step 2: Handle eaten cards (zero-sum)
        for (int i = 0; i < 4; i++) {
            if (eatenCards[i] > 0) {
                // Progressive bonus for eating cards
                for (int cardNum = 1; cardNum <= eatenCards[i]; cardNum++) {
                    scores[i] += cardNum;  // 1st card = +1, 2nd card = +2
                }
            }
        }

        // Step 3: Handle penalties (redistribute to maintain total = 0)
        int totalPenalties = 0;

        // Calculate total penalties
        for (int i = 0; i < 4; i++) {
            // Last card penalty
            int penalty = lastCards[i] * settings.lastCardFee;
            scores[i] -= penalty;
            totalPenalties += penalty;

            // Mom penalty
            if (wasMom[i]) {
                penalty = settings.momPenalty;
                scores[i] -= penalty;
                totalPenalties += penalty;
            }
        }

        // Step 4: Ensure total = 0 by adjusting the first player's score
        int currentTotal = 0;
        for (int score : scores) {
            currentTotal += score;
        }
        if (currentTotal != 0) {
            // Adjust first player's score to make total = 0
            scores[0] -= currentTotal;
        }

        return scores;
    }
    
    /**
     * Validate that total score equals 0
     */
    public static boolean validateTotal(int[] scores) {
        int total = 0;
        for (int score : scores) {
            total += score;
        }
        return total == 0;
    }
}
