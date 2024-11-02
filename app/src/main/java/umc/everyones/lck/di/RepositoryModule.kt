package umc.everyones.lck.di

import android.app.Application
import android.content.Context
import umc.everyones.lck.domain.repository.NaverRepository
import umc.everyones.lck.data.repositoryImpl.NaverRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.scopes.ViewModelScoped
import umc.everyones.lck.data.repositoryImpl.HomeRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.TestRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.TodayMatchRepositoryImpl
import umc.everyones.lck.data.datasource.login.LoginDataSource
import umc.everyones.lck.data.datasourceImpl.LoginDataSourceImpl
import umc.everyones.lck.data.repositoryImpl.mypage.MypageRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.about_lck.AboutLckRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.community.CommunityRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.login.LoginRepositoryImpl
import umc.everyones.lck.data.repositoryImpl.party.ViewingPartyRepositoryImpl
import umc.everyones.lck.data.service.MypageService
import umc.everyones.lck.data.service.LoginService
import umc.everyones.lck.data.service.NaverService
import umc.everyones.lck.data.service.TestService
import umc.everyones.lck.data.service.party.ViewingPartyService
import umc.everyones.lck.domain.repository.MypageRepository
import umc.everyones.lck.domain.repository.TestRepository
import umc.everyones.lck.domain.repository.home.HomeRepository
import umc.everyones.lck.domain.repository.match.TodayMatchRepository
import umc.everyones.lck.domain.repository.about_lck.AboutLckRepository
import umc.everyones.lck.domain.repository.community.CommunityRepository
import umc.everyones.lck.domain.repository.login.LoginRepository
import umc.everyones.lck.domain.repository.party.ViewingPartyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationContextModule {
    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context = application
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun providesTestRepository(
        testService: TestService
    ): TestRepository = TestRepositoryImpl(testService)

    @Singleton
    @Provides
    fun providesTodayMatchRepository(
        todayMatchRepositoryImpl: TodayMatchRepositoryImpl
    ): TodayMatchRepository = todayMatchRepositoryImpl

    @Singleton
    @Provides
    fun providesHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository = homeRepositoryImpl

    @Provides
    @Singleton
    fun providesNaverRepository(
        naverRepositoryImpl: NaverRepositoryImpl
    ): NaverRepository = naverRepositoryImpl

    @Singleton
    @Provides
    fun providesLoginRepository(loginDataSource: LoginDataSource): LoginRepository =
        LoginRepositoryImpl(loginDataSource) // LoginDataSource를 주입
    @Singleton
    @Provides
    fun providesViewingPartyRepository(
        viewingPartyRepositoryImpl: ViewingPartyRepositoryImpl
    ): ViewingPartyRepository = viewingPartyRepositoryImpl

    @Singleton
    @Provides
    fun provideMypageRepository(
        mypageRepositoryImpl: MypageRepositoryImpl
    ): MypageRepository = mypageRepositoryImpl


    @Singleton
    @Provides
    fun providesCommunityRepository(
        communityRepositoryImpl: CommunityRepositoryImpl
    ): CommunityRepository = communityRepositoryImpl

    @Singleton
    @Provides
    fun providesAboutLckRepository(
        aboutLckRepositoryImpl: AboutLckRepositoryImpl
    ): AboutLckRepository = aboutLckRepositoryImpl
}
