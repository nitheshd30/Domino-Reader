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

    @Test
    fun testConsumableCategoriesAndSeedData() {
        val consumables = DominoSeedData.getInitialConsumables()
        assertTrue("Seed consumables should be empty by default", consumables.isEmpty())

        val redInk = com.example.data.model.ConsumableItem(
            name = "Domino 2RD001 Red CIJ Accent Ink",
            category = com.example.data.model.ConsumableCategory.INK.id,
            partNumber = "EP-2RD001-500",
            batchLotNumber = "L26-RD405",
            quantity = 0,
            unit = "Cartridges",
            minimumThreshold = 2
        )
        assertTrue("Red ink with 0 quantity should be out of stock", redInk.isOutOfStock)

        val foodInk = com.example.data.model.ConsumableItem(
            name = "Domino 2BK024 Food-Contact Grade Black Ink",
            category = com.example.data.model.ConsumableCategory.INK.id,
            partNumber = "EP-2BK024-500",
            batchLotNumber = "L26-FC012",
            quantity = 2,
            unit = "Cartridges",
            minimumThreshold = 3
        )
        assertTrue("Food ink with 2 quantity and threshold 3 should be low stock", foodInk.isLowStock)
    }

    @Test
    fun testDominoCilfXmlParsingExactUserFile() {
        val cilfXml = """
            <?xml version='1.0' encoding='UTF-8'?>
            <label xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" schemaVersion="1" xmlns="http://www.domino-printing.com/domino/common/CILF" labelVersion="1" xsi:schemaLocation="http://www.domino-printing.com/domino/common/CILF ..\Schema\CILFSchema.xsd" name="BOLAS ROYAL ALMOND 250G (RA250)">
             <metadata>
              <browserImage></browserImage>
              <imageFormat>eBMP</imageFormat>
              <customData id="QuickStep">
               <parameter>
                <key>crc</key>
                <value>crc=4a9df7dd3f5257888f1e7540e912fc8a</value>
               </parameter>
              </customData>
             </metadata>
             <layout>
              <channel>
               <regionId>region_01</regionId>
               <segment id="segment_01">
                <compositeElement>
                 <z-order>1</z-order>
                 <xPosition>0</xPosition>
                 <yPosition>0</yPosition>
                 <textElement>
                  <name>Text 1</name>
                  <xPosition>13</xPosition>
                  <yPosition>0</yPosition>
                  <contentId>00000004</contentId>
                 </textElement>
                 <textElement>
                  <name>Text 2</name>
                  <xPosition>13</xPosition>
                  <yPosition>9</yPosition>
                  <contentId>00000003</contentId>
                 </textElement>
                 <textElement>
                  <name>Text 3</name>
                  <xPosition>13</xPosition>
                  <yPosition>18</yPosition>
                  <contentId>00000005</contentId>
                 </textElement>
                 <textElement>
                  <name>Text 5</name>
                  <xPosition>0</xPosition>
                  <yPosition>27</yPosition>
                  <contentId>00000002</contentId>
                 </textElement>
                 <textElement>
                  <name>Text 4</name>
                  <xPosition>36</xPosition>
                  <yPosition>27</yPosition>
                  <contentId>00000006</contentId>
                 </textElement>
                 <bitPatternElement>
                  <name>Image 1</name>
                  <xPosition>57</xPosition>
                  <yPosition>27</yPosition>
                  <contentId>00000007</contentId>
                 </bitPatternElement>
                </compositeElement>
               </segment>
              </channel>
             </layout>
             <staticContent id="00000002">
              <value>
               <string>429.00</string>
              </value>
             </staticContent>
             <staticContent id="00000003">
              <value>
               <string>18/08/2026</string>
              </value>
             </staticContent>
             <staticContent id="00000004">
              <value>
               <string>HRA026</string>
              </value>
             </staticContent>
             <staticContent id="00000005">
              <value>
               <string>17/08/2027</string>
              </value>
             </staticContent>
             <staticContent id="00000006">
              <value>
               <string>(USP   1.72/g)</string>
              </value>
             </staticContent>
             <staticContent id="00000007">
              <value>
               <binary>
                <formatId>eAUTO</formatId>
                <fileName>IMAGE\RS.bmp</fileName>
               </binary>
              </value>
             </staticContent>
            </label>
        """.trimIndent()

        val parsed = DominoBackupParser.parseLabelFromText(cilfXml, "BOLAS_ALMOND.lbl", 0, 1L)

        assertEquals("HRA026", parsed.batchNumber)
        assertEquals("18/08/2026", parsed.mfgDate)
        assertEquals("17/08/2027", parsed.useBy)
        assertEquals("429.00", parsed.mrp)
        assertEquals("(USP 1.72/g)", parsed.getEffectiveUsp())
        assertEquals("250g", parsed.weightDetails)
        assertEquals("BOLAS_ALMOND.lbl", parsed.getDisplayFileName())
        assertEquals("Bolas", parsed.brand)
        assertEquals("Almonds", parsed.productCategory)
        assertEquals("RS.bmp", parsed.associatedImage)

        // Verify print lines
        val lines = parsed.getPrintLines()
        assertEquals(4, lines.size)
        assertEquals("HRA026", lines[0])
        assertEquals("18/08/2026", lines[1])
        assertEquals("17/08/2027", lines[2])
        assertTrue("Line 4 should contain 429.00 and USP 1.72/g", lines[3].contains("429.00") && lines[3].contains("1.72/g"))
    }

    @Test
    fun testDominoUserPromptInstructionFormat() {
        val promptText = """
            In this code of lbl file 
            Batch number is HRA026 Mfg is 18/08/2026
            Use by is 17/08/2027 and MRP is 429.00  Usp is USP 1.72/g

            Decode like this
        """.trimIndent()

        val parsed = DominoBackupParser.parseLabelFromText(promptText, "BOLAS ROYAL ALMOND 250G (RA250).lbl", 0, 1L)

        assertEquals("HRA026", parsed.batchNumber)
        assertEquals("18/08/2026", parsed.mfgDate)
        assertEquals("17/08/2027", parsed.useBy)
        assertEquals("429.00", parsed.mrp)
        assertEquals("(USP 1.72/g)", parsed.getEffectiveUsp())
    }
}
