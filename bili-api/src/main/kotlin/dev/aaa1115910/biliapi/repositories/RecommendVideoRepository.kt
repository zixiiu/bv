package dev.aaa1115910.biliapi.repositories

import bilibili.app.show.v1.PopularGrpcKt
import bilibili.app.show.v1.popularResultReq
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.home.RecommendData
import dev.aaa1115910.biliapi.entity.home.RecommendPage
import dev.aaa1115910.biliapi.entity.rank.PopularVideoData
import dev.aaa1115910.biliapi.entity.rank.PopularVideoPage
import dev.aaa1115910.biliapi.entity.ugc.UgcItem
import dev.aaa1115910.biliapi.http.BiliHttpApi
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single

@Single
class RecommendVideoRepository(
    private val authRepository: AuthRepository,
    private val channelRepository: ChannelRepository
) {
    private val popularStub
        get() = runCatching {
            PopularGrpcKt.PopularCoroutineStub(channelRepository.defaultChannel!!)
        }.getOrNull()

    suspend fun getPopularVideos(
        page: PopularVideoPage,
        preferApiType: ApiType = ApiType.Web
    ): PopularVideoData {
        if (preferApiType == ApiType.App && page.nextAppIndex == -1) {
            return PopularVideoData(
                list = emptyList(),
                nextPage = page,
                noMore = true
            )
        }
        return preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "getPopularVideos(page=$page)",
            web = {
            val response = BiliHttpApi.getPopularVideoData(
                pageSize = page.nextWebPageSize,
                pageNumber = page.nextWebPageNumber,
                sessData = authRepository.sessionData ?: ""
            ).getResponseData()
            val list = response.list.map { UgcItem.fromVideoInfo(it) }
            val nextPage = PopularVideoPage(
                nextWebPageSize = page.nextWebPageSize,
                nextWebPageNumber = page.nextWebPageNumber + 1
            )
            PopularVideoData(
                list = list,
                nextPage = nextPage,
                noMore = response.noMore
            )
        },
            app = {
            val request = popularResultReq {
                idx = page.nextAppIndex.toLong()
                if (page.nextAppIndex == 0) {
                    loginEvent = if (authRepository.accessToken.isNullOrBlank()) 1 else 2
                }
                if (page.nextAppLastParam.isNotBlank()) {
                    lastParam = page.nextAppLastParam
                }
                if (page.nextAppVer.isNotBlank()) {
                    ver = page.nextAppVer
                }
            }
            val reply = runCatching {
                popularStub?.index(request)
            }.recoverCatching { error ->
                if (page.nextAppIndex <= 0 || !error.isPopularPagingRateLimited()) {
                    throw error
                }
                delay(500)
                popularStub?.index(request)
            }.getOrElse { error ->
                if (page.nextAppIndex > 0 && error.isPopularPagingRateLimited()) {
                    return@preferApiOrFallbackToApp PopularVideoData(
                        list = emptyList(),
                        nextPage = page.copy(nextAppIndex = -1),
                        noMore = true
                    )
                }
                throw error
            }
            val list = reply?.itemsList
                ?.filter { it.itemCase == bilibili.app.card.v1.Card.ItemCase.SMALL_COVER_V5 }
                ?.map { UgcItem.fromSmallCoverV5(it.smallCoverV5) }
                ?: emptyList()
            val lastCard = reply?.itemsList
                ?.lastOrNull { it.itemCase == bilibili.app.card.v1.Card.ItemCase.SMALL_COVER_V5 }
                ?.smallCoverV5
            val nextPage = PopularVideoPage(
                nextAppIndex = list.lastOrNull()?.idx ?: -1,
                nextAppLastParam = lastCard?.base?.param ?: "",
                nextAppVer = reply?.ver ?: ""
            )
            PopularVideoData(
                list = list,
                nextPage = nextPage,
                noMore = nextPage.nextAppIndex == -1
            )
        })
    }

    suspend fun getRecommendVideos(
        page: RecommendPage = RecommendPage(),
        preferApiType: ApiType = ApiType.Web
    ): RecommendData {
        return preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "getRecommendVideos(page=$page)",
            web = {
            val items = BiliHttpApi.getFeedRcmd(
                idx = page.nextWebIdx,
                sessData = authRepository.sessionData
            ).getResponseData().item
                .map { UgcItem.fromRcmdItem(it) }
            RecommendData(
                items = items,
                nextPage = RecommendPage(nextWebIdx = page.nextWebIdx + 1)
            )
        },
            app = {
            val items = BiliHttpApi.getFeedIndex(
                idx = page.nextAppIdx,
                accessKey = authRepository.accessToken
            ).getResponseData().items
                .filter { it.cardGoto == "av" }
                .map { UgcItem.fromRcmdItem(it) }
            RecommendData(
                items = items,
                nextPage = RecommendPage(
                    nextAppIdx = items.firstOrNull()?.idx?.plus(1) ?: page.nextAppIdx + 1
                )
            )
        })
    }
}

private fun Throwable.isPopularPagingRateLimited(): Boolean {
    return message?.contains("78000") == true
}
