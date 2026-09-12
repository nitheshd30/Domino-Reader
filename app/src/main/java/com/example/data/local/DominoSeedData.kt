package com.example.data.local

import com.example.data.model.DominoLabel
import com.example.data.model.LabelFormatType
import com.example.data.model.PrinterBackup
import com.example.data.model.ProductionLog

object DominoSeedData {

    fun getInitialBackups(): List<PrinterBackup> {
        return listOf(
            PrinterBackup(
                id = 1,
                printerName = "Packaging Line 1 - Bolas Nuts & Dry Fruits",
                printerModel = "Ax350i",
                serialNumber = "AX350-209148",
                lineLocation = "Conveyor Line 1 (Pouch Packing)",
                firmwareVersion = "QuickStep v5.4.1",
                backupDate = "2026-09-11 08:30",
                totalLabelsCount = 42,
                totalPacksPrinted = 48250,
                status = "Active",
                nozzleSizeDrop = "16 Drop / 60μm (100mm/25)",
                inkType = "2BK001 Black Ketone CIJ",
                notes = "Primary high-speed dry fruit pouch line. Auto-flush enabled."
            ),
            PrinterBackup(
                id = 2,
                printerName = "Packaging Line 2 - Tata & Molsis Retail",
                printerModel = "Ax150i",
                serialNumber = "AX150-108422",
                lineLocation = "Packaging Line 2 (Multi-Brand)",
                firmwareVersion = "QuickStep v5.3.2",
                backupDate = "2026-09-10 17:45",
                totalLabelsCount = 38,
                totalPacksPrinted = 31400,
                status = "Active",
                nozzleSizeDrop = "16 Drop / 60μm (100mm/33)",
                inkType = "2BK001 Black Fast-Dry",
                notes = "Dedicated to Tata Consumer and Molsis retail cartons."
            ),
            PrinterBackup(
                id = 3,
                printerName = "Packaging Line 3 - Sweets, Honey & Festive Boxes",
                printerModel = "Ax550i",
                serialNumber = "AX550-334910",
                lineLocation = "Confectionery & Gift Box Cell",
                firmwareVersion = "QuickStep v5.4.1",
                backupDate = "2026-09-09 14:15",
                totalLabelsCount = 45,
                totalPacksPrinted = 26900,
                status = "Standby",
                nozzleSizeDrop = "16 Drop / 75μm (Heavy Duty)",
                inkType = "2BK024 Food Contact Grade",
                notes = "Sanitary IP66 line for honey bottles, Kaju Katli, and festive gift boxes."
            )
        )
    }

