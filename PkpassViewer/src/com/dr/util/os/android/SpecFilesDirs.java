package com.dr.util.os.android;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * 
 * Facade for retrieving special android files and directories. For some methods
 * you must init shared instance.
 * 
 * 
 * @author Verma Rahul
 * 
 */
public final class SpecFilesDirs {

	private volatile static SpecFilesDirs sharedInstance;

	private Context appContext;

	private SpecFilesDirs(Context appContext) {
		this.appContext = appContext.getApplicationContext();
	}

	public static File getExternalDCIM() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	}

	public static File getExternalPictures() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}

	public static File getExternalDownloads() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	}

	public static File getExternalAlarms() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
	}

	/**
	 * Default external directory for music
	 * 
	 * @return
	 */
	public static File getExternalMusic() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
	}

	/**
	 * Default external directory for ringtones.
	 * 
	 * @return
	 */
	public static File getExternalRingtones() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
	}

	/**
	 * Default external directory for movies.
	 * 
	 * @return
	 */
	public static File getExternalMovies() {
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
	}

	/**
	 * Root directory of android device (usually "/").
	 * 
	 * @return root directory of android device
	 */
	public static File getRoot() {
		return Environment.getRootDirectory();
	}

	/**
	 * Get db file created by openOrCreateDatabase.
	 * 
	 * @param db_filename
	 * @return
	 */
	public static File getDb(String db_filename) {
		return sharedInstance.appContext.getDatabasePath(db_filename);
	}

	/**
	 * Full path to a directory assigned to the package for its persistent data.
	 * 
	 * @return
	 */
	public static File getAppDir() {
		return new File(sharedInstance.appContext.getApplicationInfo().dataDir);
	}

	/**
	 * Returns the absolute path to the directory on the file system where files
	 * created with context.openFileOutput are stored.
	 * 
	 * @return
	 */
	public static File getFilesDir() {
		return sharedInstance.appContext.getFilesDir();
	}

	/**
	 * 
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getOtherAppDataDir(String packageName)
			throws NameNotFoundException {
		return sharedInstance.appContext.getPackageManager().getPackageInfo(
				packageName, 0).applicationInfo.dataDir;
	}

	/**
	 * Returns the absolute path to the application specific cache directory on
	 * the filesystem. These files will be ones that get deleted first when the
	 * device runs low on storage. There is no guarantee when these files will
	 * be deleted.
	 * 
	 * <strong>Note: you should not <em>rely</em> on the system deleting these
	 * files for you; you should always have a reasonable maximum, such as 1 MB,
	 * for the amount of space you consume with cache files, and prune those
	 * files when exceeding that space.</strong>
	 * 
	 * @return
	 */
	public static File getCacheDir() {
		return sharedInstance.appContext.getCacheDir();

	}

	/**
	 * 
	 * @param appContext
	 *            non null for first call.
	 * @return
	 */
	public static SpecFilesDirs getSharedInstance(Context appContext) {
		if (sharedInstance == null) {
			synchronized (SpecFilesDirs.class) {
				if (sharedInstance == null) {
					if (appContext == null) {
						throw new IllegalArgumentException(
								"For first call argument must have non null value.");
					}

					sharedInstance = new SpecFilesDirs(appContext);
				}
			}
		}

		return sharedInstance;
	}

}
