package com.dr.pkpassviewer.managers;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.dr.pkpassviewer.entities.Pkpass;
import com.dr.util.java.FileUtil;
import com.dr.util.java.FilenameFilterExt;
import com.dr.util.os.android.SpecFilesDirs;
import java.io.InputStream;

/**
 * 
 * @author Verma Rahul
 * 
 */
public class PkpassManager {

	public static interface PkpassManagerListener {
		/**
		 * 
		 * @param pkpassManager
		 * @param item
		 *            null if refreshed
		 */
		public void onDatasetChanged(PkpassManager pkpassManager, Pkpass item);
	}

	protected volatile static PkpassManager sharedInstance;

	protected final File dir;
	protected Map<File, Pkpass> map = new Hashtable<>();
	protected List<PkpassManagerListener> listeners = new Vector<>();

	public PkpassManager(File dir) {
		this.dir = dir;

		if (dir == null || (!dir.exists() && !dir.mkdirs())) {
			throw new RuntimeException("Can not create PkpassManager for dir="
					+ dir);
		}

		init();
	}

	protected void init() {
		map = new Hashtable<>();
		listeners = new Vector<>();
	}

	// =======================================================

	/**
	 * Add listener. You must remove listener after using.
	 * 
	 * @param l
	 */
	public void addListener(PkpassManagerListener l) {
		listeners.add(l);
	}

	/**
	 * 
	 * @param l
	 */
	public void removeListener(PkpassManagerListener l) {
		listeners.remove(l);
	}

	protected void dispatchDatasetChanged(Pkpass item) {
		synchronized (listeners) {
			for (PkpassManagerListener l : listeners) {
				l.onDatasetChanged(this, item);
			}
		}
	}

	// =======================================================

	/**
	 * Reload from disk items if necessary.
	 */
	public void refresh() {
		boolean isChanged = false;
		synchronized (this) {
			File[] files = dir.listFiles(new FilenameFilterExt(".pkpass"));
			Pkpass item;
			HashSet<File> oldkeys = new HashSet<>(map.keySet());

			for (File file : files) {
				item = map.get(file);
				if (item == null) {
					item = new Pkpass();
					item.loadFromFile(file, true);
					isChanged = true;
					map.put(file, item);
				}

				oldkeys.remove(file);
			}

			if (!oldkeys.isEmpty()) {
				for (File file : oldkeys) {
					map.remove(file);
				}
				isChanged = true;
			}
		}

		if (isChanged) {
			dispatchDatasetChanged(null);
		}
	}

	/**
	 * Remove data from memory.
	 */
	public final void clear() {
		map.clear();
	}

	/**
	 * 
	 * @return Unmodifiable collection of files
	 */
	public final Collection<File> getFiles() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * 
	 * @return Unmodifiable collection of pkpasses
	 */
	public final Collection<Pkpass> getPkpasses() {
		return Collections.unmodifiableCollection(map.values());
	}

	/**
	 *
	 * Generate filename from organization name.
	 * 
	 * @param pkpass
	 * @return
	 */
	public String genInnerFileName(Pkpass pkpass) {
		File file = pkpass.getSourceFile();
//		String filename = file.getName();
//		if (filename.equals("pass.pkpass")) {
			String organization = pkpass.getOrganizationName();
			organization = organization.toLowerCase();
			organization = organization.replace(' ', '_');
			organization = organization.replace('\'', '_');
			organization = organization.replace('&', '_');
			organization = organization + "_"+ ((file!=null)?file.lastModified():"") + ".pkpass";
			return organization;
		//}

		//return filename;
	}

	// ====================================================================
	public boolean addPkpass(Pkpass pkpass) {
		File src = pkpass.getSourceFile();
		File dst = new File(dir, genInnerFileName(pkpass));
		boolean ret = false;

		synchronized (this) {
			ret = FileUtil.copy(src, dst);
		}

		if (ret) {
			pkpass.setSourceFile(dst);
			map.put(dst, pkpass);
			dispatchDatasetChanged(pkpass);
		}

		return ret;
	}

	public boolean addPkpass(Pkpass pkpass, InputStream is) {

		File dst = new File(dir, genInnerFileName(pkpass));
		boolean ret = false;

		synchronized (this) {
			ret = FileUtil.save(is, dst);
		}

		if (ret) {
			pkpass.setSourceFile(dst);
			map.put(dst, pkpass);
			dispatchDatasetChanged(pkpass);
		}

		return ret;
	}

	public boolean removePkpass(Pkpass pkpass) {

		boolean ret = false;
		File file = pkpass.getSourceFile();

		synchronized (this) {
			ret = file.delete();
		}

		if (ret) {
			map.remove(file);
			dispatchDatasetChanged(pkpass);
		}
		return ret;
	}

	public final boolean exist(Pkpass pkpass) {
		try {
			return pkpass.getSourceFile().getParentFile().equals(dir);
		}catch (Exception e){
		e.printStackTrace();
		}
		return false;
	}

	// ====================================================================
	public Pkpass[] toArray() {
		Pkpass[] ret;
		synchronized (map) {
			ret = new Pkpass[map.size()];
			ret = map.values().toArray(ret);
		}
		return ret;
	}

	/**
	 * 
	 * @param type
	 *            one of Pkpass.TYPE_xxx constant
	 * @return
	 */
	public void addToList(List<Pkpass> lst, String type) {

		switch (type) {
		case Pkpass.TYPE_BOARDING_PASS:
		case Pkpass.TYPE_COUPON:
		case Pkpass.TYPE_EVENT_TICKET:
		case Pkpass.TYPE_GENERIC:
		case Pkpass.TYPE_STORE_CARD:
			synchronized (map) {
				for (Pkpass pkpass : map.values()) {
					if (pkpass.getPkpassType().equals(type)) {
						lst.add(pkpass);
					}
				}
			}

			break;

		default:
			throw new IllegalArgumentException(
					"Argument type must be one of Pkpass.TYPE_xxx constants.");
		}
	}

	public final int getSize() {
		return map.size();
	}

	public final Pkpass getPkpass(String filename) {
		File file = new File(filename);
		return map.get(file);
	}

	public final Pkpass getPkpass(File file) {
		return map.get(file);
	}

	public static PkpassManager getSharedInstance() {
		if (sharedInstance == null) {
			synchronized (PkpassManager.class) {
				if (sharedInstance == null) {
					sharedInstance = new PkpassManager(new File(
							SpecFilesDirs.getFilesDir(), "pkpass"));
				}
			}
		}

		return sharedInstance;
	}

}
