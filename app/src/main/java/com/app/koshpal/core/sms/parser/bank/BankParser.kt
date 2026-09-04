package com.app.koshpal.core.sms.parser.bank

import com.app.koshpal.core.data.entities.enums.Bank
import com.app.koshpal.core.data.entities.enums.TransactionType
import com.app.koshpal.core.sms.parser.BaseBankSmsParser


object BankParser {

    class SbiSmsParser : BaseBankSmsParser() {
        override val bank = Bank.SBI
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:ATMSBI|CBSSBI|SBICRD|SBIINB|SCISMS|SBIUPI|SBIPSG|SBGMBS|SBIETC|SBIBIL|SBYONO|SBMSMS|SBIDGT|SBIKBP|SBINPS|SBIBNK).*|^1722$""",
            RegexOption.IGNORE_CASE
        )
    }

    class BankOfBarodaSmsParser : BaseBankSmsParser() {
        override val bank = Bank.BOB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:BOBTXN|BOBCRD|BOBADC|BOBSMS|MCONEC|BOBFSL).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class BankOfIndiaSmsParser : BaseBankSmsParser() {
        override val bank = Bank.BANK_OF_INDIA
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:BOIIND|STARBI|BOITXN|BOIBAL|BOIWLI).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class BankOfMaharashtraSmsParser : BaseBankSmsParser() {
        override val bank = Bank.BANK_OF_MAHARASHTRA
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?MAHABK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class CanaraSmsParser : BaseBankSmsParser() {
        override val bank = Bank.CANARA
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:CANBNK|CANCRD|WLISYA|CNRBNK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class CentralBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.CENTRAL_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:CENTBK|CBIOTP|CBoI|CBISMS).*""",
            RegexOption.IGNORE_CASE
        )
        override val displayNameAliases = listOf("CBoI", "Central Bank of India")
    }

    class IndianBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.INDIAN_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:INDBNK|INDBCC|INBUPI).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class IndianOverseasBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.INDIAN_OVERSEAS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?IOBCHN.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class PunjabSindBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.PUNJAB_SIND_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?PSBANK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class PnbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.PNB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:PNBSMS|PNBRWD|PNBHFL|PNBCCD|PNBCRC|PNBRTS|PNBMOB|PNBUPI).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class UcoBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.UCO_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?UCOBNK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class UnionBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.UNION_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:UNIONB|IPRSMS).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class AxisSmsParser : BaseBankSmsParser() {
        override val bank = Bank.AXIS
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:AXISBK|AXISPG|AXISGP).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class BandhanSmsParser : BaseBankSmsParser() {
        override val bank = Bank.BANDHAN
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:BNDNBK|BDNSMS).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class CsbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.CSB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?CSBBNK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class CityUnionSmsParser : BaseBankSmsParser() {
        override val bank = Bank.CITY_UNION
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:CUBANK|CUBLTD|CUBSMS|CUBCRD).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class DcbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.DCB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:DCBANK|DCBBNK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class DhanlaxmiSmsParser : BaseBankSmsParser() {
        override val bank = Bank.DHANLAXMI
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?DHANBK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class FederalBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.FEDERAL
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:FEDBNK|FEDFIB).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class HdfcSmsParser : BaseBankSmsParser() {
        override val bank = Bank.HDFC
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:HDFCBK|HDFCMP|TXNALE|HDFCBN|HDFCCC|PAYZAP).*|^5676712$""",
            RegexOption.IGNORE_CASE
        )
        override val displayNameAliases = listOf("HDFC Bank")
    }

    class IciciSmsParser : BaseBankSmsParser() {
        override val bank = Bank.ICICI
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:ICICIB|ICIBNK|ICICIT).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class IndusIndSmsParser : BaseBankSmsParser() {
        override val bank = Bank.INDUSIND
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:INDUSB|INDUSA).*|^040801$""",
            RegexOption.IGNORE_CASE
        )
    }

    class IdfcFirstSmsParser : BaseBankSmsParser() {
        override val bank = Bank.IDFC_FIRST
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:IDFCBK|IDFCFB|IDFCTS|FAMPAY).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class JkBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.JK_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:JKBANK|JKCARD).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class KarnatakaBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.KARNATAKA_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:KTKBNK|KBLBNK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class KarurVysyaSmsParser : BaseBankSmsParser() {
        override val bank = Bank.KARUR_VYSYA
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:KVBANK|KVBUPI|KVBMPY).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class KotakSmsParser : BaseBankSmsParser() {
        override val bank = Bank.KOTAK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:KOTAKB|KOTAKP).*|^(?:040100|111000)$""",
            RegexOption.IGNORE_CASE
        )
    }

    class NainitalBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.NAINITAL
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?NAINTL.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class RblSmsParser : BaseBankSmsParser() {
        override val bank = Bank.RBL
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:RBLBNK|RBLCRD).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class SouthIndianBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.SOUTH_INDIAN
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?SIBSMS.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class TamilnadMercantileSmsParser : BaseBankSmsParser() {
        override val bank = Bank.TAMILNAD_MERCANTILE
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?TMBANK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class YesBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.YES_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:YESBNK|YESBAK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class IdbiSmsParser : BaseBankSmsParser() {
        override val bank = Bank.IDBI
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:IDBIBK|WLIDBI).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class AuSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.AU_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?AUBANK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class CapitalSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.CAPITAL_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?CAPSFB.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class EquitasSmsParser : BaseBankSmsParser() {
        override val bank = Bank.EQUITAS
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:EQUTAS|EQUTAT|EQUTAX).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class EsafSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.ESAF_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?ESAFSF.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class SuryodaySfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.SURYODAY_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?SURYSF.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class UjjivanSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.UJJIVAN_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?UJJIVN.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class UtkarshSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.UTKARSH_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:UTKSPR|UTKBNK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class SliceSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.SLICE_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?SLCEIT.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class JanaSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.JANA_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?JANASF.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class ShivalikSfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.SHIVALIK_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?SHVLIK.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class UnitySfbSmsParser : BaseBankSmsParser() {
        override val bank = Bank.UNITY_SFB
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?UNITSF.*""",
            RegexOption.IGNORE_CASE
        )
    }

    class AirtelPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.AIRTEL_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:AIRMNY|AIRBNK).*""",
            RegexOption.IGNORE_CASE
        )

        override val displayNameAliases = listOf("Airtel Payments Bank")
    }

    class IndiaPostPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.INDIA_POST_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:DOPBNK|IPBMSG).*""",
            RegexOption.IGNORE_CASE
        )

        override fun extractAmount(body: String): Double? =
            super.extractAmount(body) ?: run {
                val m = RECEIVED_PAYMENT_OF.find(body)
                    ?: BARE_DEBIT_CREDIT.find(body)
                    ?: AUX_VERB_GAP.find(body)
                m?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            }

        override fun detectTransactionType(body: String): TransactionType {
            val base = super.detectTransactionType(body)
            if (base != TransactionType.UNKNOWN) return base

            val lower = body.lowercase()
            return when {
                "received a payment" in lower -> TransactionType.INCOME
                Regex("""\bdebit\s+(?:rs\.?|inr)""", RegexOption.IGNORE_CASE).containsMatchIn(body) -> TransactionType.EXPENSE
                Regex("""\bcredit\s+(?:rs\.?|inr)""", RegexOption.IGNORE_CASE).containsMatchIn(body) -> TransactionType.INCOME
                else -> TransactionType.UNKNOWN
            }
        }

        companion object {
            private val RECEIVED_PAYMENT_OF = Regex(
                """received a payment of (?:Rs\.?|INR)\s?([0-9,]+(?:\.[0-9]{1,2})?)""",
                RegexOption.IGNORE_CASE
            )
            private val BARE_DEBIT_CREDIT = Regex(
                """\b(?:debit|credit)\s+(?:Rs\.?|INR)\s?([0-9,]+(?:\.[0-9]{1,2})?)""",
                RegexOption.IGNORE_CASE
            )
            private val AUX_VERB_GAP = Regex(
                """(?:Rs\.?|INR)\s?([0-9,]+(?:\.[0-9]{1,2})?)\s+is\s+(?:debited|credited)""",
                RegexOption.IGNORE_CASE
            )
        }
    }

    class FinoPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.FINO_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:FINOBK|FINOIN).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class PaytmPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.PAYTM_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:IPAYTM|VPAYTM|PAYTMB|PYTMBK).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class JioPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.JIO_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?(?:JIOMNY|JIOPBL).*""",
            RegexOption.IGNORE_CASE
        )
    }

    class NsdlPaymentsBankSmsParser : BaseBankSmsParser() {
        override val bank = Bank.NSDL_PAYMENTS_BANK
        override val senderPattern = Regex(
            """^[A-Z]{0,2}-?NSDLPB.*""",
            RegexOption.IGNORE_CASE
        )
    }
}