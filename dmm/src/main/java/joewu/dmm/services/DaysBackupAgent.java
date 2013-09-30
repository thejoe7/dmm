package joewu.dmm.services;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by joe7wu on 2013-06-04.
 */
public class DaysBackupAgent extends BackupAgentHelper {
    static final String PREFS_FILENAME = "joewu.dmm_preferences";

    static final String PREFS_BACKUP_KEY = "preferences_key";

    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS_FILENAME);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
