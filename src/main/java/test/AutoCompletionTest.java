package test;

import gui.utils.AutoCompletionManager;


public class AutoCompletionTest {
    
    public static void main(String[] args) {
        System.out.println("Testing AutoCompletionManager...");
        
        // Testa correspondências básicas de prefixos
        String result = AutoCompletionManager.findClosestMatch("sel");
        assert result != null && result.contains("selection") : "Failed: sel -> selection";
        
        result = AutoCompletionManager.findClosestMatch("proj");
        assert result != null && result.contains("projection") : "Failed: proj -> projection";
        
        result = AutoCompletionManager.findClosestMatch("join");
        assert result != null && result.contains("join") : "Failed: join -> join";
        
        // Testa correção de erros de digitação
        result = AutoCompletionManager.findClosestMatch("selecton");
        assert result != null && result.contains("selection") : "Failed: selecton -> selection";
        
        result = AutoCompletionManager.findClosestMatch("jion");
        assert result != null && result.contains("join") : "Failed: jion -> join";
        
        // Testa casos onde não deve haver correspondência
        result = AutoCompletionManager.findClosestMatch("xyz");
        assert result == null : "Failed: xyz should return null";
        
        result = AutoCompletionManager.findClosestMatch(null);
        assert result == null : "Failed: null should return null";
        
        System.out.println("All tests passed!");
    }
}
