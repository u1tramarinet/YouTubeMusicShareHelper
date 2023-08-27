package com.u1tramarinet.youtubemusicsharehelper.di;

import android.content.Context;

import com.u1tramarinet.youtubemusicsharehelper.infrastructure.history.SharedPreferencesHistoryDataSource;
import com.u1tramarinet.youtubemusicsharehelper.model.MainRepository;
import com.u1tramarinet.youtubemusicsharehelper.model.history.HistoryDataSource;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
abstract class RepositoryModule {
    @Binds
    abstract HistoryDataSource bindLocalHistoryDataSource(SharedPreferencesHistoryDataSource dataSource);
}

@Module
@InstallIn(SingletonComponent.class)
class MainModules {
    @Provides
    static SharedPreferencesHistoryDataSource provideSharedPreferencesHistoryDataSource(@ApplicationContext Context context) {
        return new SharedPreferencesHistoryDataSource(context);
    }

    @Provides
    static MainRepository provideMainRepository() {
        return new MainRepository();
    }
}