    fun getInitialLabels(): List<DominoLabel> {
        val list = mutableListOf<DominoLabel>()

        // ================= PRINTER 1 (Bolas Nuts Line 1 - Ax350i) =================
        data class SeedItem(
            val name: String,
            val batch: String,
            val mfd: String = "10/09/2026",
            val useBy: String = "09/06/2026",
            val mrp: String,
            val weight: String,
            val customUsp: String? = null,
            val formatType: String = LabelFormatType.BOLAS_STANDARD.id,
            val headCode: String = "2860"
        )

        val p1Items = listOf(
            // Photo 3: BOLAS STANDARD FORMAT (ICH026)
            SeedItem("Bolas cashew premium 250g (cn250)", "ICH026", "10/09/2026", "09/09/2027", "439.00", "250g", "(USP ₹1.76/g)", LabelFormatType.BOLAS_STANDARD.id, "2860"),
            // Photo 2: BOLAS BOX FORMAT (IARS026)
            SeedItem("Bolas almond roasted box 200g (arb200)", "IARS026", "Sep.2026", "May.2027", "392.00", "200g", "(USP ₹1.96/g)", LabelFormatType.BOLAS_BOX.id, "2860"),
            SeedItem("Bolas walnut kernels 200g (wn200)", "IPWN026", "10/09/2026", "09/06/2026", "380.00", "200g", "(USP ₹ 1.90/g)"),
            SeedItem("Bolas pista salted 200g (ps200)", "IPRS026", "10/09/2026", "09/06/2026", "475.00", "200g", "(USP ₹ 2.38/g)"),
            SeedItem("Bolas cashew splits 250g jh (jh250)", "IPJH026", "10/09/2026", "09/06/2026", "290.00", "250g", "(USP ₹ 1.16/g)"),
            SeedItem("Bolas cashew pieces 250g jk (jk250)", "IPJK026", "10/09/2026", "09/06/2026", "260.00", "250g", "(USP ₹ 1.04/g)"),
            SeedItem("Bolas chilly cashew 200g (cc200)", "IPCC026", "10/09/2026", "09/06/2026", "310.00", "200g", "(USP ₹ 1.55/g)"),
            SeedItem("Bolas berry blast 200g (bb200)", "IPBB026", "10/09/2026", "09/06/2026", "340.00", "200g", "(USP ₹ 1.70/g)"),
            SeedItem("Bolas roasted seed mix 200g (rsm200)", "IPRSM026", "10/09/2026", "09/06/2026", "240.00", "200g", "(USP ₹ 1.20/g)"),
            SeedItem("Bolas premium cashew nut 250g (pcn250)", "IPPCN026", "10/09/2026", "09/06/2026", "395.00", "250g", "(USP ₹ 1.58/g)"),
            SeedItem("Bolas mamra almond 200g (ma200)", "IPMA026", "10/09/2026", "09/06/2026", "490.00", "200g", "(USP ₹ 2.45/g)"),
            SeedItem("Bolas cashew salted 200g (cs200)", "IPCS026", "10/09/2026", "09/06/2026", "310.00", "200g", "(USP ₹ 1.55/g)"),
            SeedItem("Bolas turkish hazelnuts raw 200g (thr200)", "IPTHR026", "10/09/2026", "09/06/2026", "420.00", "200g", "(USP ₹ 2.10/g)"),
            SeedItem("Bolas gourmet trail mix 200g (gtm200)", "IPGTM026", "10/09/2026", "09/06/2026", "360.00", "200g", "(USP ₹ 1.80/g)"),
            SeedItem("Bolas royal almond 250g (ra250)", "IPRA026", "10/09/2026", "09/06/2026", "350.00", "250g", "(USP ₹ 1.40/g)"),
            SeedItem("Bolas pepper cashew box 200g (pcb200)", "IPPCB026", "10/09/2026", "09/06/2026", "325.00", "200g", "(USP ₹ 1.63/g)"),
            SeedItem("Bolas pepper cashew 200g (pc200)", "IPPC026", "10/09/2026", "09/06/2026", "315.00", "200g", "(USP ₹ 1.58/g)"),
            SeedItem("Bolas pista salted pepper 200g (psp200)", "IPPSP026", "10/09/2026", "09/06/2026", "485.00", "200g", "(USP ₹ 2.43/g)"),
            SeedItem("Bolas sunflower roasted & salted 200g (srs200)", "IPSRS026", "10/09/2026", "09/06/2026", "180.00", "200g", "(USP ₹ 0.90/g)"),
            SeedItem("Bolas wood fired cashew 200g nw (wfc200)", "IPWFC026", "10/09/2026", "09/06/2026", "370.00", "200g", "(USP ₹ 1.85/g)"),
            SeedItem("Bolas salted caashew fried 200g (bscf200)", "IPBSCF026", "10/09/2026", "09/06/2026", "330.00", "200g", "(USP ₹ 1.65/g)"),
            SeedItem("Bolas coastal masala cashew fried 200g (bcmc200)", "IPBCMC026", "10/09/2026", "09/06/2026", "340.00", "200g", "(USP ₹ 1.70/g)"),
            SeedItem("Bolas pepper cashew cashew fried 200g (bpcf200)", "IPBPCF026", "10/09/2026", "09/06/2026", "340.00", "200g", "(USP ₹ 1.70/g)"),
            SeedItem("Bolas smoked almond 200g (sa200)", "IPSA026", "10/09/2026", "09/06/2026", "360.00", "200g", "(USP ₹ 1.80/g)"),
            SeedItem("Bolas cashew value pack 250g (cvp250)", "IPCVP026", "10/09/2026", "09/06/2026", "280.00", "250g", "(USP ₹ 1.12/g)"),
            SeedItem("Bolas panchmeva dry fruit mix 250g (pm250)", "IPPM026", "10/09/2026", "09/06/2026", "320.00", "250g", "(USP ₹ 1.28/g)"),
            SeedItem("Bolas panchmeva dry fruit mix 500g (pm500)", "IPPM026", "10/09/2026", "09/06/2026", "599.00", "500g", "(USP ₹ 1.20/g)"),
            SeedItem("Bolas cashew nut 250g (cn250)", "IPCN026", "10/09/2026", "09/06/2026", "360.00", "250g", "(USP ₹ 1.44/g)"),
            SeedItem("Bolas royal almond 500g (ra500)", "IPRA026", "10/09/2026", "09/06/2026", "680.00", "500g", "(USP ₹ 1.36/g)"),
            SeedItem("Bolas cashew pieces 500g (cp500)", "IPCP026", "10/09/2026", "09/06/2026", "490.00", "500g", "(USP ₹ 0.98/g)"),
            SeedItem("Bolas daily trail mix 200g (dtm200)", "IPDTM026", "10/09/2026", "09/06/2026", "260.00", "200g", "(USP ₹ 1.30/g)"),
            SeedItem("Bolas almond value pack 250g (avp250)", "IPAVP026", "10/09/2026", "09/06/2026", "290.00", "250g", "(USP ₹ 1.16/g)"),
            SeedItem("Bolas blanched almond 200g (ba200)", "IPBA026", "10/09/2026", "09/06/2026", "375.00", "200g", "(USP ₹ 1.88/g)"),
            SeedItem("Bolas brazil nuts 200g (bra200)", "IPBRA026", "10/09/2026", "09/06/2026", "650.00", "200g", "(USP ₹ 3.25/g)"),
            SeedItem("Bolas tiny cashew 250g (tcn250)", "IPTCN026", "10/09/2026", "09/06/2026", "270.00", "250g", "(USP ₹ 1.08/g)"),
            SeedItem("Bolas cashew nuts 500g (cn500)", "IPCN026", "10/09/2026", "09/06/2026", "690.00", "500g", "(USP ₹ 1.38/g)"),
            SeedItem("Bolas jumbo cashew 500g (jc500)", "IPJC026", "10/09/2026", "09/06/2026", "780.00", "500g", "(USP ₹ 1.56/g)"),
            SeedItem("Bolas super saver cashew 500g (ss500)", "IPSS026", "10/09/2026", "09/06/2026", "580.00", "500g", "(USP ₹ 1.16/g)"),
            SeedItem("Bolas almonds 75g (a75)", "IPA026", "10/09/2026", "09/06/2026", "99.00", "75g", "(USP ₹ 1.32/g)"),
            SeedItem("Bolas iranian pista salted 200g (iprs200)", "IPRS026", "10/09/2026", "09/06/2026", "475.00", "200g", "(USP ₹ 2.38/g)"),
            SeedItem("Bolas broken walnut 200g (bw200)", "IPBW026", "10/09/2026", "09/06/2026", "310.00", "200g", "(USP ₹ 1.55/g)"),
            SeedItem("Bolas american pecan r&s 200g (apn200)", "IPAPN026", "10/09/2026", "09/06/2026", "480.00", "200g", "(USP ₹ 2.40/g)"),
            SeedItem("Bolas pista salted 400g (ps400)", "IPRS026", "10/09/2026", "09/06/2026", "920.00", "400g", "(USP ₹ 2.30/g)"),
            SeedItem("Bolas indian raisins 250g", "IPIR026", "10/09/2026", "09/06/2026", "140.00", "250g", "(USP ₹ 0.56/g)")
        )

        p1Items.forEachIndexed { index, item ->
            val usp = item.customUsp ?: calculateUsp(item.mrp, item.weight)
            list.add(
                DominoLabel(
                    printerBackupId = 1,
                    fileName = "${item.name}.lbl",
                    labelName = item.name,
                    brand = "Bolas",
                    productCategory = detectCategory(item.name),
                    batchNumber = item.batch,
                    mfgDate = item.mfd,
                    useBy = item.useBy,
                    expiryDate = item.useBy,
                    mrp = item.mrp,
                    weightDetails = item.weight,
                    unitSalePrice = usp,
                    rasterDropSize = "16 Drop 100mm 25",
                    associatedImage = if (item.name.contains("RUPEES", ignoreCase = true)) "RUPEES SYMBOL.bmp" else "BOLAS NEW.bmp",
                    barcodeData = "8906018" + (10000 + index),
                    rawLabelContent = buildDominoRawContent(item.name, item.batch, item.mfd, item.useBy, item.mrp, usp, item.weight),
                    printCount = (1200 + index * 85).toLong(),
                    formatType = item.formatType,
                    printerHeadCode = item.headCode
                )
            )
        }

        // ================= PRINTER 2 (Tata, Molsis & Runutz - Ax150i) =================
        val p2Items = listOf(
            Triple("TATA ALMOND 500G", "500g (Net Wt)", "Rs. 830"),
            Triple("TATA PISTACHIOS 200G", "200g (Net Wt)", "Rs. 340.00"),
            Triple("TATA PISTACHIOS 500G", "500g (Net Wt)", "Rs. 720.00"),
            Triple("TATA ALMOND 200G", "200g (Net Wt)", "Rs. 280.00"),
            Triple("TATA ALMOND 1 Kg", "1 Kg (Net Wt)", "Rs. 1190.00"),
            Triple("TATA CASHEW 200G", "200g (Net Wt)", "Rs. 310.00"),
            Triple("TATA CASHEW 500G", "500g (Net Wt)", "Rs. 680.00"),
            Triple("TATA CASHEW 1 Kg", "1 Kg (Net Wt)", "Rs. 1290.00"),
            Triple("TATA RAISINS 200G", "200g (Net Wt)", "Rs. 130.00"),
            Triple("TATA RAISINS 500G", "500g (Net Wt)", "Rs. 260.00"),
            Triple("TATA BLACK RAISINS 200G", "200g (Net Wt)", "Rs. 175.00"),
            Triple("TATA ARABIAN DATES 400G", "400g (Net Wt)", "Rs. 290.00"),
            Triple("TATA WALNUTS 200G", "200g (Net Wt)", "Rs. 390.00"),
            Triple("MOLSIS ALMOND 50G (A50)", "50g (Net Wt)", "Rs. 65.00"),
            Triple("MOLSIS CASHEW NUT 200G", "200g (Net Wt)", "Rs. 299.00"),
            Triple("MOLSIS ROYAL ALMOND 200G", "200g (Net Wt)", "Rs. 280.00"),
            Triple("MOLSIS APERO NUT MIX 100G NOT FOR SALE", "100g (Net Wt)", "SAMPLE / NFS"),
            Triple("MOLSIS JUMBO CASHEW 200G (JC200)", "200g (Net Wt)", "Rs. 350.00"),
            Triple("MOLSIS JUMBO CASHEW 200G W180 GIFT BOX", "200g (Net Wt)", "Rs. 399.00"),
            Triple("MOLSIS WHOLES CASHE 50G (C50)", "50g (Net Wt)", "Rs. 70.00"),
            Triple("MOLSIS JUMBO CASHEW 250G GIFT BOX", "250g (Net Wt)", "Rs. 440.00"),
            Triple("MOLSIS ALMOND WHITE 250G", "250g (Net Wt)", "Rs. 330.00"),
            Triple("MOLSIS ALMOND SALTED 50G(AS50G)", "50g (Net Wt)", "Rs. 65.00"),
            Triple("MOLSIS PEPPER CASHEW 50G (PC50)", "50g (Net Wt)", "Rs. 75.00"),
            Triple("MOLSIS APERO NUT MIX 50G", "50g (Net Wt)", "Rs. 60.00"),
            Triple("MOLSIS HONEY CASHEW 50G (HR50)", "50g (Net Wt)", "Rs. 80.00"),
            Triple("MOLSIS PISTA SALTED 50G (PS50)", "50g (Net Wt)", "Rs. 75.00"),
            Triple("MOLSIS CASHEW SALTED 50G (CS50)", "50g (Net Wt)", "Rs. 75.00"),
            Triple("RUNUTZ INDEPENDENCE ALMOND 250G (IA250)", "250g (Net Wt)", "Rs. 299.00"),
            Triple("RUNUTZ CASHEW 240 250G (W240)", "250g (Net Wt)", "Rs. 340.00"),
            Triple("RUNUTZ CASHEW NW 250G", "250g (Net Wt)", "Rs. 310.00"),
            Triple("RUNUTZ CASHEW LWP 250G", "250g (Net Wt)", "Rs. 270.00"),
            Triple("RUNUTZ CASHEW SWP 250G", "250g (Net Wt)", "Rs. 250.00"),
            Triple("RUNUTZ CASHEW W320 250G (W320)", "250g (Net Wt)", "Rs. 320.00"),
            Triple("RUNUTZ CASHEW W210 250G SAMPLE (W210)", "250g (Net Wt)", "Rs. 360.00"),
            Triple("RUNUTZ GREEN ALMOND JUMBO 200G GIFT BOX", "200g (Net Wt)", "Rs. 350.00"),
            Triple("RUNUTZ GREEN ROUND RAISINS 150G GIFT BOX", "150g (Net Wt)", "Rs. 190.00"),
            Triple("DAILY INDEPENDENCE ALMOND 250G (DIA)", "250g (Net Wt)", "Rs. 280.00")
        )

        p2Items.forEachIndexed { index, item ->
            val brand = when {
                item.first.startsWith("TATA") -> "Tata"
                item.first.startsWith("MOLSIS") -> "Molsis"
                item.first.startsWith("RUNUTZ") -> "Runutz"
                else -> "Daily"
            }
            val isTata = brand == "Tata"
            val isTataAlmond500 = item.first.equals("TATA ALMOND 500G", ignoreCase = true)
            val bNo = if (isTataAlmond500) "B06H2735D1" else "IP${brand.take(2).uppercase()}026"
            val mfd = if (isTata) "27/08/26" else "10/09/2026"
            val useBy = if (isTata) "26/08/27" else "09/06/2026"
            val cleanMrp = item.third.removePrefix("Rs. ").trim()
            val usp = if (isTataAlmond500) "(₹1.66/g)" else calculateUsp(item.third, item.second)
            val formatType = if (isTata) LabelFormatType.TATA_STYLE.id else if (item.first.contains("BOX", ignoreCase = true)) LabelFormatType.BOLAS_BOX.id else LabelFormatType.BOLAS_STANDARD.id
            list.add(
                DominoLabel(
                    printerBackupId = 2,
                    fileName = "${item.first}.lbl",
                    labelName = item.first,
                    brand = brand,
                    productCategory = detectCategory(item.first),
                    batchNumber = bNo,
                    mfgDate = mfd,
                    useBy = useBy,
                    expiryDate = useBy,
                    mrp = cleanMrp,
                    weightDetails = item.second.replace(" (Net Wt)", "").trim(),
                    unitSalePrice = usp,
                    rasterDropSize = "16 Drop 100mm 33",
                    associatedImage = "RS.bmp",
                    barcodeData = "8901030" + (20000 + index),
                    rawLabelContent = buildDominoRawContent(
                        name = item.first,
                        batch = bNo,
                        mfd = mfd,
                        useBy = useBy,
                        mrp = cleanMrp,
                        usp = usp,
                        weight = item.second.replace(" (Net Wt)", "").trim()
                    ),
                    printCount = (800 + index * 50).toLong(),
                    formatType = formatType,
                    printerHeadCode = "2860"
                )
            )
        }

        // ================= PRINTER 3 (Sweets, Honey, Gifts - Ax550i) =================
        val p3Items = listOf(
            Triple("UNIFLORAL TULSI HONEY 400G", "400g (Net Wt)", "Rs. 320.00"),
            Triple("UNIFLORAL TULSI HONEY 500G", "500g (Net Wt)", "Rs. 390.00"),
            Triple("UNIFLORAL TULSI HONEY 500G SAMPLE", "500g (Net Wt)", "SAMPLE / NFS"),
            Triple("MULTIFLORAL HONEY 500G", "500g (Net Wt)", "Rs. 360.00"),
            Triple("BOLAS KAJU KATLI 140G (KK140)", "140g (Net Wt)", "Rs. 175.00"),
            Triple("BOLAS KAJU KATLI 250G (KK250)", "250g (Net Wt)", "Rs. 290.00"),
            Triple("BOLAS KAJU KATLI 200G METRO (KK200)", "200g (Net Wt)", "Rs. 240.00"),
            Triple("BOLAS JAGGERY KAJU KATLI 200G SAMPLE", "200g (Net Wt)", "Rs. 250.00"),
            Triple("BOLAS MYSORE PAK 100G (MP100)", "100g (Net Wt)", "Rs. 120.00"),
            Triple("BOLAS MYSORE PAK 200G METRO", "200g (Net Wt)", "Rs. 220.00"),
            Triple("BOLAS BESAN LADOO 200G (BL200)", "200g (Net Wt)", "Rs. 190.00"),
            Triple("BOLAS BESAN LADOO 400G (BL400)", "400g (Net Wt)", "Rs. 360.00"),
            Triple("BOLAS ANJEER BARFI 110G (AB110)", "110g (Net Wt)", "Rs. 180.00"),
            Triple("BOLAS ANJEER KATLI 250G (AK250)", "250g (Net Wt)", "Rs. 340.00"),
            Triple("BOLAS ASSORTED SWEET 250G", "250g (Net Wt)", "Rs. 310.00"),
            Triple("BOLAS SHABARI ASSORTED SWEET BOX 250G", "250g (Net Wt)", "Rs. 325.00"),
            Triple("BOLAS SHABARI ASSORTED NUT BOX 300G", "300g (Net Wt)", "Rs. 420.00"),
            Triple("BOLAS SHABARI ASSORTED KAJU KATLI 250G", "250g (Net Wt)", "Rs. 310.00"),
            Triple("BOLAS SHABARI ASSORTED MYSORE PAK 250G", "250g (Net Wt)", "Rs. 260.00"),
            Triple("BOLAS KUMKUM COMBO GIFT BOX 400G", "400g (Net Wt)", "Rs. 599.00"),
            Triple("BOLAS HALDI COMBO GIFT BOX 400G", "400g (Net Wt)", "Rs. 599.00"),
            Triple("BOLAS BEST WISHES 150G (BWD150)", "150g (Net Wt)", "Rs. 299.00"),
            Triple("BOLAS BEST WISHES RED 225G (BWD225)", "225g (Net Wt)", "Rs. 399.00"),
            Triple("BOLAS 3 IN 1 GIFT BOX 240G", "240g (Net Wt)", "Rs. 450.00"),
            Triple("BOLAS SWEETS & NAMKEEN GIFT BOX 600G", "600g (Net Wt)", "Rs. 799.00"),
            Triple("BOLAS DRY FRUIT & NUTS GIFT BOX 400G KPN", "400g (Net Wt)", "Rs. 650.00"),
            Triple("BOLAS DRYFRUITS AND SWEETS BOX 400G SUDAMA METRO", "400g (Net Wt)", "Rs. 699.00"),
            Triple("BOLAS RAMA YATRA GIFT BOX 545g (RY545)", "545g (Net Wt)", "Rs. 890.00"),
            Triple("BOLAS FESTIVAL OF JOY RED 600g (FOJ600)", "600g (Net Wt)", "Rs. 950.00"),
            Triple("BOLAS FESTIVAL OF JOY RED 400g (FOJ400)", "400g (Net Wt)", "Rs. 650.00"),
            Triple("SWEETS AND STORIES FROM TULUNADU 350G", "350g (Net Wt)", "Rs. 520.00"),
            Triple("ROASTED NUTS RAMA & SQUIRREL EDITION 600G", "600g (Net Wt)", "Rs. 990.00"),
            Triple("KODAVA FOLKLORE 850G COORG", "850g (Net Wt)", "Rs. 1350.00"),
            Triple("KRISHNA SUDAMA 450G", "450g (Net Wt)", "Rs. 720.00"),
            Triple("RELIANCE RAMA DARBAR MEDIAM 720G", "720g (Net Wt)", "Rs. 1050.00"),
            Triple("RELIANCE RAMA DARBAR LARGE 1100G", "1100g (Net Wt)", "Rs. 1650.00"),
            Triple("RAMA DEEPAVALI NUTS AND SWEETS 1050G", "1050g (Net Wt)", "Rs. 1590.00"),
            Triple("VEDAKA CASHEW KERNELS BROKEN", "500g (Net Wt)", "Rs. 420.00"),
            Triple("VEDAKA ALMOND KERNELS", "500g (Net Wt)", "Rs. 490.00"),
            Triple("VEDAKA RAISINS", "500g (Net Wt)", "Rs. 220.00"),
            Triple("VEDAKA PUMPKIN SEEDS", "250g (Net Wt)", "Rs. 240.00"),
            Triple("BNPM MYSORE", "500g (Net Wt)", "Rs. 480.00")
        )

        p3Items.forEachIndexed { index, item ->
            val brand = when {
                item.first.contains("TULSI") || item.first.contains("HONEY") -> "Bolas Pure"
                item.first.startsWith("BOLAS") -> "Bolas"
                item.first.startsWith("RELIANCE") -> "Reliance"
                item.first.startsWith("VEDAKA") -> "Vedaka"
                else -> "Heritage"
            }
            val isSweets = item.first.contains("KATLI") || item.first.contains("PAK") || item.first.contains("LADOO") || item.first.contains("BARFI")
            val isHoney = item.first.contains("HONEY")
            val useBy = when {
                isHoney -> "09/09/2028"
                isSweets -> "25/09/2026"
                else -> "09/06/2026"
            }
            val bNo = "IP${brand.take(2).uppercase()}026"
            val cleanMrp = item.third.removePrefix("Rs. ").trim()
            val cleanWeight = item.second.replace(" (Net Wt)", "").trim()
            val usp = calculateUsp(cleanMrp, cleanWeight)

            val formatType = if (item.first.contains("BOX", ignoreCase = true)) LabelFormatType.BOLAS_BOX.id else LabelFormatType.BOLAS_STANDARD.id

            list.add(
                DominoLabel(
                    printerBackupId = 3,
                    fileName = "${item.first}.lbl",
                    labelName = item.first,
                    brand = brand,
                    productCategory = detectCategory(item.first),
                    batchNumber = bNo,
                    mfgDate = "10/09/2026",
                    useBy = useBy,
                    expiryDate = useBy,
                    mrp = cleanMrp,
                    weightDetails = cleanWeight,
                    unitSalePrice = usp,
                    rasterDropSize = "16 Drop (Food Grade)",
                    associatedImage = "RUPEES SYMBOL.bmp",
                    barcodeData = "8908025" + (30000 + index),
                    rawLabelContent = buildDominoRawContent(
                        name = item.first,
                        batch = bNo,
                        mfd = "10/09/2026",
                        useBy = useBy,
                        mrp = cleanMrp,
                        usp = usp,
                        weight = cleanWeight
                    ),
                    printCount = (600 + index * 40).toLong(),
                    formatType = formatType,
                    printerHeadCode = "2860"
                )
            )
        }

        return list
    }

