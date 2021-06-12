package com.orange.credicard.proposal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentEncodeTest {

    @Test
    void simpleEncode() {
        String encryptDocument = DocumentEncode.simpleEncode("76681113877");
        assertEquals("O22SRRRNSOO", encryptDocument);
    }

    @Test
    void simpleEncode_with_mask() {
        String encryptDocument = DocumentEncode.simpleEncode("766.811.138-77");
        assertEquals("O22SRRRNSOO", encryptDocument);
    }

    @Test
    void simpleDecode() {
        String encryptDocument = DocumentEncode.simpleDecode("O22SRRRNSOO");
        assertEquals("76681113877", encryptDocument);
    }

}