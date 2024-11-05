package umc.everyones.lck.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import umc.everyones.lck.data.datasource.MypageDataSource
import umc.everyones.lck.data.datasourceImpl.mypage.MypageDataSourceImpl
import umc.everyones.lck.data.service.MypageService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    @Provides
    fun provideMypageDataSource(mypageService: MypageService): MypageDataSource = MypageDataSourceImpl(mypageService)

}