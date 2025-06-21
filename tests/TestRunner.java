/**
 * Test Runner - Runs all test suites for Tá Lả Score Calculator
 * Separates business logic tests from UI flow tests
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("🚀 RUNNING ALL TÁ LẢ TESTS");
        System.out.println("=".repeat(60));
        
        boolean allTestsPassed = true;
        
        try {
            // Run Business Logic Tests
            System.out.println("\n📊 PHASE 1: BUSINESS LOGIC TESTS");
            System.out.println("-".repeat(40));
            runBusinessLogicTests();
            System.out.println("✅ Business Logic Tests: PASSED");
            
        } catch (Exception e) {
            System.out.println("❌ Business Logic Tests: FAILED");
            System.out.println("Error: " + e.getMessage());
            allTestsPassed = false;
        }
        
        try {
            // Run UI Flow Tests
            System.out.println("\n🎯 PHASE 2: UI FLOW TESTS");
            System.out.println("-".repeat(40));
            runUIFlowTests();
            System.out.println("✅ UI Flow Tests: PASSED");
            
        } catch (Exception e) {
            System.out.println("❌ UI Flow Tests: FAILED");
            System.out.println("Error: " + e.getMessage());
            allTestsPassed = false;
        }
        
        // Final Summary
        System.out.println("\n" + "=".repeat(60));
        if (allTestsPassed) {
            System.out.println("🎉 ALL TESTS PASSED!");
            System.out.println("✅ Business Logic: Mathematical correctness verified");
            System.out.println("✅ UI Flow: Navigation and constants validated");
            System.out.println("✅ Integration: Ready for production");
        } else {
            System.out.println("💥 SOME TESTS FAILED!");
            System.out.println("Please check the error messages above.");
            System.exit(1);
        }
        
        System.out.println("\n🎯 Test Coverage Summary:");
        System.out.println("   • Perfect Hand Scenarios (Khan, Thường, Tròn, Đền)");
        System.out.println("   • Ranking Scenarios (with mom, last cards, penalties)");
        System.out.println("   • Edge Cases (max eaten, zero eaten, multiple victims)");
        System.out.println("   • Settings Variations (high/low penalties)");
        System.out.println("   • UI Flow Validation (navigation, constants, steps)");
        System.out.println("   • Mathematical Validation (total = 0 guaranteed)");
        
        System.out.println("\n📁 Test Structure:");
        System.out.println("   • ScoreCalculatorTest.java - Business logic & calculations");
        System.out.println("   • UIFlowTest.java - UI navigation & flow validation");
        System.out.println("   • TestRunner.java - Orchestrates all test suites");
    }
    
    private static void runBusinessLogicTests() {
        // Redirect to ScoreCalculatorTest
        ScoreCalculatorTest.main(new String[]{});
    }
    
    private static void runUIFlowTests() {
        // Redirect to UIFlowTest
        UIFlowTest.main(new String[]{});
    }
}
