package com.syc.world

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.SmallTitle

@Composable
fun Dynamic(navController: NavController,postId: Int) {
    SmallTitle(postId.toString())
}