    fun getInitialProductionLogs(): List<ProductionLog> {
        return listOf(
            ProductionLog(
                id = 1,
                printerBackupId = 1,
                labelName = "BOLAS WALNUT KERNELS 200G (WN200)",
                batchNumber = "BWN2609",
                shiftName = "Shift A (Morning 06:00 - 14:00)",
                logDate = "2026-09-11",
                startTime = "06:15",
                endTime = "13:50",
                packsPrinted = 4850,
                packsRejected = 12,
                lineSpeedMPerMin = 48.5,
                inkPressureBar = 2.89,
                viscosityCps = 4.15,
                inkLevelPercent = 86,
                makeupLevelPercent = 72,
                operatorName = "Ramesh K.",
                status = "Completed",
                notes = "Optimal line speed. No printhead clogs recorded."
            ),
            ProductionLog(
                id = 2,
                printerBackupId = 1,
                labelName = "BOLAS PISTA SALTED 200G(PS200)",
                batchNumber = "BPS2609",
                shiftName = "Shift B (Evening 14:00 - 22:00)",
                logDate = "2026-09-11",
                startTime = "14:10",
                endTime = "21:40",
                packsPrinted = 4320,
                packsRejected = 18,
                lineSpeedMPerMin = 45.0,
                inkPressureBar = 2.92,
                viscosityCps = 4.20,
                inkLevelPercent = 82,
                makeupLevelPercent = 68,
                operatorName = "Suresh M.",
                status = "Completed",
                notes = "Shift run completed. Quality check verified MRP and expiry."
            ),
            ProductionLog(
                id = 3,
                printerBackupId = 1,
                labelName = "BOLAS CASHEW NUT 250G (CN250)",
                batchNumber = "BCN2609A10",
                shiftName = "Shift C (Night 22:00 - 06:00)",
                logDate = "2026-09-10",
                startTime = "22:15",
                endTime = "05:45",
                packsPrinted = 3980,
                packsRejected = 8,
                lineSpeedMPerMin = 42.0,
                inkPressureBar = 2.85,
                viscosityCps = 4.10,
                inkLevelPercent = 88,
                makeupLevelPercent = 75,
                operatorName = "Anil V.",
                status = "Completed",
                notes = "Smooth overnight run."
            ),
            ProductionLog(
                id = 4,
                printerBackupId = 2,
                labelName = "TATA PISTACHIOS 200G",
                batchNumber = "TA2609B1",
                shiftName = "Shift A (Morning 06:00 - 14:00)",
                logDate = "2026-09-11",
                startTime = "06:30",
                endTime = "13:30",
                packsPrinted = 3500,
                packsRejected = 5,
                lineSpeedMPerMin = 38.0,
                inkPressureBar = 2.82,
                viscosityCps = 3.95,
                inkLevelPercent = 91,
                makeupLevelPercent = 83,
                operatorName = "Gopal R.",
                status = "Completed",
                notes = "Tata retail specs verified. Barcode scan 100% readable."
            ),
            ProductionLog(
                id = 5,
                printerBackupId = 2,
                labelName = "MOLSIS JUMBO CASHEW 200G (JC200)",
                batchNumber = "MO2609B18",
                shiftName = "Shift B (Evening 14:00 - 22:00)",
                logDate = "2026-09-11",
                startTime = "14:15",
                endTime = "21:00",
                packsPrinted = 2950,
                packsRejected = 14,
                lineSpeedMPerMin = 36.5,
                inkPressureBar = 2.88,
                viscosityCps = 4.05,
                inkLevelPercent = 78,
                makeupLevelPercent = 70,
                operatorName = "Devraj N.",
                status = "Completed",
                notes = "Auto gutter wash performed before start."
            ),
            ProductionLog(
                id = 6,
                printerBackupId = 3,
                labelName = "BOLAS KAJU KATLI 250G (KK250)",
                batchNumber = "SW2609C6",
                shiftName = "Shift A (Morning 06:00 - 14:00)",
                logDate = "2026-09-11",
                startTime = "07:00",
                endTime = "12:30",
                packsPrinted = 1850,
                packsRejected = 4,
                lineSpeedMPerMin = 28.0,
                inkPressureBar = 2.95,
                viscosityCps = 4.30,
                inkLevelPercent = 94,
                makeupLevelPercent = 88,
                operatorName = "Pradeep S.",
                status = "Completed",
                notes = "Fresh sweet packaging. 15-day shelf life verified on label print."
            ),
            ProductionLog(
                id = 7,
                printerBackupId = 3,
                labelName = "UNIFLORAL TULSI HONEY 500G",
                batchNumber = "SW2609C2",
                shiftName = "Shift B (Evening 14:00 - 22:00)",
                logDate = "2026-09-10",
                startTime = "14:30",
                endTime = "20:45",
                packsPrinted = 2400,
                packsRejected = 9,
                lineSpeedMPerMin = 32.0,
                inkPressureBar = 2.90,
                viscosityCps = 4.25,
                inkLevelPercent = 89,
                makeupLevelPercent = 81,
                operatorName = "Mahesh T.",
                status = "Completed",
                notes = "Glass jar printing with food grade ink."
            )
        )
    }

