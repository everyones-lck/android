package umc.everyones.lck.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import umc.everyones.lck.data.service.LoginService
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.data.service.NaverService
import umc.everyones.lck.data.service.TestService
import umc.everyones.lck.data.service.home.HomeService
import umc.everyones.lck.data.service.match.TodayMatchService
import umc.everyones.lck.data.service.about_lck.AboutLckService
import umc.everyones.lck.data.service.community.CommunityService
import umc.everyones.lck.data.service.party.ViewingPartyService
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    private inline fun <reified T> Retrofit.buildService(): T {
        return this.create(T::class.java)
    }

    @Provides
    @Singleton
    fun provideTestService(retrofit: Retrofit): TestService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideNaverService(@Named("naver") retrofit: Retrofit): NaverService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideTodayMatchService(retrofit: Retrofit): TodayMatchService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideViewingPartyService(retrofit: Retrofit): ViewingPartyService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideCommunityService(retrofit: Retrofit): CommunityService {
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun provideAboutLckService(retrofit: Retrofit): AboutLckService{
        return retrofit.buildService()
    }

    @Provides
    @Singleton
    fun providesLoginService(@Named("refresh") retrofit: Retrofit): LoginService {
        return retrofit.create(LoginService::class.java)
    }

    @Provides
    @Singleton
    fun providesMyPageService(retrofit: Retrofit): MypageService {
        return retrofit.create(MypageService::class.java)
    }
}