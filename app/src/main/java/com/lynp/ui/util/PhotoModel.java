package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/30.
 */
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Aizaz
 */


public class PhotoModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;//删除图片时用
    private String originalPath = "";
    private boolean isChecked;
    private String url = "";
    private boolean fromNetwork;
    private boolean isCancel;

    public PhotoModel(String originalPath, boolean isChecked) {
        super();
        this.originalPath = originalPath;
        this.isChecked = isChecked;
    }

    public PhotoModel(String originalPath) {
        this.originalPath = originalPath;
    }

    public PhotoModel(boolean fromNetwork, String url) {
        this.fromNetwork = fromNetwork;
        this.url = url;
    }

    public PhotoModel() {
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public boolean isChecked() {
        return isChecked;
    }


//	@Override
//	public boolean equals(Object o) {
//		if (o.getClass() == getClass()) {
//			PhotoModel model = (PhotoModel) o;
//			if (this.getOriginalPath().equals(model.getOriginalPath())) {
//				return true;
//			}
//		}
//		return false;
//	}

    public boolean isCancel() {
        return isCancel;
    }

    public void setIsCancel(boolean isCancel) {
        this.isCancel = isCancel;
    }

    public String getUrl() {
        return url;
    }

    public boolean isFromNetwork() {
        return fromNetwork;
    }

    public void setFromNetwork(boolean fromNetwork) {
        this.fromNetwork = fromNetwork;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((originalPath == null) ? 0 : originalPath.hashCode());
        return result;
    }

	/* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//			return true;
//		}
//		if (obj == null) {
//			return false;
//		}
//		if (!(obj instanceof PhotoModel)) {
//			return false;
//		}
//		PhotoModel other = (PhotoModel) obj;
//		if (originalPath == null) {
//			if (other.originalPath != null) {
//				return false;
//			}
//		} else if (!originalPath.equals(other.originalPath)) {
//			return false;
//		}
//		return true;
//	}

    public static int removeByFilepath(List<PhotoModel> mPhotosList, String filePath) {
        int i = 0;
        Iterator<PhotoModel> it = mPhotosList.iterator();
        while (it.hasNext()) {
            PhotoModel model = it.next();
            String originalPath = model.getOriginalPath();
            if (originalPath.equals(filePath)) {
                it.remove();
                i++;
            }
        }
        return i;
    }

    public static int removeByUrl(List<PhotoModel> mPhotosList, String url) {
        int i = 0;
        Iterator<PhotoModel> it = mPhotosList.iterator();
        while (it.hasNext()) {
            PhotoModel model = it.next();
            String mUrl = model.getUrl();
            if (mUrl.equals(url)) {
                it.remove();
                i++;
            }
        }
        return i;
    }

    public static int getIndex(List<PhotoModel> mPhotosList, PhotoModel photoModel) {
        int i = -1;
        for (PhotoModel model : mPhotosList) {
            i++;
            if (photoModel.isFromNetwork()) {
                if (model.getUrl().equals(photoModel.getUrl())) {
                    break;
                }
            } else {
                if (model.getOriginalPath().equals(photoModel.getOriginalPath())) {
                    break;
                }
            }
        }
        return i;
    }

    @Override
    public String toString() {
        return "PhotoModel [originalPath=" + originalPath + ", isChecked="
                + isChecked + ", url=" + url + ", uploaded=" + fromNetwork + "]";
    }
}