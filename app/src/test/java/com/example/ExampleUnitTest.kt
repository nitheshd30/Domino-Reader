package com.example

import com.example.data.local.DominoBackupParser
import com.example.data.local.DominoSeedData
import com.example.data.model.DominoLabel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testUspCalculation() {
        val usp = DominoSeedData.calculateUsp("475.00", "200g")
        assertEquals("(USP ₹ 2.38/g)", usp)
    }

    @Test
    fun testBolasPistaSaltedLabelData() {
        val labels = DominoSeedData.getInitialLabels()
        val pistaLabel = labels.find { it.labelName.contains("PISTA SALTED 200G", ignoreCase = true) }
        
        assertNotNull("Pista salted label should exist in seed data", pistaLabel)
        pistaLabel?.let {
            assertEquals("IPRS026", it.batchNumber)
            assertEquals("10/09/2026", it.mfgDate)
            assertEquals("09/06/2026", it.useBy)
            assertEquals("475.00", it.mrp)
            assertEquals("(USP ₹ 2.38/g)", it.getEffectiveUsp())
            assertEquals("200g", it.weightDetails)
            assertEquals("475.00 (USP ₹ 2.38/g)", it.getDisplayMrp())
        }
    }

    @Test
    fun testDirectLblFileParsingFromRawBytesAndText() {
        val rawLblContent = """
            [DOMINO_LABEL_FORMAT_V2]
            ITEM: Bolas pista salted 200g (ps200)
            BATCH NO: IPRS026
            DATE OF MFG: 10/09/2026
            USE BY: 09/06/2026
            MRP: 475.00 (USP ₹ 2.38/g)
            WEIGHT: 200g
            RASTER: 16-Drop Pinpoint CIJ
        """.trimIndent()

        val parsed = DominoBackupParser.parseLabelFromText(
            text = rawLblContent,
            defaultName = "bolas_pista_200g.lbl",
            targetBackupId = 1L
        )

        assertEquals("IPRS026", parsed.batchNumber)
        assertEquals("10/09/2026", parsed.mfgDate)
        assertEquals("09/06/2026", parsed.useBy)
        assertEquals("475.00", parsed.mrp)
        assertEquals("(USP ₹ 2.38/g)", parsed.getEffectiveUsp())
        assertEquals("200g", parsed.weightDetails)
        assertEquals("475.00 (USP ₹ 2.38/g)", parsed.getDisplayMrp())
    }

    @Test
    fun testDirectLblUserExampleSentenceFormat() {
        val userPromptFormat = "Bolas pista salted 200g (ps200) batchnumber is IPRS026 date of mfg is 10/09/2026. Use by 09/06/2026. Mrp is 475.00. (USP ₹ 2.38/g)"

        val parsed = DominoBackupParser.parseLabelFromText(
            text = userPromptFormat,
            defaultName = "ps200.lbl",
            targetBackupId = 1L
        )

        assertEquals("IPRS026", parsed.batchNumber)
        assertEquals("10/09/2026", parsed.mfgDate)
        assertEquals("09/06/2026", parsed.useBy)
        assertEquals("475.00", parsed.mrp)
        assertEquals("(USP ₹ 2.38/g)", parsed.getEffectiveUsp())
        assertEquals("200g", parsed.weightDetails)
    }
}
