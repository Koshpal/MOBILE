package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.bank.BankParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CentralBankParsingTest {

    private val parser = BankParser.CentralBankSmsParser()

    @Test
    fun `test sms 1 - debit with VPA`() {
        val body = "Your account no. XXXXO898 is successfully debited for Rs.949.00 to VPA luvyn.cf@axisbank (UPI Ref no 612079799037) - Central Bank of India"
        val sms = SmsMessage(1L, "CENTBK", body, System.currentTimeMillis())
        val result = parser.parse(sms)
        
        assertNotNull(result)
        assertEquals(949.0, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
        assertEquals("612079799037", result?.referenceNumber)
        assertEquals("Me", result?.senderName)
        assertEquals("luvyn.cf", result?.receiverName)
    }

    @Test
    fun `test sms 2 - credit with user and sender VPA`() {
        val body = "Your VPA 6268403375@ptyes linked to CBI account no. XXXX0898 is credited with Rs. 900.00 by rajeshchourey1970@okaxis (UPI Ref no 622471551281). - Central Bank of India"
        val sms = SmsMessage(2L, "CENTBK", body, System.currentTimeMillis())
        val result = parser.parse(sms)
        
        assertNotNull(result)
        assertEquals(900.0, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("622471551281", result?.referenceNumber)
        assertEquals("rajeshchourey1970", result?.senderName)
        assertEquals("Me", result?.receiverName)
    }

    @Test
    fun `test sms 3 - credit with sender name after from`() {
        val body = "A/c XX0898 credited by Rs. 900.00 on 12082026 via UPI from 6268403375 ptyes via Ref No. 622471551281. -CBol"
        val sms = SmsMessage(3L, "CENTBK", body, System.currentTimeMillis())
        val result = parser.parse(sms)
        
        assertNotNull(result)
        assertEquals(900.0, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("622471551281", result?.referenceNumber)
        // In the summary message, the name after 'from' is assigned as sender (will be merged/discarded later)
        assertEquals("6268403375 ptyes", result?.senderName)
        assertEquals("Me", result?.receiverName)
    }

    @Test
    fun `test sms 4 - debit with garbage extraction prevention`() {
        val body = "A/c XX0898 debited by Rs. 949.00 via UPI vith Ref No. 612079799037 Total Bal Rs. 595.87 Clr Bal Rs. 595.87. -CBol"
        val sms = SmsMessage(4L, "CENTBK", body, System.currentTimeMillis())
        val result = parser.parse(sms)
        
        assertNotNull(result)
        assertEquals(949.0, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
        assertEquals("612079799037", result?.referenceNumber)
        assertEquals("Me", result?.senderName)
        assertEquals(null, result?.receiverName) // "Rs" should be blocked
    }
}
