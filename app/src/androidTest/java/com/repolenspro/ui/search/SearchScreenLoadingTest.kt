package com.repolenspro.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class SearchScreenLoadingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun when_stateIsLoading_circularProgressIndicator_is_displayed() {

        composeTestRule.setContent {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    // ટેસ્ટિંગમાં શોધવા માટે ટેગ આપવો જરૂરી છે
                    modifier = androidx.compose.ui.Modifier.testTag("loading_spinner")
                )
            }
        }

        // ૨. ચેક કરો કે લોડિંગ બાર સ્ક્રીન પર હાજર છે કે નહિ
        composeTestRule.onNodeWithTag("loading_spinner").assertIsDisplayed()
    }
}