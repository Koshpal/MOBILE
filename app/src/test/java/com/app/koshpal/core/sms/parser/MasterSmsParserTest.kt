package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.dedup.DuplicateDetectorImpl
import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.bank.BankParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MasterSmsParserTest {

    private lateinit var transactionParser: TransactionSmsParser
    private lateinit var duplicateDetector: DuplicateDetectorImpl

    @Before
    fun setUp() {
        val parsers = listOf(
            BankParser.SbiSmsParser(),
            BankParser.BankOfBarodaSmsParser(),
            BankParser.CentralBankSmsParser(),
            BankParser.EquitasSmsParser(),
            BankParser.IndiaPostPaymentsBankSmsParser()
        )
        transactionParser = TransactionSmsParser(parsers)
        duplicateDetector = DuplicateDetectorImpl()
    }

    @Test
    fun `Confidence Merge - Detailed VPA wins over Summary Bare Name`() {
        // Summary Format (Low Confidence)
        val body1 = "A/c XX0898 credited by Rs. 1000.00 on 19082026 via UPI from Mr VEDANT  CHOUREY via Ref No. 659777234240. -CBoI"
        val sms1 = SmsMessage(1, "AX-CENTBK-T", body1, 0)
        val p1 = transactionParser.parse(sms1)
        assertNotNull(p1)
        assertEquals("Mr VEDANT  CHOUREY", p1?.senderName)
        assertEquals(false, p1?.isVpaAnchored)

        // Detailed Format (High Confidence)
        val body2 = "Your VPA vedantchourey99@okicici linked to CBI account no. XXXX0898 is credited with Rs. 1000.00 by rajeshchourey1970@okaxis (UPI Ref no 659777234240). - Central Bank of India"
        val sms2 = SmsMessage(2, "AX-CENTBK-S", body2, 0)
        val p2 = transactionParser.parse(sms2)
        assertNotNull(p2)
        assertEquals("rajeshchourey1970", p2?.senderName)
        assertEquals(true, p2?.isVpaAnchored)

        // Merge check
        val merged = duplicateDetector.removeDuplicates(listOf(p1!!), listOf(p2!!))
        // Since p2 is high confidence and matches p1 by ref, it should overwrite/emit p2
        assertEquals(1, merged.size)
        assertEquals("rajeshchourey1970", merged[0].senderName)
        assertTrue(merged[0].isVpaAnchored)
    }

    @Test
    fun `Income Receiver is always Me`() {
        val body = "A/c XX0898 credited by Rs. 1.00 on 16082026 via UPI from Master VATSAL UG JYOTI J via Ref No. 659335683853. -CBoI"
        val sms = SmsMessage(1, "AX-CENTBK-T", body, 0)
        val p = transactionParser.parse(sms)
        assertEquals("Me", p?.receiverName)
    }

    @Test
    fun `Garbage Cleanup - remitter is blacklisted`() {
        val body = "A/c X1234 credited Rs 1000 from REMITTER"
        // Force a match using a generic parser if needed, but let's just test ParsedTransaction sanitize
        val p = ParsedTransaction(1000.0, TransactionType.INCOME, Bank.SBI, "REMITTER", "Me", "1234", "123", 0)
        assertEquals(null, p.senderName)
    }
}
