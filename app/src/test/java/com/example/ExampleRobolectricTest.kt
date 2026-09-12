package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DominoBackupParser
import com.example.data.local.DominoSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Domino Ax Reader", appName)
  }

  @Test
  fun `verify seed data contains realistic backups labels and logs`() {
    val backups = DominoSeedData.getInitialBackups()
    assertTrue(backups.isNotEmpty())
    assertEquals("Ax350i", backups[0].printerModel)

    val labels = DominoSeedData.getInitialLabels()
    assertTrue(labels.isNotEmpty())

    // Check first label extraction fields requested by user
    val sampleLabel = labels.first()
    assertNotNull(sampleLabel.batchNumber)
    assertNotNull(sampleLabel.mfgDate)
    assertNotNull(sampleLabel.expiryDate)
    assertNotNull(sampleLabel.useBy)
    assertNotNull(sampleLabel.mrp)
    assertNotNull(sampleLabel.weightDetails)
    assertTrue(sampleLabel.weightDetails.contains("g") || sampleLabel.weightDetails.contains("Kg"))

    val logs = DominoSeedData.getInitialProductionLogs()
    assertTrue(logs.isNotEmpty())
    assertTrue(logs.first().packsPrinted > 0)
  }

  @Test
  fun `verify label detail extraction logic from filename`() {
    val label = DominoBackupParser.extractLabelDetails("BOLAS CHILLI CASHEWS 200G (B88)", 0, 1L)
    assertEquals("Bolas", label.brand)
    assertTrue(label.weightDetails.contains("200G"))
    assertEquals("BB88", label.batchNumber)
    assertEquals("Cashews", label.productCategory)
    assertTrue(label.mrp.isNotEmpty())
    assertNotNull(label.mfgDate)
    assertNotNull(label.expiryDate)
    assertNotNull(label.useBy)
  }
}

