package mx.uv.sistemagestionpizzeria.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HasheoContraseniaTest {

    @Test
    public void testHashPasswordConsistency() {
        String password = "testPassword";
        String hash1 = HasheoContrasenia.hashPassword(password);
        String hash2 = HasheoContrasenia.hashPassword(password);
        
        assertEquals(hash1, hash2);
    }
    
    @Test
    public void testHashPasswordDifferent() {
        String password1 = "password123";
        String password2 = "password456";
        
        String hash1 = HasheoContrasenia.hashPassword(password1);
        String hash2 = HasheoContrasenia.hashPassword(password2);
        
        assertNotEquals(hash1, hash2);
    }
}
