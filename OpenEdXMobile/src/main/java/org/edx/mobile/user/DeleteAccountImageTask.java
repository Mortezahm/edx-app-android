package org.edx.mobile.user;

import android.content.Context;

import androidx.annotation.NonNull;

import org.edx.mobile.core.EdxDefaultModule;
import org.edx.mobile.event.ProfilePhotoUpdatedEvent;
import org.edx.mobile.module.prefs.LoginPrefs;
import org.edx.mobile.task.Task;

import java.io.IOException;

import dagger.hilt.android.EntryPointAccessors;
import de.greenrobot.event.EventBus;

public class DeleteAccountImageTask extends
        Task<Void> {

    UserService userService;
    LoginPrefs loginPrefs;

    @NonNull
    private final String username;

    public DeleteAccountImageTask(@NonNull Context context, @NonNull String username) {
        super(context);
        this.username = username;
        userService = EntryPointAccessors
                .fromApplication(context, EdxDefaultModule.ProviderEntryPoint.class)
                .getUserService();
        loginPrefs = EntryPointAccessors
                .fromApplication(context, EdxDefaultModule.ProviderEntryPoint.class)
                .getLoginPrefs();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            userService.deleteProfileImage(username).execute();
        } catch (IOException e) {
            e.printStackTrace();
            handleException(e);
            logger.error(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void response) {
        super.onPostExecute(response);
        EventBus.getDefault().post(new ProfilePhotoUpdatedEvent(username, null));
        // Delete the logged in user's ProfileImage
        loginPrefs.setProfileImage(username, null);
    }

    @Override
    public void onException(Exception ex) {
        // nothing to do
    }
}
