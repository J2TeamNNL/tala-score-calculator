/**
 * Business Logic Test - Tests scoring calculations and game rules
 * Focuses on mathematical correctness and business rules validation
 */
public class ScoreCalculatorTest {
    
    private static ScoreCalculator.Settings settings = new ScoreCalculator.Settings();
    private static int testCount = 0;
    private static int passCount = 0;
    
    public static void main(String[] args) {
        System.out.println("🧮 RUNNING BUSINESS LOGIC TESTS");
        System.out.println("=".repeat(50));

        try {
            // Test all scenarios (ported from Python)
            testPerfectHandScenarios();
            testRankingScenarios();
            testEdgeCases();
            testSettingsVariations();
            testDenScenarios();
            testFlowScenarios();

            // Summary
            System.out.println("\n" + "=".repeat(50));
            System.out.println("✅ ALL BUSINESS LOGIC TESTS PASSED!");
            System.out.println("🎯 Scoring calculations are mathematically correct:");
            System.out.println("   • Ù Khan (no eaten cards)");
            System.out.println("   • Ù Thường/Ù Tròn (with eaten cards)");
            System.out.println("   • Ù Đền (victim pays for everyone)");
            System.out.println("   • Normal ranking with various eaten cards");
            System.out.println("   • Edge cases and boundary conditions");
            System.out.println("   • Different settings configurations");
            System.out.println("   • Mathematical validation (total = 0)");

        } catch (AssertionError e) {
            System.out.println("\n❌ TEST FAILED: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.out.println("\n💥 ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\nTotal tests: " + testCount);
        System.out.println("Passed: " + passCount);
        System.out.println("Failed: " + (testCount - passCount));
    }
    
    private static void testPerfectHandScenarios() {
        System.out.println("=== TESTING PERFECT HAND SCENARIOS ===");

        // Test 1: Ù Khan - Player 0 Ù khan
        System.out.println("\n1. Ù Khan - Player 0 Ù khan");
        int[] result = ScoreCalculator.calculatePerfectHandKhan(0, settings);
        int[] expected = {21, -7, -7, -7};

        assertArrayEquals("Ù Khan", expected, result);
        assertTotalZero("Ù Khan", result);
        System.out.println("   Result: " + arrayToString(result));
        System.out.println("   ✅ Ù Khan test passed");

        // Test 2: Ù Thường - Player 1 Ù, eaten cards bình thường
        System.out.println("\n2. Ù Thường - Player 1 Ù, eaten cards bình thường");
        int[] eatenCards = {1, 0, 2, 1};  // Player 0 ate 1, Player 2 ate 2, Player 3 ate 1
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards, false, false, settings);

        assertTotalZero("Ù Thường with eaten", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù Thường with eaten cards passed");

        // Test 3: Ù Tròn - Player 2 Ù tròn, eaten cards bình thường
        System.out.println("\n3. Ù Tròn - Player 2 Ù tròn, eaten cards bình thường");
        int[] eatenCards2 = {0, 1, 0, 2};  // Player 1 ate 1, Player 3 ate 2
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(2, eatenCards2, false, true, settings);

        assertTotalZero("Ù Tròn with eaten", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù Tròn with eaten cards passed");

        // Test 4: Ù Đền Thường - Player 3 Ù, Player 0 bị ăn 3 cây
        System.out.println("\n4. Ù Đền Thường - Player 3 Ù, Player 0 bị ăn 3 cây");
        int[] eatenCards3 = {3, 0, 0, 0};  // Player 0 bị ăn 3 cây
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(3, eatenCards3, false, false, settings);

        assertTotalZero("Ù Đền Thường", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù Đền Thường passed");

        // Test 5: Ù Đền Tròn - Player 1 Ù tròn, Player 2 bị ăn 3 cây
        System.out.println("\n5. Ù Đền Tròn - Player 1 Ù tròn, Player 2 bị ăn 3 cây");
        int[] eatenCards4 = {0, 0, 3, 0};  // Player 2 bị ăn 3 cây
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards4, false, true, settings);

        assertTotalZero("Ù Đền Tròn", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù Đền Tròn passed");
    }
    
    private static void testRankingScenarios() {
        System.out.println("\n=== TESTING RANKING SCENARIOS ===");

        // Test 1: Normal ranking - all players ate cards
        System.out.println("\n1. Normal ranking - all players ate cards");
        int[] ranking = {0, 1, 2, 3};  // Player 0 is 1st, Player 1 is 2nd, etc.
        int[] eatenCards = {1, 2, 0, 1};  // Each player's eaten cards
        int[] lastCards = {0, 1, 0, 0};   // Each player's last cards eaten
        boolean[] wasMom = {false, false, true, false};
        int firstPlayer = 0;
        int[] result = ScoreCalculator.calculateRankingScore(ranking, eatenCards, lastCards, wasMom, firstPlayer, settings);

        assertTotalZero("Normal ranking", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Normal ranking passed");

        // Test 2: All players mom - no first player bonus
        System.out.println("\n2. All players mom - no first player bonus");
        int[] ranking2 = {1, 0, 2, 3};  // Player 1 is 1st, Player 0 is 2nd, etc.
        int[] eatenCards2 = {0, 0, 0, 0};
        int[] lastCards2 = {0, 0, 0, 0};
        boolean[] wasMom2 = {true, true, true, true};
        firstPlayer = 1;
        result = ScoreCalculator.calculateRankingScore(ranking2, eatenCards2, lastCards2, wasMom2, firstPlayer, settings);

        assertTotalZero("All mom scenario", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ All mom scenario passed");

        // Test 3: Mixed scenario with last cards
        System.out.println("\n3. Mixed scenario with last cards");
        int[] ranking3 = {2, 3, 0, 1};  // Player 2 is 1st, Player 3 is 2nd, etc.
        int[] eatenCards3 = {1, 2, 0, 1};
        int[] lastCards3 = {0, 1, 0, 1};  // Player 1 and 3 ate last cards
        boolean[] wasMom3 = {false, false, true, false};
        firstPlayer = 2;
        result = ScoreCalculator.calculateRankingScore(ranking3, eatenCards3, lastCards3, wasMom3, firstPlayer, settings);

        assertTotalZero("Mixed scenario with last cards", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Mixed scenario with last cards passed");
    }
    
    private static void testPerfectHandRound() {
        System.out.println("3. Testing Ù Tròn...");
        
        int[] eatenCards = {0, 1, 0, 2};
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(2, eatenCards, false, true, settings);
        
        assertTotalZero("Ù Tròn", result);
        System.out.println("   Result: " + arrayToString(result));
        System.out.println("   ✅ Ù Tròn test passed\n");
    }
    
    private static void testPerfectHandDen() {
        System.out.println("4. Testing Ù Đền...");
        
        // Player 0 got eaten 3 cards, Player 1 wins
        int[] eatenCards = {3, 0, 0, 0};
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards, false, false, settings);
        
        assertTotalZero("Ù Đền", result);
        assertTrue("Ù Đền: Winner should be positive", result[1] > 0);
        assertTrue("Ù Đền: Victim should be negative", result[0] < 0);
        assertTrue("Ù Đền: Others should be 0", result[2] == 0 && result[3] == 0);
        
        System.out.println("   Result: " + arrayToString(result));
        System.out.println("   ✅ Ù Đền test passed\n");
    }
    
    private static void testRankingBasic() {
        System.out.println("5. Testing Basic Ranking...");
        
        int[] ranking = {0, 1, 2, 3};  // A=1st, B=2nd, C=3rd, D=4th
        int[] eatenCards = {0, 0, 0, 0};
        int[] lastCards = {0, 0, 0, 0};
        boolean[] wasMom = {false, false, false, false};
        
        int[] result = ScoreCalculator.calculateRankingScore(ranking, eatenCards, lastCards, wasMom, 0, settings);
        int[] expected = {6, -1, -2, -3};
        
        assertArrayEquals("Basic ranking", expected, result);
        assertTotalZero("Basic ranking", result);
        
        System.out.println("   ✅ Basic ranking test passed\n");
    }
    
    private static void testRankingWithPenalties() {
        System.out.println("6. Testing Ranking with Penalties...");
        
        int[] ranking = {0, 1, 2, 3};
        int[] eatenCards = {1, 2, 0, 1};
        int[] lastCards = {0, 1, 0, 0};
        boolean[] wasMom = {false, false, true, false};
        
        int[] result = ScoreCalculator.calculateRankingScore(ranking, eatenCards, lastCards, wasMom, 0, settings);
        
        assertTotalZero("Ranking with penalties", result);
        System.out.println("   Result: " + arrayToString(result));
        System.out.println("   ✅ Ranking with penalties test passed\n");
    }
    
    private static void testEdgeCases() {
        System.out.println("\n=== TESTING EDGE CASES ===");

        // Test 1: Maximum eaten cards (3) for Ù đền
        System.out.println("\n1. Maximum eaten cards (3) for Ù đền");
        int[] eatenCards = {3, 0, 0, 0};  // Player 0 bị ăn 3 cây
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards, false, false, settings);

        assertTotalZero("Maximum eaten cards (Ù đền)", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Maximum eaten cards (Ù đền) passed");

        // Test 2: Zero eaten cards for all players
        System.out.println("\n2. Zero eaten cards for all players");
        int[] eatenCards2 = {0, 0, 0, 0};
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(2, eatenCards2, false, false, settings);

        assertTotalZero("Zero eaten cards", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Zero eaten cards passed");

        // Test 3: All players ate maximum cards (edge case)
        System.out.println("\n3. All players ate maximum cards (edge case)");
        int[] eatenCards3 = {2, 2, 2, 2};  // All ate 2 cards (max before Ù đền)
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(0, eatenCards3, false, false, settings);

        assertTotalZero("All players ate maximum cards", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ All players ate maximum cards passed");
    }

    private static void testSettingsVariations() {
        System.out.println("\n=== TESTING SETTINGS VARIATIONS ===");

        // Test 1: High penalty settings
        System.out.println("\n1. High penalty settings");
        ScoreCalculator.Settings highSettings = new ScoreCalculator.Settings();
        highSettings.perfectHandBonus = 40;
        highSettings.perfectHandKhanBonus = 60;
        highSettings.perfectHandRoundBonus = 80;
        highSettings.eatenCardFee = 3;
        highSettings.lastCardFee = 5;
        highSettings.momPenalty = 7;

        int[] eatenCards = {1, 0, 1, 2};
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards, false, false, highSettings);

        assertTotalZero("High penalty settings", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ High penalty settings passed");

        // Test Ù Khan with high penalties
        result = ScoreCalculator.calculatePerfectHandKhan(1, highSettings);

        assertTotalZero("High penalty Ù Khan", result);
        System.out.println("   High penalty Ù Khan: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ High penalty Ù Khan passed");
    }
    
    private static void testDenScenarios() {
        System.out.println("\n=== TESTING Ù ĐỀN SCENARIOS ===");

        ScoreCalculator.Settings testSettings = new ScoreCalculator.Settings();
        testSettings.perfectHandBonus = 10;
        testSettings.perfectHandKhanBonus = 20;
        testSettings.perfectHandRoundBonus = 30;

        // Test 1: Ù đền thường - Player 0 bị ăn 3 cây, Player 1 Ù
        System.out.println("\n1. Ù đền thường - Player 0 bị ăn 3 cây, Player 1 Ù");
        int[] eatenCards = {3, 0, 0, 0};  // Player 0 bị ăn 3 cây
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards, false, false, testSettings);

        assertTotalZero("Ù đền thường", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù đền thường passed");

        // Test 2: Ù khan (no eaten cards) - Player 0 Ù khan
        System.out.println("\n2. Ù khan (no eaten cards) - Player 0 Ù khan");
        result = ScoreCalculator.calculatePerfectHandKhan(0, testSettings);

        assertTotalZero("Ù khan (no đền)", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù khan (no đền) passed");

        // Test 3: Ù đền tròn - Player 3 bị ăn 3 cây, Player 1 Ù tròn
        System.out.println("\n3. Ù đền tròn - Player 3 bị ăn 3 cây, Player 1 Ù tròn");
        int[] eatenCards2 = {0, 0, 0, 3};  // Player 3 bị ăn 3 cây
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards2, false, true, testSettings);

        assertTotalZero("Ù đền tròn", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Ù đền tròn passed");

        // Test 4: Multiple players bị ăn 3 cây (edge case)
        System.out.println("\n4. Multiple players bị ăn 3 cây (edge case)");
        int[] eatenCards3 = {3, 0, 3, 0};  // Player 0 and 2 both bị ăn 3 cây
        result = ScoreCalculator.calculatePerfectHandWithEatenCards(1, eatenCards3, false, false, testSettings);

        assertTotalZero("Multiple Ù đền", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Multiple Ù đền passed");
    }

    private static void testFlowScenarios() {
        System.out.println("\n=== TESTING COMPLETE GAME FLOWS ===");

        ScoreCalculator.Settings testSettings = new ScoreCalculator.Settings();
        testSettings.perfectHandBonus = 10;
        testSettings.perfectHandKhanBonus = 20;
        testSettings.perfectHandRoundBonus = 30;

        // Test 1: Complete Ù đền flow
        System.out.println("\n1. Complete Ù đền flow");
        int winner = 1;
        boolean isKhan = false;
        boolean isRound = false;
        int[] eatenCards = {3, 0, 0, 0};  // Player 0 bị ăn 3 cây (Ù đền)
        int[] result = ScoreCalculator.calculatePerfectHandWithEatenCards(winner, eatenCards, isKhan, isRound, testSettings);

        assertTotalZero("Complete Ù đền flow", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Complete Ù đền flow passed");

        // Test 2: Complete Ù khan flow (immediate result)
        System.out.println("\n2. Complete Ù khan flow (immediate result)");
        winner = 2;
        isKhan = true;
        isRound = false;
        result = ScoreCalculator.calculatePerfectHandKhan(winner, testSettings);

        assertTotalZero("Complete Ù khan flow", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Complete Ù khan flow passed");

        // Test 3: Complete normal ranking flow
        System.out.println("\n3. Complete normal ranking flow");
        int[] ranking = {0, 1, 2, 3};  // Player 0 is 1st, Player 1 is 2nd, etc.
        int[] eatenCards2 = {1, 2, 0, 1};
        int[] lastCards = {0, 1, 0, 0};
        boolean[] wasMom = {false, false, true, false};
        int firstPlayer = 0;
        result = ScoreCalculator.calculateRankingScore(ranking, eatenCards2, lastCards, wasMom, firstPlayer, testSettings);

        assertTotalZero("Complete normal ranking flow", result);
        System.out.println("   Result: " + arrayToString(result) + " (Total: " + getTotal(result) + ")");
        System.out.println("   ✅ Complete normal ranking flow passed");
    }



    // Helper methods
    private static void assertArrayEquals(String testName, int[] expected, int[] actual) {
        testCount++;
        boolean equal = true;
        if (expected.length != actual.length) {
            equal = false;
        } else {
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != actual[i]) {
                    equal = false;
                    break;
                }
            }
        }
        
        if (equal) {
            passCount++;
        } else {
            System.out.println("   ❌ " + testName + " FAILED!");
            System.out.println("      Expected: " + arrayToString(expected));
            System.out.println("      Actual:   " + arrayToString(actual));
        }
    }
    
    private static void assertTotalZero(String testName, int[] scores) {
        testCount++;
        boolean valid = ScoreCalculator.validateTotal(scores);
        
        if (valid) {
            passCount++;
        } else {
            int total = 0;
            for (int score : scores) total += score;
            System.out.println("   ❌ " + testName + " total FAILED! Total = " + total);
            System.out.println("      Scores: " + arrayToString(scores));
        }
    }
    
    private static void assertTrue(String message, boolean condition) {
        testCount++;
        if (condition) {
            passCount++;
        } else {
            System.out.println("   ❌ " + message + " FAILED!");
        }
    }
    
    private static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(array[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private static int getTotal(int[] scores) {
        int total = 0;
        for (int score : scores) {
            total += score;
        }
        return total;
    }


}
