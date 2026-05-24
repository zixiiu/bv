package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.entity.FavoriteFolderData
import dev.aaa1115910.biliapi.entity.FavoriteFolderMetadata
import dev.aaa1115910.biliapi.entity.FavoriteItemType
import dev.aaa1115910.biliapi.http.BiliHttpApi
import org.koin.core.annotation.Single

@Single
class FavoriteRepository(
    private val authRepository: AuthRepository
) {
    suspend fun checkVideoFavoured(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ): Boolean {
        return preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "checkVideoFavoured(aid=$aid)",
            web = {
            BiliHttpApi.checkVideoFavoured(
                avid = aid,
                sessData = authRepository.sessionData ?: ""
            )
        },
            app = {
            BiliHttpApi.checkVideoFavoured(
                avid = aid,
                accessKey = authRepository.accessToken ?: ""
            )
        })
    }

    suspend fun addVideoToFavoriteFolder(
        aid: Long,
        addMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "addVideoToFavoriteFolder(aid=$aid)",
            web = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )
        },
            app = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                accessKey = authRepository.accessToken
            )
        })
    }

    suspend fun delVideoFromFavoriteFolder(
        aid: Long,
        delMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "delVideoFromFavoriteFolder(aid=$aid)",
            web = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                delMediaIds = delMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )
        },
            app = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                delMediaIds = delMediaIds,
                accessKey = authRepository.accessToken
            )
        })
    }

    suspend fun updateVideoToFavoriteFolder(
        aid: Long,
        addMediaIds: List<Long>,
        delMediaIds: List<Long>,
        preferApiType: ApiType = ApiType.Web
    ) {
        preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "updateVideoToFavoriteFolder(aid=$aid)",
            web = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )
        },
            app = {
            BiliHttpApi.setVideoToFavorite(
                avid = aid,
                type = FavoriteItemType.Video.value,
                addMediaIds = addMediaIds,
                delMediaIds = delMediaIds,
                accessKey = authRepository.accessToken
            )
        })
    }

    suspend fun getAllFavoriteFolderMetadataList(
        mid: Long,
        type: FavoriteItemType = FavoriteItemType.Video,
        rid: Long? = null,
        preferApiType: ApiType = ApiType.Web
    ): List<FavoriteFolderMetadata> {
        val userFavoriteFoldersData = preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "getAllFavoriteFolderMetadataList(mid=$mid)",
            web = {
            BiliHttpApi.getAllFavoriteFoldersInfo(
                mid = mid,
                type = type.value,
                rid = rid,
                sessData = authRepository.sessionData ?: ""
            )
        },
            app = {
            BiliHttpApi.getAllFavoriteFoldersInfo(
                mid = mid,
                type = type.value,
                rid = rid,
                accessKey = authRepository.accessToken ?: ""
            )
        }).getResponseData()
        return userFavoriteFoldersData.list.map {
            FavoriteFolderMetadata.fromHttpUserFavoriteFolder(it)
        }
    }

    suspend fun getFavoriteFolderData(
        mediaId: Long,
        pageSize: Int = 20,
        pageNumber: Int = 1,
        preferApiType: ApiType = ApiType.Web
    ): FavoriteFolderData {
        val favoriteFolderListData = preferApiOrFallbackToApp(
            preferApiType = preferApiType,
            operation = "getFavoriteFolderData(mediaId=$mediaId)",
            web = {
            BiliHttpApi.getFavoriteList(
                mediaId = mediaId,
                pageSize = pageSize,
                pageNumber = pageNumber,
                sessData = authRepository.sessionData ?: ""
            )
        },
            app = {
            BiliHttpApi.getFavoriteList(
                mediaId = mediaId,
                pageSize = pageSize,
                pageNumber = pageNumber,
                accessKey = authRepository.accessToken ?: ""
            )
        }).getResponseData()
        return FavoriteFolderData.fromHttpFavoriteFolderInfoListData(favoriteFolderListData)
    }
}
