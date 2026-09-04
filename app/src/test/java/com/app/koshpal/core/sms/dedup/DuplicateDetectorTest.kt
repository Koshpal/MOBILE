package com.app.koshpal.core.sms.dedup

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.ParsedTransaction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class DuplicateDetectorTest {
    private lateinit var detector: DuplicateDetector

    @Before
    fun setUp() {
        detector = DuplicateDetectorImpl()
    }

    @Test
    fun `drops incoming transaction with same reference number as existing`() {
        val existingTxn = transaction(referenceNumber = "REF123")
        val incomingTxn = transaction(referenceNumber = "REF123", amount = 999.0)

        val result = detector.removeDuplicates(
            existing = listOf(existingTxn),
            incoming = listOf(incomingTxn)
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `keep incoming transaction with different reference number as existing`() {
        val existingTxn = transaction(referenceNumber = "REF123", amount = 999.0)
        val incomingTxn = transaction(referenceNumber = "REF124", amount = 999.0)

        val result = detector.removeDuplicates(
            existing = listOf(existingTxn),
            incoming = listOf(incomingTxn)
        )

        assertEquals(1, result.size)
        assertEquals(incomingTxn, result[0])
    }

    @Test
    fun `drops duplicate when reference number missing but same amount and account within time bucket`() {
        val existingTxn = transaction(referenceNumber = null, timestamp = 1_000_000L)
        val incomingTxn = transaction(referenceNumber = null, timestamp = 1_030_000L)

        val result = detector.removeDuplicates(existing = listOf(existingTxn), incoming = listOf(incomingTxn))

        assertTrue(result.isEmpty())
    }

    @Test
    fun `keeps transaction when reference number missing and timestamps are far apart`() {
        val existingTxn = transaction(referenceNumber = null, timestamp = 1_000_000L)
        val incomingTxn = transaction(referenceNumber = null, timestamp = 1_700_000L)

        val result = detector.removeDuplicates(
            existing = listOf(existingTxn),
            incoming = listOf(incomingTxn)
        )

        assertEquals(1, result.size)
    }

    @Test
    fun `drops duplicate found within the same incoming batch`() {
        val txnA = transaction(referenceNumber = "REF999")
        val txnB = transaction(referenceNumber = "REF999", amount = 111.0)

        val result = detector.removeDuplicates(existing = emptyList(), incoming = listOf(txnA, txnB))

        assertEquals(1, result.size)
    }


    @Test
    fun `keeps all incoming when existing is empty and no duplicates among them`() {
        val txnA = transaction(referenceNumber = "REF001")
        val txnB = transaction(referenceNumber = "REF002")

        val result = detector.removeDuplicates(existing = emptyList(), incoming = listOf(txnA, txnB))

        assertEquals(2, result.size)
    }



    private fun transaction(
        amount: Double = 250.0,
        referenceNumber: String? = "REF123",
        timestamp: Long = 1_000_000L,
        accountNumber: String? = "1234"
    ) = ParsedTransaction(
        amount = amount,
        type = TransactionType.EXPENSE,
        bank = Bank.HDFC,
        senderName = "Me",
        receiverName = "Unknown",
        accountNumber = accountNumber,
        referenceNumber = referenceNumber,
        timestamp = timestamp
    )

}
