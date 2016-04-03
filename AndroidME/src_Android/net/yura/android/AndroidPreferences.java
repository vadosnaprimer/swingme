package net.yura.android;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import android.content.SharedPreferences;
import android.os.Build;

public class AndroidPreferences extends Preferences {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    
    public AndroidPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String get(String key, String deflt) {
        return preferences.getString(key, deflt);
    }

    @Override
    public boolean getBoolean(String key, boolean deflt) {
        return preferences.getBoolean(key, deflt);
    }

    @Override
    public float getFloat(String key, float deflt) {
        return preferences.getFloat(key, deflt);
    }

    @Override
    public int getInt(String key, int deflt) {
        return preferences.getInt(key, deflt);
    }

    @Override
    public long getLong(String key, long deflt) {
        return preferences.getLong(key, deflt);
    }

    @Override
    public boolean nodeExists(String path) throws BackingStoreException {
        return preferences.contains(path);
    }

    @Override
    public String[] keys() throws BackingStoreException {
        Set<String> keys = preferences.getAll().keySet();
        return keys.toArray( new String[keys.size()] );
    }

    @Override
    public String toString() {
        return preferences.toString();
    }

    private SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = preferences.edit();
        }
        return editor;
    }

    @Override
    public void put(String key, String value) {
        getEditor().putString(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        getEditor().putFloat(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        getEditor().putInt(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        getEditor().putLong(key, value);
    }

    @Override
    public void flush() throws BackingStoreException {
        if (editor != null) {
            SharedPreferences.Editor theEditor = editor;
            editor = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                theEditor.apply();
            } else {
                if (!theEditor.commit()) {
                    throw new BackingStoreException("not able to save");
                }
            }
        }
    }







    @Override
    public byte[] getByteArray(String key, byte[] deflt) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public double getDouble(String key, double deflt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String absolutePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNodeChangeListener(NodeChangeListener ncl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] childrenNames() throws BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() throws BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exportNode(OutputStream ostream) throws IOException, BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exportSubtree(OutputStream ostream) throws IOException, BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUserNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String name() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Preferences node(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Preferences parent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putByteArray(String key, byte[] value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putDouble(String key, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNode() throws BackingStoreException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNodeChangeListener(NodeChangeListener ncl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sync() throws BackingStoreException {
        throw new UnsupportedOperationException();
    }

}
