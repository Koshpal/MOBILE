package com.app.koshpal.core.sms.filter

import com.app.koshpal.core.sms.model.SmsMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SmsFilterImplTest {

    private lateinit var filter: SmsFilterImpl

    @Before
    fun setUp() {
        filter = SmsFilterImpl()
    }

    @Test
    fun `bank debit sms passes filter`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Rs.250.00 debited from A/c XX1234 on 04-Jul-26. UPI Ref 123456. Avl Bal Rs.5000"
        )

        val result = filter.filter(listOf(sms))

        assertEquals(listOf(sms), result)
    }

    @Test
    fun `bank credit sms passes filter`() {
        val sms = sms(
            sender = "JD-CENTBK-T",
            body = "A/c XX0896 credited by Rs. 500.00 via UPI with Ref no. 623860541023"
        )

        val result = filter.filter(listOf(sms))

        assertEquals(listOf(sms), result)
    }

    @Test
    fun `otp sms is rejected`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "123456 is your OTP for txn of Rs.500 at Amazon. Do not share."
        )

        assertTrue(filter.filter(listOf(sms)).isEmpty())
    }

    @Test
    fun `promotional sms is rejected`() {
        val sms = sms(
            sender = "AD-HDFCBK",
            body = "Get a pre-approved loan of Rs.5,00,000 at 10.5% interest. Apply now!"
        )

        assertTrue(filter.filter(listOf(sms)).isEmpty())
    }

    @Test
    fun `personal number sender is rejected`() {
        val sms = sms(
            sender = "+919876543210",
            body = "I debited the amount from your account, will send it back"
        )

        assertTrue(filter.filter(listOf(sms)).isEmpty())
    }

    @Test
    fun `plain numeric sender is rejected`() {
        val sms = sms(
            sender = "9876543210",
            body = "Rs.250 debited from your account"
        )

        assertTrue(filter.filter(listOf(sms)).isEmpty())
    }

    @Test
    fun `emi reminder is rejected despite bank sender`() {
        val sms = sms(
            sender = "VM-HDFCBK",
            body = "Your EMI of Rs.5000 is due on 10-Jul-26. Please maintain sufficient balance."
        )

        assertTrue(filter.filter(listOf(sms)).isEmpty())
    }

    @Test
    fun `mixed batch keeps only transactional messages`() {
        val debitSms = sms("VM-HDFCBK", "Rs.100 debited from A/c XX1234. Ref 111")
        val otpSms = sms("VM-HDFCBK", "Your OTP is 4321")
        val promoSms = sms("AD-ICICIB", "50% cashback offer this weekend!")
        val creditSms = sms("VM-AXISBK", "Rs.2000 credited to A/c XX9876. Ref 222")

        val result = filter.filter(listOf(debitSms, otpSms, promoSms, creditSms))

        assertEquals(listOf(debitSms, creditSms), result)
    }

    private fun sms(sender: String, body: String) =
        SmsMessage(id = 1L, sender = sender, body = body, timestamp = 0L)
}