package com.hb.tm.di.module.sub;


import com.hb.lib.data.IDataManager;
import com.hb.tm.data.DataManager;
import com.hb.tm.data.repository.SystemRepository;
import com.hb.tm.data.store.system.SystemLocalStorage;
import com.hb.tm.data.store.system.SystemRepositoryImpl;
import com.hb.tm.data.store.system.SystemStore;
import com.hb.tm.di.scope.CustomScope;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by buihai on 8/8/17.
 */
@Module
public class SystemModule {


    @Provides
    @CustomScope
    SystemStore.LocalStorage provideLocalStorage(IDataManager dm) {
        return new SystemLocalStorage((DataManager) dm);
    }

    @Provides
    @CustomScope
    SystemStore.RequestService provideRequestService(Retrofit retrofit) {
        return retrofit.create(SystemStore.RequestService.class);
    }

    @Provides
    @CustomScope
    SystemRepository provideRepository(SystemStore.LocalStorage storage,
                                       SystemStore.RequestService service) {
        return new SystemRepositoryImpl(storage, service);
    }


}