    private fun detectCategory(name: String): String {
        val upper = name.uppercase()
        return when {
            upper.contains("WALNUT") -> "Walnuts"
            upper.contains("PISTA") -> "Pistachios"
            upper.contains("CASHEW") || upper.contains("CAASHEW") || upper.contains("CAEHEW") -> "Cashews"
            upper.contains("ALMOND") -> "Almonds"
            upper.contains("HONEY") -> "Honey"
            upper.contains("KATLI") || upper.contains("PAK") || upper.contains("LADOO") || upper.contains("BARFI") || upper.contains("SWEET") -> "Sweets"
            upper.contains("GIFT") || upper.contains("BOX") || upper.contains("FESTIVAL") -> "Gift Boxes"
            upper.contains("MAKHANA") -> "Makhana"
            upper.contains("SUNFLOWER") || upper.contains("PUMPKIN") || upper.contains("SEED") -> "Seeds"
            upper.contains("RAISIN") || upper.contains("DATES") -> "Raisins & Dates"
            else -> "Dry Fruits"
        }
    }

    fun calculateUsp(mrpStr: String, weightStr: String): String {
        val mrpNum = mrpStr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return ""
        val wtNum = weightStr.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: return ""
        val isKg = weightStr.contains("kg", ignoreCase = true)
        val wtG = if (isKg) wtNum * 1000.0 else wtNum
        return if (wtG > 0 && mrpNum > 0) {
            val perG = mrpNum / wtG
            String.format(java.util.Locale.US, "(USP ₹ %.2f/g)", perG)
        } else {
            ""
        }
    }

    private fun buildDominoRawContent(
        name: String,
        batch: String,
        mfd: String,
        useBy: String,
        mrp: String,
        usp: String = "",
        weight: String
    ): String {
        val mrpLine = if (usp.isNotBlank()) "$mrp $usp (INCL. OF ALL TAXES)" else "$mrp (INCL. OF ALL TAXES)"
        return """
            [DOMINO Ax FORMAT V5.4]
            ITEM        : $name
            BATCH NO    : $batch
            DATE OF MFG : $mfd
            USE BY      : $useBy
            MRP         : $mrpLine
            FOR NET WT  : $weight
            RASTER      : 16-Drop FontMatrix
            STROKE      : 1.2ms | DELAY: 24ms
        """.trimIndent()
    }
}
