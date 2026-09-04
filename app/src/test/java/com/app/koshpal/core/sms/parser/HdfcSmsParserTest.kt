package com.app.koshpal.core.sms.parser

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.SmsMessage
import com.app.koshpal.core.sms.parser.bank.BankParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class HdfcSmsParserTest {

    private lateinit var parser: BankParser.HdfcSmsParser

    @Before
    fun setUp() {
        parser = BankParser.HdfcSmsParser()
    }

    @Test
    fun `Plain HDFCBK sender is parsable`() {
        assertTrue(parser.isParsable(sms(sender = "HDFCBK", body = "")))
    }

    @Test
    fun ` sender with circle prefix is parsable`() {
        assertTrue(parser.isParsable(sms(sender = "VM-HDFCBK", body = "")))
    }

    @Test
    fun `sender with trailing suffix is parsable`() {
        assertTrue(parser.isParsable(sms(sender = "AD-HDFCBK-S", body = "")))
    }

    @Test
    fun `different bank sender is rejected`() {
        assertFalse(parser.isParsable(sms(sender = "VM-ICICIB", body = "")))
    }

    @Test
    fun `personal number is rejected`() {
        assertFalse(parser.isParsable(sms(sender = "+919876543210", body = "")))
    }

    @Test
    fun `parses debit transaction correctly`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.250.00 debited from A/c XX1234 on 04-Jul-26 at AMAZON. UPI Ref 123456789012. Avl Bal Rs.5000"
        )

        val result = parser.parse(sms)

        assertEquals(250.00, result?.amount)
        assertEquals(TransactionType.EXPENSE, result?.type)
        assertEquals(Bank.HDFC, result?.bank)
        assertEquals("1234", result?.accountNumber)
        assertEquals("123456789012", result?.referenceNumber)
    }

    @Test
    fun `parses credit transaction correctly`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.10,000.00 credited to A/c XX5678 on 04-Jul-26. Ref 987654321098. Avl Bal Rs.15000"
        )

        val result = parser.parse(sms)

        assertEquals(10000.00, result?.amount)
        assertEquals(TransactionType.INCOME, result?.type)
        assertEquals("5678", result?.accountNumber)
        assertEquals("987654321098", result?.referenceNumber)
    }

    @Test
    fun `handles amount with comma separator`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.1,25,000.00 debited from A/c XX1234. Ref 111222333"
        )

        assertEquals(125000.00, parser.parse(sms)?.amount)
    }

    @Test
    fun `handles amount without decimal places`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.500 debited from A/c XX1234. Ref 444555"
        )

        assertEquals(500.0, parser.parse(sms)?.amount)
    }


    @Test
    fun `returns null when amount cannot be extracted`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Your OTP is 1234. Do not share with anyone."
        )

        assertNull(parser.parse(sms))
    }

    @Test
    fun `returns null when transaction type cannot be determined`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.500 available for A/c XX1234 as per latest statement."
        )

        assertNull(parser.parse(sms))
    }

    @Test
    fun `returns transaction with null accountNumber when suffix is missing`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.500 debited. Ref 123456."
        )

        val result = parser.parse(sms)

        assertEquals(500.0, result?.amount)
        assertNull(result?.accountNumber)
    }

    @Test
    fun `returns transaction with null referenceNumber when ref is missing`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.500 debited from A/c XX1234."
        )

        val result = parser.parse(sms)

        assertEquals(500.0, result?.amount)
        assertNull(result?.referenceNumber)
    }

    private fun sms(sender: String, body: String) =
        SmsMessage(id = 1L, sender = sender, body = body, timestamp = 0L)
}