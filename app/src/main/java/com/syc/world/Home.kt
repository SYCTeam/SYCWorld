package com.syc.world

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun Home(topAppBarScrollBehavior: ScrollBehavior,
         padding: PaddingValues,
         navController: NavController
) {
    Scaffold() {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                LatestContentShow()
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 65.dp
                    )
                )
            }
        }
    }
}

@Composable
fun HeadlineInLargePrint(headline: String) {
    Row(
        modifier = Modifier
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalDivider(
            modifier = Modifier
                .padding(end = 10.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(18.dp)),
            thickness = 8.dp,
            color = Color.LightGray
        )
        Text(
            text = headline,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun LatestContentShow() {
    Box(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            HeadlineInLargePrint(headline = "最新动态")
            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        // TODO
                    }
            ) {
                Box(
                    modifier = Modifier

                ) {
                    OutlinedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier,
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(10.dp),
                                painter = painterResource(id = R.drawable.point_green),
                                contentDescription = null
                            )
                            Text(
                                text = "沉莫",
                                modifier = Modifier
                                    .padding(10.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = TextStyle(fontStyle = FontStyle.Normal)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...",
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontStyle = FontStyle.Normal),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            var isThumbsUp by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isThumbsUp) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                            .clickable {
                                isThumbsUp = !isThumbsUp
                            },
                        painter = painterResource(id = R.drawable.thumbs_up),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                            .clickable {
                                isThumbsUp = !isThumbsUp
                            },
                        painter = painterResource(id = R.drawable.thumbs_up),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
