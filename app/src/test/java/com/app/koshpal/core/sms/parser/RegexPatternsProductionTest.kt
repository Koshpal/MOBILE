package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.sms.parser.util.RegexPatterns
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RegexPatternsProductionTest {

    @Test
    fun `test party name extraction from contextual patterns`() {
        val sms1 = "Rs.500 spent at AMAZON via UPI"
        val match1 = RegexPatterns.PARTY_CONTEXTUAL.find(sms1)
        assertNotNull(match1)
        assertEquals("AMAZON", match1?.groupValues?.get(1)?.trim())

        val sms2 = "Paid to Zomato using Card"
        val match2 = RegexPatterns.PARTY_CONTEXTUAL.find(sms2)
        assertNotNull(match2)
        assertEquals("Zomato", match2?.groupValues?.get(1)?.trim())

        val sms3 = "transfer from Rajesh Chourey"
        val match3 = RegexPatterns.PARTY_CONTEXTUAL.find(sms3)
        assertNotNull(match3)
        assertEquals("Rajesh Chourey", match3?.groupValues?.get(1)?.trim())

        val badExtraction = "SWIGGY via"
        val cleaned = badExtraction.trim().split(Regex("""\s+via\b""", RegexOption.IGNORE_CASE))[0].trim()
        assertEquals("SWIGGY", cleaned)
    }

    @Test
    fun `test reference number extraction`() {
        val sms1 = "UPI Ref No 123456789012"
        assertEquals("123456789012", RegexPatterns.extractReferenceNumber(sms1))

        val sms2 = "UTR: 9876543210"
        assertEquals("9876543210", RegexPatterns.extractReferenceNumber(sms2))
    }

    @Test
    fun `test amount extraction with symbol`() {
        val sms1 = "₹500.00 debited"
        assertEquals(500.0, RegexPatterns.extractTransactionAmount(sms1))

        val sms2 = "Amount ₹ 1,250.50 credited"
        assertEquals(1250.5, RegexPatterns.extractTransactionAmount(sms2))
    }
}
