package umc.everyones.lck.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import umc.everyones.lck.data.datasource.HomeDataSource
import umc.everyones.lck.data.datasource.TodayMatchDataSource
import umc.everyones.lck.data.datasourceImpl.HomeDataSourceImpl
import umc.everyones.lck.data.datasourceImpl.TodayMatchDataSourceImpl
import umc.everyones.lck.data.datasource.AboutLckDataSource
import umc.everyones.lck.data.datasource.NaverDataSource
import umc.everyones.lck.data.datasource.ViewingPartyDataSource
import umc.everyones.lck.data.datasource.community.CommunityDataSource
import umc.everyones.lck.data.datasource.login.LoginDataSource
import umc.everyones.lck.data.datasourceImpl.AboutLckDataSourceImpl
import umc.everyones.lck.data.datasourceImpl.LoginDataSourceImpl
import umc.everyones.lck.data.datasourceImpl.community.CommunityDataSourceImpl
import umc.everyones.lck.data.datasourceImpl.naver.NaverDataSourceImpl
import umc.everyones.lck.data.datasourceImpl.party.ViewingPartyDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideTodayMatchDataSource(todayMatchDataSourceImpl: TodayMatchDataSourceImpl): TodayMatchDataSource =
        todayMatchDataSourceImpl
    @Provides
    @Singleton
    fun provideHomeDataSource(homeDataSourceImpl: HomeDataSourceImpl): HomeDataSource =
        homeDataSourceImpl

    @Provides
    @Singleton
    fun provideViewingPartyDataSource(viewingPartyDataSourceImpl: ViewingPartyDataSourceImpl): ViewingPartyDataSource =
        viewingPartyDataSourceImpl

    @Provides
    @Singleton
    fun provideNaverDataSource(naverDataSourceImpl: NaverDataSourceImpl): NaverDataSource =
        naverDataSourceImpl

    @Provides
    @Singleton
    fun provideCommunityDataSource(communityDataSourceImpl: CommunityDataSourceImpl): CommunityDataSource =
        communityDataSourceImpl

    @Provides
    @Singleton
    fun provideAboutLckDataSource(aboutLckDataSourceImpl: AboutLckDataSourceImpl): AboutLckDataSource =
        aboutLckDataSourceImpl

    @Provides
    @Singleton
    fun provideLoginDataSource(loginDataSourceImpl: LoginDataSourceImpl): LoginDataSource =
        loginDataSourceImpl

}