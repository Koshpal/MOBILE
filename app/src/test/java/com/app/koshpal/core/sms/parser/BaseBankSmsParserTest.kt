package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.bank.BankParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class BaseBankSmsParserTest {

    private lateinit var transactionParser: TransactionSmsParser

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
    }

    @Test
    fun `CENTBK - parses credit with by(sender) and from(user) correctly`() {
        // Example provided by user: Good message
        val body = "Your VPA 6268403375@ptyes linked to CBI account no. XXXX0898 is credited with Rs. 1050.00 by rajeshchourey1970@okaxis (UPI Ref no 658956023628). - Central Bank of India"
        val sms = SmsMessage(1, "AX-CENTBK-S", body, 0)
        
        val result = transactionParser.parse(sms)
        assertNotNull(result)
        assertEquals(1050.00, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("rajeshchourey1970", result?.senderName)
        assertEquals("Me", result?.receiverName) // Because Your VPA matched
    }

    @Test
    fun `CENTBK - parses credit with only from(user) correctly`() {
        // Example provided by user: "Bad" message that mentions user
        val body = "A/c XX0898 credited by Rs. 1000.00 on 19082026 via UPI from Mr VEDANT  CHOUREY        via Ref No. 659777234240. -CBoI"
        val sms = SmsMessage(1, "AX-CENTBK-T", body, 0)

        val result = transactionParser.parse(sms)
        assertNotNull(result)
        assertEquals(1000.0, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("Mr VEDANT  CHOUREY", result?.senderName)
        assertEquals("Me", result?.receiverName)
    }

    @Test
    fun `SBI - parses debit trf to party correctly`() {
        val body = "Dear UPI user A/C X7222 debited by 15.0 on date 29Jan25 trf to NARENDRA MACHHIN Refno 988409976348. If not u? call 1800111109. -SBI"
        val sms = SmsMessage(1, "TX-SBIUPI", body, 0)

        val result = transactionParser.parse(sms)
        assertNotNull(result)
        assertEquals(15.0, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
        assertEquals("NARENDRA MACHHIN", result?.receiverName)
    }

    @Test
    fun `Equitas - parses debit to party with Ref correctly`() {
        val body = "INR 156.00 debited via UPI from Equitas A/c 1647 -Ref:528920490602 on 16-10-25 to Amar Associates. Avl Bal is INR 115.02."
        val sms = SmsMessage(1, "VM-EQUTAS-S", body, 0)

        val result = transactionParser.parse(sms)
        assertNotNull(result)
        assertEquals(156.0, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
        assertEquals("Amar Associates", result?.receiverName)
    }

    @Test
    fun `IPPB - parses received payment from party correctly`() {
        val body = "You have received a payment of Rs. 150.00 in a/c X3695 on 07/10/2024 18:10 from Mr Ajinkya Jagannath thru IPPB. Info: UPI/CREDIT/428146723694.-IPPB"
        val sms = SmsMessage(1, "AX-IPBMSG", body, 0)

        val result = transactionParser.parse(sms)
        assertNotNull(result)
        assertEquals(150.0, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("Ajinkya Jagannath", result?.senderName)
    }
}
