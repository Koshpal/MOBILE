package com.app.koshpal.core.sms.validator

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.model.ParsedTransaction
import com.app.koshpal.core.sms.validate.TransactionValidator
import com.app.koshpal.core.sms.validate.TransactionValidatorImpl
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class TransactionValidatorTest {
    private lateinit var validator: TransactionValidator

    @Before
    fun setUp() {
        validator = TransactionValidatorImpl()
    }


    @Test
    fun `rejects transaction with zero amount`() {
        assertFalse(validator.validate(validTransaction(amount = 0.0)))
    }

    @Test
    fun `rejects transaction with negative amount`() {
        assertFalse(validator.validate(validTransaction(amount = -50.0)))
    }


    @Test
    fun `rejects transaction with implausibly large amount`() {
        assertFalse(validator.validate(validTransaction(amount = 20_00_001.0)))
    }


    @Test
    fun `accepts a well-formed transaction`() {
        assertTrue(validator.validate(validTransaction(amount = 250.0)))
    }

    @Test
    fun `rejects transaction with future timestamp`() {
        val futureTime = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
        assertFalse(validator.validate(validTransaction(timestamp = futureTime)))
    }

    @Test
    fun `rejects transaction with timestamp too old`() {
        val ancientTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(365 * 3)
        assertFalse(validator.validate(validTransaction(timestamp = ancientTime)))
    }

    @Test
    fun `rejects transaction with type UNKNOWN`() {
        assertFalse(validator.validate(validTransaction(type = TransactionType.UNKNOWN)))
    }


    @Test
    fun `rejects transaction with zero timestamp`() {
        assertFalse(validator.validate(validTransaction(timestamp = 0)))
    }

    private fun validTransaction(
        amount: Double = 250.0,
        type: TransactionType = TransactionType.EXPENSE,
        timestamp: Long = System.currentTimeMillis()
    ) = ParsedTransaction(
        amount = amount,
        type = type,
        bank = Bank.HDFC,
        senderName = "Me",
        receiverName = "Unknown",
        accountNumber = "1234",
        referenceNumber = "111",
        timestamp = timestamp
    )


}
